package pe.utp.saludcerca.pattern.structural.decorator;

import java.math.BigDecimal;

/**
 * PATRÓN DECORATOR — rol: DECORADOR CONCRETO (ConcreteDecorator).
 * Añade un seguro opcional (monto configurable) al costo de la consulta.
 */
public class SeguroDecorator extends CostoDecorator {

    private final BigDecimal costoSeguro;

    public SeguroDecorator(CostoConsulta envuelto, BigDecimal costoSeguro) {
        super(envuelto);
        this.costoSeguro = costoSeguro;
    }

    @Override
    public BigDecimal monto() {
        return envuelto.monto().add(costoSeguro);
    }

    @Override
    public String descripcion() {
        return envuelto.descripcion() + " + seguro";
    }
}
