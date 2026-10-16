package pe.utp.saludcerca.pattern.creational.abstractfactory;

import java.math.BigDecimal;

/**
 * PATRÓN ABSTRACT FACTORY — rol: PRODUCTO ABSTRACTO A (AbstractProduct).
 *
 * Representa el documento del comprobante (su encabezado y su detalle).
 * Cada familia (Boleta, Recibo) tiene su propia versión.
 */
public interface Comprobante {

    String encabezado();

    String detalle(String cliente, String concepto, BigDecimal monto);
}
