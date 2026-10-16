package pe.utp.saludcerca.pattern.creational.abstractfactory;

import pe.utp.saludcerca.pattern.creational.singleton.ConfiguracionApp;

import java.math.BigDecimal;

/**
 * ABSTRACT FACTORY — PRODUCTO CONCRETO A (familia Boleta).
 */
public class BoletaComprobante implements Comprobante {

    @Override
    public String encabezado() {
        return "BOLETA DE VENTA ELECTRÓNICA\nSerie: B001";
    }

    @Override
    public String detalle(String cliente, String concepto, BigDecimal monto) {
        String moneda = ConfiguracionApp.getInstance().getMoneda();
        return "Cliente : " + cliente + "\n"
                + "Concepto: " + concepto + "\n"
                + "Importe : " + moneda + " " + monto;
    }
}
