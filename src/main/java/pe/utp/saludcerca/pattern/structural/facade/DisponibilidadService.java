package pe.utp.saludcerca.pattern.structural.facade;

import org.springframework.stereotype.Service;
import pe.utp.saludcerca.repository.CitaRepository;

import java.time.LocalDateTime;

/**
 * Subsistema usado por el Facade: verifica la disponibilidad del médico.
 * (El Facade coordina este y otros subsistemas para esconder la complejidad.)
 */
@Service
public class DisponibilidadService {

    private final CitaRepository citaRepository;

    public DisponibilidadService(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    public boolean estaDisponible(Integer idMedico, LocalDateTime fechaHora) {
        return !citaRepository.existsByMedico_IdAndFechaHora(idMedico, fechaHora);
    }
}
