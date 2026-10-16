package pe.utp.saludcerca.service;

import java.math.BigDecimal;

/** Una línea del desglose de costo (lo que suma cada decorador). */
public class LineaCosto {

    private final String concepto;
    private final BigDecimal monto;

    public LineaCosto(String concepto, BigDecimal monto) {
        this.concepto = concepto;
        this.monto = monto;
    }

    public String getConcepto() {
        return concepto;
    }

    public BigDecimal getMonto() {
        return monto;
    }
}
