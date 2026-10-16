package pe.utp.saludcerca.pattern.structural.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.utp.saludcerca.model.*;
import pe.utp.saludcerca.model.enums.EstadoCita;
import pe.utp.saludcerca.model.enums.EstadoVerificacion;
import pe.utp.saludcerca.model.enums.TipoAtencion;
import pe.utp.saludcerca.pattern.creational.factorymethod.EmailNotificacionFactory;
import pe.utp.saludcerca.pattern.creational.factorymethod.NotificacionFactory;
import pe.utp.saludcerca.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * PATRÓN FACADE (estructural) — rol: FACHADA.
 *
 * Intención: ofrecer UNA interfaz simple ({@link #agendarCita}) a un subsistema
 * complejo. El cliente (el controlador) solo llama a un método; por dentro, la
 * fachada coordina varios subsistemas:
 *   1) validación del médico y regla de teleconsulta,
 *   2) disponibilidad (DisponibilidadService),
 *   3) creación de la cita y enlace de video (GeneradorEnlaceVideollamada),
 *   4) registro del pago,
 *   5) notificación al paciente (reutiliza el Factory Method del módulo creacional).
 *
 * Esto corresponde al CitaService que la PC1 describía orquestando pago y
 * notificación, ahora resuelto con el patrón Facade.
 */
@Service
public class AgendamientoFacade {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm");

    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final CitaRepository citaRepository;
    private final PagoRepository pagoRepository;
    private final NotificacionRepository notificacionRepository;
    private final DisponibilidadService disponibilidadService;
    private final GeneradorEnlaceVideollamada generadorEnlace;

    public AgendamientoFacade(PacienteRepository pacienteRepository, MedicoRepository medicoRepository,
                              CitaRepository citaRepository, PagoRepository pagoRepository,
                              NotificacionRepository notificacionRepository,
                              DisponibilidadService disponibilidadService,
                              GeneradorEnlaceVideollamada generadorEnlace) {
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.citaRepository = citaRepository;
        this.pagoRepository = pagoRepository;
        this.notificacionRepository = notificacionRepository;
        this.disponibilidadService = disponibilidadService;
        this.generadorEnlace = generadorEnlace;
    }

    /** Versión simple: notifica por email y registra el pago como "Simulado". */
    @Transactional
    public ResultadoAgendamiento agendarCita(Integer idPaciente, Integer idMedico,
                                             LocalDateTime fechaHora, TipoAtencion tipo,
                                             BigDecimal monto) {
        return agendarCita(idPaciente, idMedico, fechaHora, tipo, monto, new EmailNotificacionFactory(), "Simulado");
    }

    /**
     * Agenda una cita completa con una sola llamada.
     *
     * @param notificador fábrica del canal elegido por el paciente (Factory Method)
     * @param metodoPago  descripción del pago simulado (p. ej. "Simulado - Boleta")
     */
    @Transactional
    public ResultadoAgendamiento agendarCita(Integer idPaciente, Integer idMedico,
                                             LocalDateTime fechaHora, TipoAtencion tipo,
                                             BigDecimal monto, NotificacionFactory notificador,
                                             String metodoPago) {
        // 1) Validaciones de existencia
        Optional<Paciente> oPaciente = pacienteRepository.findById(idPaciente);
        Optional<Medico> oMedico = medicoRepository.findById(idMedico);
        if (oPaciente.isEmpty()) {
            return ResultadoAgendamiento.error("El paciente " + idPaciente + " no existe");
        }
        if (oMedico.isEmpty()) {
            return ResultadoAgendamiento.error("El médico " + idMedico + " no existe");
        }
        Paciente paciente = oPaciente.get();
        Medico medico = oMedico.get();

        // 2) El médico debe estar verificado (aprobado por el administrador)
        if (medico.getEstadoVerificacion() != EstadoVerificacion.Aprobado) {
            return ResultadoAgendamiento.error("El médico aún no está aprobado por el administrador");
        }

        // 3) Regla de negocio: teleconsulta solo si la especialidad la permite
        if (tipo == TipoAtencion.Teleconsulta && !medico.getEspecialidad().isPermiteTeleconsulta()) {
            return ResultadoAgendamiento.error(
                    "La especialidad " + medico.getEspecialidad().getNombre() + " no permite teleconsulta");
        }

        // 4) La fecha debe ser futura y el horario estar libre
        if (fechaHora.isBefore(LocalDateTime.now())) {
            return ResultadoAgendamiento.error("No se puede agendar una cita en una fecha pasada");
        }
        if (!disponibilidadService.estaDisponible(idMedico, fechaHora)) {
            return ResultadoAgendamiento.error("El médico ya tiene una cita en ese horario");
        }

        // 5) Crear y guardar la cita (con enlace de video si es teleconsulta)
        Cita cita = new Cita(paciente, medico, fechaHora, tipo);
        cita.setEstado(EstadoCita.Confirmada);
        if (tipo == TipoAtencion.Teleconsulta) {
            cita.setEnlaceVideollamada(generadorEnlace.generar());
        }
        cita = citaRepository.save(cita);

        // 6) Registrar el pago (simulado, queda Pendiente)
        Pago pago = new Pago(cita, monto, metodoPago);
        pagoRepository.save(pago);

        // 7) Notificar al paciente por el canal elegido (FACTORY METHOD)
        String mensaje = "Su cita #" + cita.getId() + " con " + medico.getNombreCompleto()
                + " (" + medico.getEspecialidad().getNombre() + ") quedó confirmada para el "
                + fechaHora.format(FORMATO) + "."
                + (cita.getEnlaceVideollamada() != null ? " Enlace: " + cita.getEnlaceVideollamada() : "");
        String envio = notificador.notificarPaciente(paciente, mensaje);
        notificacionRepository.save(new Notificacion(paciente, "Agendamiento - " + notificador.getCanal(), mensaje));

        return ResultadoAgendamiento.ok(cita.getId(), cita.getEnlaceVideollamada(), envio);
    }
}
