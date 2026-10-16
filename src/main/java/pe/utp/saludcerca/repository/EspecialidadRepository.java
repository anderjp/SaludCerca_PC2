package pe.utp.saludcerca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.saludcerca.model.Especialidad;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de especialidades. JpaRepository ya aporta el CRUD completo
 * (save, findById, findAll, delete, etc.); aquí solo añadimos buscadores propios.
 */
public interface EspecialidadRepository extends JpaRepository<Especialidad, Integer> {

    Optional<Especialidad> findByNombre(String nombre);

    /** Especialidades que admiten teleconsulta (configurado por el administrador). */
    List<Especialidad> findByPermiteTeleconsultaTrue();
}
