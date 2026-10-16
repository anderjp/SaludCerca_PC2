package pe.utp.saludcerca.service;

import pe.utp.saludcerca.pattern.creational.builder.Reporte;

import java.math.BigDecimal;
import java.util.Map;

/** Datos del reporte de atenciones para la vista (el Reporte lo arma el Builder). */
public class ResultadoReporte {

    private final Reporte reporte;
    private final int total;
    private final long completadas;
    private final long teleconsultas;
    private final BigDecimal montoPagos;
    private final Map<String, Long> porEspecialidad;
    private final long maximo;

    public ResultadoReporte(Reporte reporte, int total, long completadas, long teleconsultas,
                            BigDecimal montoPagos, Map<String, Long> porEspecialidad) {
        this.reporte = reporte;
        this.total = total;
        this.completadas = completadas;
        this.teleconsultas = teleconsultas;
        this.montoPagos = montoPagos;
        this.porEspecialidad = porEspecialidad;
        this.maximo = porEspecialidad.values().stream().mapToLong(Long::longValue).max().orElse(0);
    }

    public Reporte getReporte() {
        return reporte;
    }

    public int getTotal() {
        return total;
    }

    public long getCompletadas() {
        return completadas;
    }

    public long getTeleconsultas() {
        return teleconsultas;
    }

    public BigDecimal getMontoPagos() {
        return montoPagos;
    }

    public Map<String, Long> getPorEspecialidad() {
        return porEspecialidad;
    }

    public long getMaximo() {
        return maximo;
    }
}
