package pe.utp.saludcerca.web;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import pe.utp.saludcerca.model.*;
import pe.utp.saludcerca.model.enums.EstadoCita;
import pe.utp.saludcerca.model.enums.EstadoVerificacion;
import pe.utp.saludcerca.model.enums.TipoAtencion;
import pe.utp.saludcerca.pattern.creational.factorymethod.EmailNotificacionFactory;
import pe.utp.saludcerca.pattern.creational.factorymethod.NotificacionFactory;
import pe.utp.saludcerca.pattern.creational.factorymethod.SmsNotificacionFactory;
import pe.utp.saludcerca.pattern.creational.singleton.ConfiguracionApp;
import pe.utp.saludcerca.pattern.structural.adapter.ResultadoValidacion;
import pe.utp.saludcerca.pattern.structural.adapter.ValidadorIdentidad;
import pe.utp.saludcerca.pattern.structural.facade.AgendamientoFacade;
import pe.utp.saludcerca.pattern.structural.facade.ResultadoAgendamiento;
import pe.utp.saludcerca.repository.*;
import pe.utp.saludcerca.service.Cotizacion;
import pe.utp.saludcerca.service.CotizacionService;

import java.text.Normalizer;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Flujo del PACIENTE (prototipo de la PC1): Buscar → Agendar → Pagar → Confirmar.
 *
 * Patrones que se ejecutan en este flujo:
 *  - Singleton: tarifas y recargos desde ConfiguracionApp.
 *  - Adapter: validación del DNI con RENIEC (ValidadorIdentidad).
 *  - Decorator + Abstract Factory: costo de la cita y comprobante (CotizacionService).
 *  - Factory Method: canal de notificación elegido por el paciente.
 *  - Facade: AgendamientoFacade registra todo con una sola llamada.
 */
@Controller
@RequestMapping("/paciente")
public class PacienteController {

    private static final List<String> HORARIOS =
            List.of("09:00", "09:30", "10:00", "11:15", "14:00", "14:30", "16:00", "17:30");

    private final MedicoRepository medicoRepository;
    private final EspecialidadRepository especialidadRepository;
    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final PagoRepository pagoRepository;
    private final NotificacionRepository notificacionRepository;
    private final ValidadorIdentidad validadorIdentidad;
    private final CotizacionService cotizacionService;
    private final AgendamientoFacade agendamientoFacade;

    public PacienteController(MedicoRepository medicoRepository, EspecialidadRepository especialidadRepository,
                              CitaRepository citaRepository, PacienteRepository pacienteRepository,
                              PagoRepository pagoRepository, NotificacionRepository notificacionRepository,
                              ValidadorIdentidad validadorIdentidad, CotizacionService cotizacionService,
                              AgendamientoFacade agendamientoFacade) {
        this.medicoRepository = medicoRepository;
        this.especialidadRepository = especialidadRepository;
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.pagoRepository = pagoRepository;
        this.notificacionRepository = notificacionRepository;
        this.validadorIdentidad = validadorIdentidad;
        this.cotizacionService = cotizacionService;
        this.agendamientoFacade = agendamientoFacade;
    }

    // ===================== 1. Buscar especialista =====================
    @GetMapping
    public String buscar(@RequestParam(required = false) String q,
                         @RequestParam(required = false) Integer especialidad,
                         @RequestParam(defaultValue = "false") boolean tele,
                         Model model) {
        String filtro = normalizar(q);
        List<Medico> medicos = medicoRepository.findByEstadoVerificacion(EstadoVerificacion.Aprobado).stream()
                .filter(m -> especialidad == null || m.getEspecialidad().getId().equals(especialidad))
                .filter(m -> !tele || m.getEspecialidad().isPermiteTeleconsulta())
                .filter(m -> filtro.isEmpty() || normalizar(m.getNombreCompleto() + " " + m.getEspecialidad().getNombre()
                        + " " + m.getCentroSalud().getNombre() + " " + m.getCentroSalud().getDistrito()).contains(filtro))
                .toList();

        model.addAttribute("medicos", medicos);
        model.addAttribute("especialidades", especialidadRepository.findAll());
        model.addAttribute("q", q);
        model.addAttribute("especialidadSel", especialidad);
        model.addAttribute("tele", tele);
        model.addAttribute("cfg", ConfiguracionApp.getInstance());
        return "paciente/buscar";
    }

