package pe.utp.saludcerca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.saludcerca.model.Paciente;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    Optional<Paciente> findByDni(String dni);

    Optional<Paciente> findByEmail(String email);

    boolean existsByDni(String dni);
}
