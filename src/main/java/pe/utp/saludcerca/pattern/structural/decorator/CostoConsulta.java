package pe.utp.saludcerca.pattern.structural.decorator;

import java.math.BigDecimal;

/**
 * PATRÓN DECORATOR — rol: COMPONENTE (Component).
 *
 * Abstrae el costo de una consulta: su monto y la descripción de lo que incluye.
 * Tanto la consulta base como los "añadidos" (decoradores) implementan esta
 * misma interfaz, por eso se pueden envolver unos a otros.
 */
public interface CostoConsulta {

    BigDecimal monto();

    String descripcion();
}