    // ===================== 2. Agendar (día y horario) =====================
    @GetMapping("/medico/{id}")
    public String agendar(@PathVariable Integer id,
                          @RequestParam(required = false) String tipo,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                          Model model) {
        Medico medico = medicoAprobado(id);
        if (medico == null) {
            return "redirect:/paciente";
        }
        List<LocalDate> dias = proximosDias();
        LocalDate dia = (fecha != null && dias.contains(fecha)) ? fecha : dias.get(0);
        boolean permiteTele = medico.getEspecialidad().isPermiteTeleconsulta();

        Set<LocalDateTime> ocupados = citaRepository.findByMedico_IdOrderByFechaHoraDesc(id).stream()
                .filter(c -> c.getEstado() != EstadoCita.Cancelada)
                .map(Cita::getFechaHora)
                .collect(Collectors.toSet());
        List<Horario> horarios = HORARIOS.stream()
                .map(h -> new Horario(h, ocupados.contains(dia.atTime(LocalTime.parse(h)))))
                .toList();

        model.addAttribute("medico", medico);
        model.addAttribute("dias", dias);
        model.addAttribute("dia", dia);
        model.addAttribute("horarios", horarios);
        model.addAttribute("permiteTele", permiteTele);
        model.addAttribute("tipoSel", tipoPermitido(medico, tipo).name());
        model.addAttribute("cfg", ConfiguracionApp.getInstance());
        return "paciente/agendar";
    }

    // ===================== 3. Confirmar y pagar (vista previa) =====================
    @GetMapping("/confirmar")
    public String confirmar(@RequestParam Integer idMedico,
                            @RequestParam(defaultValue = "Presencial") String tipo,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                            @RequestParam String hora,
                            @RequestParam(defaultValue = "false") boolean seguro,
                            @RequestParam(defaultValue = CotizacionService.BOLETA) String comprobante,
                            @RequestParam(defaultValue = "email") String canal,
                            @RequestParam(required = false) String dni,
                            Model model) {
        Medico medico = medicoAprobado(idMedico);
        if (medico == null || !HORARIOS.contains(hora)) {
            return "redirect:/paciente";
        }
        TipoAtencion tipoAtencion = tipoPermitido(medico, tipo);

        // ADAPTER: el sistema valida el DNI con RENIEC a través de su propia interfaz
        ResultadoValidacion validacion = null;
        Paciente registrado = null;
        if (dni != null && !dni.isBlank()) {
            validacion = validadorIdentidad.validar(dni.trim());
            if (validacion.isValido()) {
                registrado = pacienteRepository.findByDni(dni.trim()).orElse(null);
            }
        }
        String cliente = (validacion != null && validacion.isValido()) ? validacion.getNombreCompleto().trim() : "Paciente";

        // DECORATOR + ABSTRACT FACTORY (dentro de CotizacionService)
        Cotizacion cotizacion = cotizacionService.cotizar(medico, tipoAtencion, seguro, comprobante, cliente);

        model.addAttribute("medico", medico);
        model.addAttribute("tipo", tipoAtencion.name());
        model.addAttribute("fecha", fecha);
        model.addAttribute("hora", hora);
        model.addAttribute("seguro", seguro);
        model.addAttribute("comprobante", CotizacionService.RECIBO.equalsIgnoreCase(comprobante)
                ? CotizacionService.RECIBO : CotizacionService.BOLETA);
        model.addAttribute("canal", "sms".equalsIgnoreCase(canal) ? "sms" : "email");
        model.addAttribute("dni", dni);
        model.addAttribute("validacion", validacion);
        model.addAttribute("registrado", registrado);
        model.addAttribute("cotizacion", cotizacion);
        model.addAttribute("cfg", ConfiguracionApp.getInstance());
        return "paciente/confirmar";
    }

