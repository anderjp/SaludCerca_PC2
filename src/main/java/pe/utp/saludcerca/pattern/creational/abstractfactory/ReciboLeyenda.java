package pe.utp.saludcerca.pattern.creational.abstractfactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ABSTRACT FACTORY — PRODUCTO CONCRETO B (familia Recibo).
 * El recibo por honorarios NO lleva IGV, pero si supera S/ 1500 aplica
 * retención de renta de 4ta categoría (8%).
 */
public class ReciboLeyenda implements LeyendaTributaria {

    private static final BigDecimal UMBRAL_RETENCION = new BigDecimal("1500.00");
    private static final BigDecimal TASA_RETENCION = new BigDecimal("0.08");

    @Override
    public String texto(BigDecimal monto) {
        StringBuilder sb = new StringBuilder("Renta de 4ta categoría - No afecto a IGV.");
        if (monto.compareTo(UMBRAL_RETENCION) > 0) {
            BigDecimal retencion = monto.multiply(TASA_RETENCION).setScale(2, RoundingMode.HALF_UP);
            sb.append("\nRetención 8%: S/ ").append(retencion)
              .append("  |  Neto a pagar: S/ ").append(monto.subtract(retencion).setScale(2, RoundingMode.HALF_UP));
        }
        return sb.toString();
    }
}
