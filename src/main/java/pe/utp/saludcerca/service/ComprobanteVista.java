package pe.utp.saludcerca.service;

/** Comprobante armado con la familia elegida (Abstract Factory), listo para mostrar. */
public class ComprobanteVista {

    private final String tipo;
    private final String encabezado;
    private final String detalle;
    private final String leyenda;

    public ComprobanteVista(String tipo, String encabezado, String detalle, String leyenda) {
        this.tipo = tipo;
        this.encabezado = encabezado;
        this.detalle = detalle;
        this.leyenda = leyenda;
    }

    public String getTipo() {
        return tipo;
    }

    public String getEncabezado() {
        return encabezado;
    }

    public String getDetalle() {
        return detalle;
    }

    public String getLeyenda() {
        return leyenda;
    }

    /** Texto completo del comprobante. */
    public String getTexto() {
        return encabezado + "\n--------------------------------\n" + detalle
                + "\n--------------------------------\n" + leyenda;
    }
}
