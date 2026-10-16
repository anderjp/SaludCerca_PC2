package pe.utp.saludcerca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.saludcerca.model.Medico;
import pe.utp.saludcerca.model.enums.EstadoVerificacion;

import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Integer> {

    Optional<Medico> findByColegiatura(String colegiatura);

    /** Médicos según su estado de verificación (p. ej. los Pendientes para el admin). */
    List<Medico> findByEstadoVerificacion(EstadoVerificacion estado);

    /** Médicos de una especialidad (el "_" indica navegar a especialidad.id). */
    List<Medico> findByEspecialidad_Id(Integer idEspecialidad);

    List<Medico> findByEspecialidad_IdAndEstadoVerificacion(Integer idEspecialidad, EstadoVerificacion estado);
}
