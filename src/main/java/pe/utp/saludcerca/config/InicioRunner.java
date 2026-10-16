package pe.utp.saludcerca.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pe.utp.saludcerca.repository.*;

/**
 * Componente de verificación que se ejecuta al arrancar la aplicación.
 * Comprueba que la conexión a la base de datos y el mapeo entidad–tabla
 * funcionan, mostrando en consola cuántos registros hay en cada tabla y un
 * par de ejemplos (incluyendo relaciones como médico → especialidad).
 */
@Component
@Order(2)   // se ejecuta después de DatosDemo, para mostrar los datos ya cargados
public class InicioRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InicioRunner.class);

    private final EspecialidadRepository especialidadRepo;
    private final CentroSaludRepository centroRepo;
    private final PacienteRepository pacienteRepo;
    private final MedicoRepository medicoRepo;
    private final CitaRepository citaRepo;
    private final HistorialClinicoRepository historialRepo;
    private final PagoRepository pagoRepo;
    private final NotificacionRepository notificacionRepo;

    // Inyección de dependencias por constructor (buena práctica: favorece SOLID y las pruebas)
    public InicioRunner(EspecialidadRepository especialidadRepo, CentroSaludRepository centroRepo,
                        PacienteRepository pacienteRepo, MedicoRepository medicoRepo,
                        CitaRepository citaRepo, HistorialClinicoRepository historialRepo,
                        PagoRepository pagoRepo, NotificacionRepository notificacionRepo) {
        this.especialidadRepo = especialidadRepo;
        this.centroRepo = centroRepo;
        this.pacienteRepo = pacienteRepo;
        this.medicoRepo = medicoRepo;
        this.citaRepo = citaRepo;
        this.historialRepo = historialRepo;
        this.pagoRepo = pagoRepo;
        this.notificacionRepo = notificacionRepo;
    }

    @Override
    public void run(String... args) {
        log.info("============== SaludCerca · verificación de datos (arranque) ==============");
        log.info("Especialidades : {}", especialidadRepo.count());
        log.info("Centros salud  : {}", centroRepo.count());
        log.info("Pacientes      : {}", pacienteRepo.count());
        log.info("Médicos        : {}", medicoRepo.count());
        log.info("Citas          : {}", citaRepo.count());
        log.info("Historiales    : {}", historialRepo.count());
        log.info("Pagos          : {}", pagoRepo.count());
        log.info("Notificaciones : {}", notificacionRepo.count());
        log.info("---------------------------------------------------------------------------");

        // Prueba de mapeo de columnas y relaciones (hidratación de entidades)
        especialidadRepo.findAll().forEach(e ->
                log.info("Especialidad: {} (teleconsulta: {})", e.getNombre(), e.isPermiteTeleconsulta()));
        medicoRepo.findAll().forEach(m ->
                log.info("Médico: {} - {} | centro: {} | estado: {}",
                        m.getNombreCompleto(), m.getEspecialidad().getNombre(),
                        m.getCentroSalud().getNombre(), m.getEstadoVerificacion()));
        log.info("===========================================================================");
    }
}
