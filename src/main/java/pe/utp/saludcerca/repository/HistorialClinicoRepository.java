package pe.utp.saludcerca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.saludcerca.model.HistorialClinico;

import java.util.Optional;

public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Integer> {

    /** Historial de una cita (relación 1 a 1). */
    Optional<HistorialClinico> findByCita_Id(Integer idCita);
}
