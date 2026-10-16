package pe.utp.saludcerca.pattern.creational.abstractfactory;

import pe.utp.saludcerca.pattern.creational.singleton.ConfiguracionApp;

import java.math.BigDecimal;

/**
 * ABSTRACT FACTORY — PRODUCTO CONCRETO A (familia Recibo).
 */
public class ReciboComprobante implements Comprobante {

    @Override
    public String encabezado() {
        return "RECIBO POR HONORARIOS ELECTRÓNICO\nSerie: E001";
    }

    @Override
    public String detalle(String cliente, String concepto, BigDecimal monto) {
        String moneda = ConfiguracionApp.getInstance().getMoneda();
        return "Usuario  : " + cliente + "\n"
                + "Servicio : " + concepto + "\n"
                + "Honorario: " + moneda + " " + monto;
    }
}
