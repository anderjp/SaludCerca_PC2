package pe.utp.saludcerca.pattern.creational.abstractfactory;

import pe.utp.saludcerca.pattern.creational.singleton.ConfiguracionApp;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ABSTRACT FACTORY — PRODUCTO CONCRETO B (familia Boleta).
 * La boleta SÍ está afecta a IGV: desglosa la operación gravada y el impuesto.
 * (Usa el IGV definido en el Singleton ConfiguracionApp → patrones trabajando juntos.)
 */
public class BoletaLeyenda implements LeyendaTributaria {

    @Override
    public String texto(BigDecimal total) {
        BigDecimal igv = ConfiguracionApp.getInstance().getIgv();                 // 0.18
        // El total incluye IGV: base = total / (1 + igv)
        BigDecimal base = total.divide(BigDecimal.ONE.add(igv), 2, RoundingMode.HALF_UP);
        BigDecimal montoIgv = total.subtract(base).setScale(2, RoundingMode.HALF_UP);
        int porcentaje = igv.multiply(new BigDecimal("100")).intValue();
        return "Op. Gravada: S/ " + base
                + "  |  IGV (" + porcentaje + "%): S/ " + montoIgv
                + "\nAfecto a IGV.";
    }
}
