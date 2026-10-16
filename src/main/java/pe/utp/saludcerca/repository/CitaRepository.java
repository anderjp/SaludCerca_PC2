package pe.utp.saludcerca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.saludcerca.model.Cita;
import pe.utp.saludcerca.model.enums.EstadoCita;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Integer> {

    List<Cita> findByPaciente_IdOrderByFechaHoraDesc(Integer idPaciente);

    List<Cita> findByMedico_IdOrderByFechaHoraDesc(Integer idMedico);

    List<Cita> findByEstado(EstadoCita estado);

    /** ¿El médico ya tiene una cita a esa fecha y hora exacta? (control de disponibilidad) */
    boolean existsByMedico_IdAndFechaHora(Integer idMedico, LocalDateTime fechaHora);
}
