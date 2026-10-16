package pe.utp.saludcerca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.saludcerca.model.Pago;
import pe.utp.saludcerca.model.enums.EstadoPago;

import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Integer> {

    /** Pago de una cita (relación 1 a 1). */
    Optional<Pago> findByCita_Id(Integer idCita);

    List<Pago> findByEstadoPago(EstadoPago estadoPago);
}
