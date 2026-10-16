package pe.utp.saludcerca.service;

import org.springframework.stereotype.Service;
import pe.utp.saludcerca.model.Cita;
import pe.utp.saludcerca.model.Especialidad;
import pe.utp.saludcerca.model.Pago;
import pe.utp.saludcerca.model.enums.EstadoCita;
import pe.utp.saludcerca.model.enums.TipoAtencion;
import pe.utp.saludcerca.pattern.creational.builder.Reporte;
import pe.utp.saludcerca.pattern.creational.builder.ReporteAtencionesBuilder;
import pe.utp.saludcerca.repository.CitaRepository;
import pe.utp.saludcerca.repository.EspecialidadRepository;
import pe.utp.saludcerca.repository.PagoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Genera el reporte de atenciones usando el patrón BUILDER: según los filtros
 * elegidos por el administrador, el reporte se construye paso a paso
 * (título, rango, especialidad, agrupación por región, totales y filas).
 */
@Service
public class ReporteService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private final CitaRepository citaRepository;
    private final PagoRepository pagoRepository;
    private final EspecialidadRepository especialidadRepository;

    public ReporteService(CitaRepository citaRepository, PagoRepository pagoRepository,
                          EspecialidadRepository especialidadRepository) {
        this.citaRepository = citaRepository;
        this.pagoRepository = pagoRepository;
        this.especialidadRepository = especialidadRepository;
    }

    /**
     * @param idEspecialidad especialidad a filtrar (null = todas)
     * @param dias           citas registradas en los últimos N días (0 = todo el historial)
     * @param agrupar        agrupar las filas por región (distrito del centro de salud)
     */
    public ResultadoReporte generar(Integer idEspecialidad, int dias, boolean agrupar) {
        Especialidad especialidad = idEspecialidad == null ? null
                : especialidadRepository.findById(idEspecialidad).orElse(null);
        LocalDate hasta = LocalDate.now();
        LocalDate desde = dias > 0 ? hasta.minusDays(dias) : null;

        List<Cita> citas = citaRepository.findAll().stream()
                .filter(c -> especialidad == null || c.getMedico().getEspecialidad().getId().equals(especialidad.getId()))
                .filter(c -> desde == null || c.getCreadoEn() == null || !c.getCreadoEn().toLocalDate().isBefore(desde))
                .sorted(Comparator.comparing(Cita::getFechaHora))
                .toList();

        // ---- BUILDER: construcción paso a paso del reporte ----
        ReporteAtencionesBuilder builder = new ReporteAtencionesBuilder()
                .titulo("Reporte de atenciones" + (especialidad != null ? " — " + especialidad.getNombre() : ""))
                .filtrarEspecialidad(especialidad != null ? especialidad.getNombre() : null)
                .incluirTotales();
        if (desde != null) {
            builder.rango(desde, hasta);
        }
        if (agrupar) {
            // Agrupado: las filas se ordenan por región y la región va como primera columna
            builder.agruparPorRegion();
            Map<String, List<Cita>> porRegion = citas.stream().collect(Collectors.groupingBy(
                    c -> c.getMedico().getCentroSalud().getDistrito(), TreeMap::new, Collectors.toList()));
            porRegion.forEach((region, lista) ->
                    lista.forEach(c -> builder.agregarFila(region + " | " + fila(c))));
        } else {
            citas.forEach(c -> builder.agregarFila(fila(c)));
        }
        Reporte reporte = builder.construir();

        // ---- Indicadores para la vista ----
        long completadas = citas.stream().filter(c -> c.getEstado() == EstadoCita.Completada).count();
        long teleconsultas = citas.stream().filter(c -> c.getTipoAtencion() == TipoAtencion.Teleconsulta).count();
        BigDecimal monto = citas.stream()
                .map(c -> pagoRepository.findByCita_Id(c.getId()).map(Pago::getMonto).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Long> porEspecialidad = new LinkedHashMap<>();
        especialidadRepository.findAll().forEach(e -> porEspecialidad.put(e.getNombre(), 0L));
        citas.forEach(c -> porEspecialidad.merge(c.getMedico().getEspecialidad().getNombre(), 1L, Long::sum));

        return new ResultadoReporte(reporte, citas.size(), completadas, teleconsultas, monto, porEspecialidad);
    }

    private String fila(Cita c) {
        return c.getFechaHora().format(FMT) + " · " + c.getMedico().getEspecialidad().getNombre()
                + " · " + c.getMedico().getNombreCompleto() + " · " + c.getPaciente().getNombreCompleto()
                + " · " + c.getTipoAtencion() + " · " + c.getEstado();
    }
}
