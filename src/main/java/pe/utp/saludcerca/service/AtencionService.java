package pe.utp.saludcerca.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.utp.saludcerca.model.Cita;
import pe.utp.saludcerca.model.HistorialClinico;
import pe.utp.saludcerca.model.Notificacion;
import pe.utp.saludcerca.model.enums.EstadoCita;
import pe.utp.saludcerca.pattern.creational.factorymethod.EmailNotificacionFactory;
import pe.utp.saludcerca.pattern.creational.factorymethod.NotificacionFactory;
import pe.utp.saludcerca.repository.CitaRepository;
import pe.utp.saludcerca.repository.HistorialClinicoRepository;
import pe.utp.saludcerca.repository.NotificacionRepository;

/**
 * Finaliza la atención de una cita: guarda el historial clínico, marca la cita
 * como Completada y avisa al paciente (Factory Method), todo en una transacción.
 */
@Service
public class AtencionService {

    private final CitaRepository citaRepository;
    private final HistorialClinicoRepository historialRepository;
    private final NotificacionRepository notificacionRepository;

    public AtencionService(CitaRepository citaRepository, HistorialClinicoRepository historialRepository,
                           NotificacionRepository notificacionRepository) {
        this.citaRepository = citaRepository;
        this.historialRepository = historialRepository;
        this.notificacionRepository = notificacionRepository;
    }

    /** @return descripción de la notificación enviada al paciente */
    @Transactional
    public String finalizarAtencion(Cita cita, String diagnostico, String receta, String observaciones) {
        if (historialRepository.findByCita_Id(cita.getId()).isEmpty()) {
            historialRepository.save(new HistorialClinico(cita, diagnostico, receta, observaciones));
        }
        cita.setEstado(EstadoCita.Completada);
        citaRepository.save(cita);

        NotificacionFactory notificador = new EmailNotificacionFactory();
        String mensaje = "Tu diagnóstico y receta de la cita #" + cita.getId() + " ya están disponibles en SaludCerca.";
        String envio = notificador.notificarPaciente(cita.getPaciente(), mensaje);
        notificacionRepository.save(new Notificacion(cita.getPaciente(), "Diagnóstico - " + notificador.getCanal(), mensaje));
        return envio;
    }
}
