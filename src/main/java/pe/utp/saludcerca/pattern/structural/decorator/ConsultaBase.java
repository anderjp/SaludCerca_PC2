package pe.utp.saludcerca.pattern.structural.decorator;

import java.math.BigDecimal;

/**
 * PATRÓN DECORATOR — rol: COMPONENTE CONCRETO (ConcreteComponent).
 * El costo base de una consulta, sin añadidos.
 */
public class ConsultaBase implements CostoConsulta {

    private final BigDecimal base;

    public ConsultaBase(BigDecimal base) {
        this.base = base;
    }

    @Override
    public BigDecimal monto() {
        return base;
    }

    @Override
    public String descripcion() {
        return "Consulta base";
    }
}
