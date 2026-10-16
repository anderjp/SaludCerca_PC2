package pe.utp.saludcerca.pattern.structural.decorator;

import pe.utp.saludcerca.pattern.creational.singleton.ConfiguracionApp;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * PATRÓN DECORATOR — rol: DECORADOR CONCRETO (ConcreteDecorator).
 * Añade el IGV (18%) al costo. Toma la tasa del Singleton ConfiguracionApp.
 */
public class IgvDecorator extends CostoDecorator {

    public IgvDecorator(CostoConsulta envuelto) {
        super(envuelto);
    }

    @Override
    public BigDecimal monto() {
        BigDecimal igv = ConfiguracionApp.getInstance().getIgv();
        BigDecimal impuesto = envuelto.monto().multiply(igv).setScale(2, RoundingMode.HALF_UP);
        return envuelto.monto().add(impuesto);
    }

    @Override
    public String descripcion() {
        return envuelto.descripcion() + " + IGV(18%)";
    }
}
