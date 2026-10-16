package pe.utp.saludcerca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.saludcerca.model.Notificacion;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    List<Notificacion> findByPaciente_IdOrderByFechaEnvioDesc(Integer idPaciente);

    List<Notificacion> findByPaciente_IdAndLeidoFalse(Integer idPaciente);
}
