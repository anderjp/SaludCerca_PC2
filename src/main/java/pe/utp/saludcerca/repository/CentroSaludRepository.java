package pe.utp.saludcerca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.saludcerca.model.CentroSalud;

import java.util.List;
import java.util.Optional;

public interface CentroSaludRepository extends JpaRepository<CentroSalud, Integer> {

    List<CentroSalud> findByDistrito(String distrito);

    Optional<CentroSalud> findByNombre(String nombre);
}
