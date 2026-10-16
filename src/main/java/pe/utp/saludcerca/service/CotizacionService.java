package pe.utp.saludcerca.service;

import org.springframework.stereotype.Service;
import pe.utp.saludcerca.model.Medico;
import pe.utp.saludcerca.model.enums.TipoAtencion;
import pe.utp.saludcerca.pattern.creational.abstractfactory.*;
import pe.utp.saludcerca.pattern.creational.singleton.ConfiguracionApp;
import pe.utp.saludcerca.pattern.structural.decorator.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Calcula el costo de una cita y arma su comprobante. Aquí se ven trabajando
 * juntos tres patrones:
 *  - SINGLETON: la tarifa, el recargo, el seguro y el IGV salen de ConfiguracionApp.
 *  - DECORATOR: el costo base se "envuelve" con cada añadido elegido.
 *  - ABSTRACT FACTORY: la familia de comprobante (Boleta o Recibo) se elige una vez
 *    y crea un comprobante y una leyenda tributaria coherentes entre sí.
 */
@Service
public class CotizacionService {

    public static final String BOLETA = "boleta";
    public static final String RECIBO = "recibo";

    public Cotizacion cotizar(Medico medico, TipoAtencion tipo, boolean conSeguro,
                              String tipoComprobante, String cliente) {
        ConfiguracionApp cfg = ConfiguracionApp.getInstance();
        String especialidad = medico.getEspecialidad().getNombre();
        boolean esBoleta = !RECIBO.equalsIgnoreCase(tipoComprobante);

        List<LineaCosto> lineas = new ArrayList<>();
        BigDecimal tarifa = cfg.getTarifa(especialidad);
        CostoConsulta costo = new ConsultaBase(tarifa);
        lineas.add(new LineaCosto("Consulta de " + especialidad, tarifa));

        if (tipo == TipoAtencion.Teleconsulta) {
            costo = envolver(costo, new RecargoTeleconsultaDecorator(costo), "Recargo por teleconsulta", lineas);
        }
        if (conSeguro) {
            costo = envolver(costo, new SeguroDecorator(costo, cfg.getCostoSeguro()), "Seguro de atención", lineas);
        }
        if (esBoleta) {
            // La boleta está afecta a IGV; el recibo por honorarios no.
            costo = envolver(costo, new IgvDecorator(costo), "IGV (18%)", lineas);
        }

        BigDecimal total = costo.monto().setScale(2, RoundingMode.HALF_UP);
        ComprobanteVista comprobante = comprobante(tipoComprobante, cliente, "Consulta de " + especialidad, total);
        return new Cotizacion(lineas, total, costo.descripcion(), comprobante);
    }

    /** Arma el comprobante con la familia elegida (Abstract Factory). */
    public ComprobanteVista comprobante(String tipoComprobante, String cliente, String concepto, BigDecimal total) {
        boolean esBoleta = !RECIBO.equalsIgnoreCase(tipoComprobante);
        ComprobanteFactory fabrica = esBoleta ? new BoletaFactory() : new ReciboFactory();
        Comprobante comprobante = fabrica.crearComprobante();
        LeyendaTributaria leyenda = fabrica.crearLeyenda();
        return new ComprobanteVista(esBoleta ? "Boleta" : "Recibo",
                comprobante.encabezado(), comprobante.detalle(cliente, concepto, total), leyenda.texto(total));
    }

    /** Aplica un decorador y registra cuánto sumó, para mostrar el desglose. */
    private CostoConsulta envolver(CostoConsulta anterior, CostoConsulta decorado, String concepto,
                                   List<LineaCosto> lineas) {
        BigDecimal agregado = decorado.monto().subtract(anterior.monto()).setScale(2, RoundingMode.HALF_UP);
        lineas.add(new LineaCosto(concepto, agregado));
        return decorado;
    }
}