    // ===================== 4. Agendar la cita (FACADE) =====================
    @PostMapping("/agendar")
    public String registrar(@RequestParam Integer idMedico,
                            @RequestParam(defaultValue = "Presencial") String tipo,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                            @RequestParam String hora,
                            @RequestParam(defaultValue = "false") boolean seguro,
                            @RequestParam(defaultValue = CotizacionService.BOLETA) String comprobante,
                            @RequestParam(defaultValue = "email") String canal,
                            @RequestParam(required = false) String dni,
                            @RequestParam(required = false) String email,
                            @RequestParam(required = false) String telefono,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaNacimiento,
                            RedirectAttributes ra) {
        String volver = UriComponentsBuilder.fromPath("/paciente/confirmar")
                .queryParam("idMedico", idMedico).queryParam("tipo", tipo).queryParam("fecha", fecha)
                .queryParam("hora", hora).queryParam("seguro", seguro).queryParam("comprobante", comprobante)
                .queryParam("canal", canal).queryParam("dni", dni == null ? "" : dni)
                .encode().toUriString();

        Medico medico = medicoAprobado(idMedico);
        if (medico == null || !HORARIOS.contains(hora)) {
            return "redirect:/paciente";
        }

        // ADAPTER: identidad validada con RENIEC antes de registrar
        ResultadoValidacion v = (dni == null || dni.isBlank()) ? null : validadorIdentidad.validar(dni.trim());
        if (v == null || !v.isValido()) {
            ra.addFlashAttribute("error", "Valida un DNI registrado en RENIEC antes de confirmar.");
            return "redirect:" + volver;
        }
        Paciente paciente = pacienteRepository.findByDni(dni.trim()).orElse(null);
        if (paciente == null) {
            String error = validarNuevoPaciente(email, fechaNacimiento);
            if (error != null) {
                ra.addFlashAttribute("error", error);
                return "redirect:" + volver;
            }
            // Paciente nuevo: nombres y apellidos vienen de RENIEC (vía el Adapter)
            paciente = pacienteRepository.save(new Paciente(dni.trim(), v.getNombres(), v.getApellidos(),
                    fechaNacimiento, email.trim(), (telefono == null || telefono.isBlank()) ? null : telefono.trim()));
        }

        TipoAtencion tipoAtencion = tipoPermitido(medico, tipo);
        // DECORATOR + ABSTRACT FACTORY: total y comprobante
        Cotizacion cotizacion = cotizacionService.cotizar(medico, tipoAtencion, seguro, comprobante,
                paciente.getNombreCompleto());
        // FACTORY METHOD: el paciente eligió el canal de notificación
        NotificacionFactory notificador = "sms".equalsIgnoreCase(canal)
                ? new SmsNotificacionFactory() : new EmailNotificacionFactory();
        // FACADE: una sola llamada agenda la cita completa
        ResultadoAgendamiento resultado = agendamientoFacade.agendarCita(paciente.getId(), medico.getId(),
                fecha.atTime(LocalTime.parse(hora)), tipoAtencion, cotizacion.getTotal(), notificador,
                "Simulado - " + cotizacion.getTipoComprobante());

        if (!resultado.isExito()) {
            ra.addFlashAttribute("error", resultado.getMensaje());
            return "redirect:" + volver;
        }
        ra.addFlashAttribute("envio", resultado.getNotificacion());
        return "redirect:/paciente/cita/" + resultado.getIdCita();
    }

    // ===================== 5. Cita confirmada =====================
    @GetMapping("/cita/{id}")
    public String cita(@PathVariable Integer id, Model model) {
        Cita cita = citaRepository.findById(id).orElse(null);
        if (cita == null) {
            return "redirect:/paciente";
        }
        Pago pago = pagoRepository.findByCita_Id(id).orElse(null);
        if (pago != null) {
            String tipo = (pago.getMetodoPago() != null && pago.getMetodoPago().toLowerCase().contains("recibo"))
                    ? CotizacionService.RECIBO : CotizacionService.BOLETA;
            model.addAttribute("comprobante", cotizacionService.comprobante(tipo,
                    cita.getPaciente().getNombreCompleto(),
                    "Consulta de " + cita.getMedico().getEspecialidad().getNombre(), pago.getMonto()));
        }
        Notificacion ultima = notificacionRepository
                .findByPaciente_IdOrderByFechaEnvioDesc(cita.getPaciente().getId())
                .stream().findFirst().orElse(null);

        model.addAttribute("cita", cita);
        model.addAttribute("pago", pago);
        model.addAttribute("ultima", ultima);
        return "paciente/cita";
    }

    // ===================== utilidades =====================
    private Medico medicoAprobado(Integer id) {
        return medicoRepository.findById(id)
                .filter(m -> m.getEstadoVerificacion() == EstadoVerificacion.Aprobado)
                .orElse(null);
    }

    /** La teleconsulta solo se permite si la especialidad del médico la admite. */
    private TipoAtencion tipoPermitido(Medico medico, String tipo) {
        return ("Teleconsulta".equalsIgnoreCase(tipo) && medico.getEspecialidad().isPermiteTeleconsulta())
                ? TipoAtencion.Teleconsulta : TipoAtencion.Presencial;
    }

    /** Próximos 6 días hábiles (desde mañana, sin domingos). */
    private static List<LocalDate> proximosDias() {
        List<LocalDate> dias = new ArrayList<>();
        LocalDate d = LocalDate.now().plusDays(1);
        while (dias.size() < 6) {
            if (d.getDayOfWeek() != DayOfWeek.SUNDAY) {
                dias.add(d);
            }
            d = d.plusDays(1);
        }
        return dias;
    }

    private String validarNuevoPaciente(String email, LocalDate fechaNacimiento) {
        if (email == null || !email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return "Ingresa un correo válido para registrarte.";
        }
        if (fechaNacimiento == null || !fechaNacimiento.isBefore(LocalDate.now())) {
            return "Ingresa tu fecha de nacimiento.";
        }
        if (pacienteRepository.findByEmail(email.trim()).isPresent()) {
            return "Ese correo ya está registrado por otro paciente.";
        }
        return null;
    }

    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase().trim();
    }
}
