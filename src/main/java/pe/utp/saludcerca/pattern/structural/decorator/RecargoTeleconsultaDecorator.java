package pe.utp.saludcerca.pattern.structural.decorator;

import pe.utp.saludcerca.pattern.creational.singleton.ConfiguracionApp;

import java.math.BigDecimal;

/**
 * PATRÓN DECORATOR — rol: DECORADOR CONCRETO (ConcreteDecorator).
 * Añade el recargo por teleconsulta (tomado del Singleton ConfiguracionApp).
 */
public class RecargoTeleconsultaDecorator extends CostoDecorator {

    public RecargoTeleconsultaDecorator(CostoConsulta envuelto) {
        super(envuelto);
    }

    @Override
    public BigDecimal monto() {
        BigDecimal recargo = ConfiguracionApp.getInstance().getRecargoTeleconsulta();
        return envuelto.monto().add(recargo);
    }

    @Override
    public String descripcion() {
        return envuelto.descripcion() + " + recargo teleconsulta";
    }
}
