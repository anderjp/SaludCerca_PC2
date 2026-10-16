package pe.utp.saludcerca.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.utp.saludcerca.model.*;
import pe.utp.saludcerca.model.enums.EstadoCita;
import pe.utp.saludcerca.model.enums.EstadoVerificacion;
import pe.utp.saludcerca.model.enums.TipoAtencion;
import pe.utp.saludcerca.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Carga datos de demostración la primera vez que arranca la aplicación
 * (es idempotente: si los datos ya existen, no hace nada).
 *
 * Complementa el script de la PC1 con más médicos (incluidos dos "Pendientes"
 * para el flujo de verificación del administrador) y algunas citas en la
 * agenda de la Dra. Castañeda para el flujo del médico.
 */
@Component
@Order(1)
public class DatosDemo implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosDemo.class);

    private final EspecialidadRepository especialidadRepo;
    private final CentroSaludRepository centroRepo;
    private final MedicoRepository medicoRepo;
    private final PacienteRepository pacienteRepo;
    private final CitaRepository citaRepo;

    public DatosDemo(EspecialidadRepository especialidadRepo, CentroSaludRepository centroRepo,
                     MedicoRepository medicoRepo, PacienteRepository pacienteRepo, CitaRepository citaRepo) {
        this.especialidadRepo = especialidadRepo;
        this.centroRepo = centroRepo;
        this.medicoRepo = medicoRepo;
        this.pacienteRepo = pacienteRepo;
        this.citaRepo = citaRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Especialidad nutricion = especialidadRepo.findByNombre("Nutricion Clinica").orElse(null);
        Especialidad psicologia = especialidadRepo.findByNombre("Psicologia").orElse(null);
        Especialidad odontologia = especialidadRepo.findByNombre("Odontologia").orElse(null);
        Especialidad general = especialidadRepo.findByNombre("Medicina General").orElse(null);
        if (nutricion == null || psicologia == null || odontologia == null || general == null) {
            log.warn("No se encontraron las especialidades del script de la PC1; se omiten los datos de demo.");
            return;
        }

        CentroSalud sanFelipe = centro("Clinica San Felipe", "San Isidro", "014712000");
        CentroSalud olivos = centro("Centro Medico Los Olivos", "Los Olivos", "014870000");
        CentroSalud surco = centro("Clinica Surco Salud", "Santiago de Surco", "014445566");

        int nuevos = 0;
        nuevos += medico("CMP51022", "Renzo", "Huamán Torres", "rhuaman@saludcerca.pe", "998877661", nutricion, surco, EstadoVerificacion.Aprobado);
        nuevos += medico("CMP49875", "Ana Belén", "Rojas Paz", "arojas@saludcerca.pe", "998877662", odontologia, olivos, EstadoVerificacion.Aprobado);
        nuevos += medico("CMP44310", "Jorge", "Salazar Ríos", "jsalazar@saludcerca.pe", "998877663", general, surco, EstadoVerificacion.Aprobado);
        nuevos += medico("CMP39021", "Fiorella", "Ninanya Cruz", "fninanya@saludcerca.pe", "998877664", psicologia, sanFelipe, EstadoVerificacion.Pendiente);
        nuevos += medico("CMP52890", "Sebastián", "Vidal Paredes", "svidal@saludcerca.pe", "998877665", general, sanFelipe, EstadoVerificacion.Pendiente);

        // Citas de ejemplo en la agenda de la Dra. Castañeda (solo la primera vez)
        Medico castaneda = medicoRepo.findByColegiatura("CMP48213").orElse(null);
        if (castaneda != null) {
            LocalDate manana = LocalDate.now().plusDays(1);
            nuevos += pacienteConCita("41239876", "Diego", "Ramos Paredes", LocalDate.of(1991, 5, 20),
                    "diego.ramos@mail.com", "987111222", castaneda, manana.atTime(11, 15), EstadoCita.Confirmada);
            nuevos += pacienteConCita("43567812", "Sofía", "Quiroz Salinas", LocalDate.of(1984, 9, 2),
                    "sofia.quiroz@mail.com", "987333444", castaneda, manana.atTime(16, 0), EstadoCita.Pendiente);
        }
        if (nuevos > 0) {
            log.info("Datos de demo cargados: {} registros nuevos.", nuevos);
        }
    }

    private CentroSalud centro(String nombre, String distrito, String telefono) {
        return centroRepo.findByNombre(nombre)
                .orElseGet(() -> centroRepo.save(new CentroSalud(nombre, distrito, telefono)));
    }

    private int medico(String colegiatura, String nombres, String apellidos, String email, String telefono,
                       Especialidad especialidad, CentroSalud centro, EstadoVerificacion estado) {
        if (medicoRepo.findByColegiatura(colegiatura).isPresent()) {
            return 0;
        }
        Medico m = new Medico(colegiatura, nombres, apellidos, email, telefono, especialidad, centro);
        m.setEstadoVerificacion(estado);
        medicoRepo.save(m);
        return 1;
    }

    private int pacienteConCita(String dni, String nombres, String apellidos, LocalDate nacimiento,
                                String email, String telefono, Medico medico, LocalDateTime fechaHora,
                                EstadoCita estado) {
        if (pacienteRepo.findByDni(dni).isPresent()) {
            return 0;
        }
        Paciente p = pacienteRepo.save(new Paciente(dni, nombres, apellidos, nacimiento, email, telefono));
        Cita c = new Cita(p, medico, fechaHora, TipoAtencion.Presencial);
        c.setEstado(estado);
        citaRepo.save(c);
        return 2;
    }
}
