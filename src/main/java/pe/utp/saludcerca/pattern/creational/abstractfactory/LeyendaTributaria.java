package pe.utp.saludcerca.pattern.creational.abstractfactory;

import java.math.BigDecimal;

/**
 * PATRÓN ABSTRACT FACTORY — rol: PRODUCTO ABSTRACTO B (AbstractProduct).
 *
 * Representa la leyenda tributaria que acompaña al comprobante. Debe ser
 * COHERENTE con el tipo de comprobante (una Boleta lleva IGV; un Recibo por
 * honorarios no, pero puede llevar retención de renta de 4ta categoría).
 */
public interface LeyendaTributaria {

    String texto(BigDecimal monto);
}
