package pe.utp.saludcerca.pattern.creational.abstractfactory;

/**
 * PATRÓN ABSTRACT FACTORY — rol: FÁBRICA ABSTRACTA (AbstractFactory).
 *
 * Intención: proveer una interfaz para crear FAMILIAS de objetos relacionados
 * (aquí: el comprobante y su leyenda tributaria) sin especificar sus clases
 * concretas. Cada fábrica concreta (Boleta, Recibo) crea una familia coherente.
 */
public interface ComprobanteFactory {

    Comprobante crearComprobante();

    LeyendaTributaria crearLeyenda();
}
