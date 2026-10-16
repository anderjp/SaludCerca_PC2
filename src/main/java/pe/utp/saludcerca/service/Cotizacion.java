package pe.utp.saludcerca.service;

import java.math.BigDecimal;
import java.util.List;

/** Resultado de cotizar una cita: desglose (Decorator) y comprobante (Abstract Factory). */
public class Cotizacion {

    private final List<LineaCosto> lineas;
    private final BigDecimal total;
    private final String descripcion;
    private final ComprobanteVista comprobante;

    public Cotizacion(List<LineaCosto> lineas, BigDecimal total, String descripcion, ComprobanteVista comprobante) {
        this.lineas = lineas;
        this.total = total;
        this.descripcion = descripcion;
        this.comprobante = comprobante;
    }

    public List<LineaCosto> getLineas() {
        return lineas;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public ComprobanteVista getComprobante() {
        return comprobante;
    }

    public String getTipoComprobante() {
        return comprobante.getTipo();
    }
}
