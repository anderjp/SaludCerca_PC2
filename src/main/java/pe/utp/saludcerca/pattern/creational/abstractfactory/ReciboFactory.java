package pe.utp.saludcerca.pattern.creational.abstractfactory;

/**
 * ABSTRACT FACTORY — FÁBRICA CONCRETA (familia Recibo).
 * Crea una familia coherente: ReciboComprobante + ReciboLeyenda (sin IGV).
 */
public class ReciboFactory implements ComprobanteFactory {

    @Override
    public Comprobante crearComprobante() {
        return new ReciboComprobante();
    }

    @Override
    public LeyendaTributaria crearLeyenda() {
        return new ReciboLeyenda();
    }
}
