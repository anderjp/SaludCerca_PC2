package pe.utp.saludcerca.pattern.creational.builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * PATRÓN BUILDER — rol: PRODUCTO (Product).
 *
 * Objeto complejo (un reporte de atenciones) con muchas partes opcionales:
 * rango de fechas, filtro por especialidad, agrupación, totales y filas de
 * datos. Se construye paso a paso con {@link ReporteAtencionesBuilder}.
 */
public class Reporte {

    private final String titulo;
    private final LocalDate desde;
    private final LocalDate hasta;
    private final String especialidad;     // null = todas
    private final boolean agruparPorRegion;
    private final boolean incluirTotales;
    private final List<String> filas;
    private final LocalDateTime generadoEn;

    /** Se construye a partir del builder (mismo paquete). */
    Reporte(ReporteAtencionesBuilder b) {
        this.titulo = b.titulo;
        this.desde = b.desde;
        this.hasta = b.hasta;
        this.especialidad = b.especialidad;
        this.agruparPorRegion = b.agruparPorRegion;
        this.incluirTotales = b.incluirTotales;
        this.filas = b.filas;
        this.generadoEn = LocalDateTime.now();
    }

    /** Renderiza el reporte como texto según las opciones elegidas. */
    public String generar() {
        StringBuilder sb = new StringBuilder();
        sb.append("══════════════════════════════════════════\n");
        sb.append("  ").append(titulo).append("\n");
        sb.append("══════════════════════════════════════════\n");
        sb.append("Generado: ").append(generadoEn.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        if (desde != null && hasta != null) {
            sb.append("Periodo: ").append(desde).append(" a ").append(hasta).append("\n");
        }
        sb.append("Especialidad: ").append(especialidad == null ? "Todas" : especialidad).append("\n");
        sb.append("Agrupado por región: ").append(agruparPorRegion ? "Sí" : "No").append("\n");
        sb.append("------------------------------------------\n");
        if (filas != null && !filas.isEmpty()) {
            filas.forEach(f -> sb.append("• ").append(f).append("\n"));
            if (incluirTotales) {
                sb.append("------------------------------------------\n");
                sb.append("TOTAL de registros: ").append(filas.size()).append("\n");
            }
        } else {
            sb.append("(sin datos)\n");
        }
        return sb.toString();
    }

    public String getTitulo() {
        return titulo;
    }

    public List<String> getFilas() {
        return filas;
    }

    public LocalDate getDesde() {
        return desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public boolean isAgruparPorRegion() {
        return agruparPorRegion;
    }

    public boolean isIncluirTotales() {
        return incluirTotales;
    }

    public LocalDateTime getGeneradoEn() {
        return generadoEn;
    }
}
