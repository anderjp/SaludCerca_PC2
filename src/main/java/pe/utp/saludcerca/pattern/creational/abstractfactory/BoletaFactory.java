package pe.utp.saludcerca.pattern.creational.abstractfactory;

/**
 * ABSTRACT FACTORY — FÁBRICA CONCRETA (familia Boleta).
 * Crea una familia coherente: BoletaComprobante + BoletaLeyenda (con IGV).
 */
public class BoletaFactory implements ComprobanteFactory {

    @Override
    public Comprobante crearComprobante() {
        return new BoletaComprobante();
    }

    @Override
    public LeyendaTributaria crearLeyenda() {
        return new BoletaLeyenda();
    }
}
