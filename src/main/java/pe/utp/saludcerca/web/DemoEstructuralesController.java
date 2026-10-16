package pe.utp.saludcerca.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.utp.saludcerca.model.enums.TipoAtencion;
import pe.utp.saludcerca.pattern.structural.adapter.ReniecApiExterna;
import pe.utp.saludcerca.pattern.structural.adapter.ReniecValidadorAdapter;
import pe.utp.saludcerca.pattern.structural.adapter.ResultadoValidacion;
import pe.utp.saludcerca.pattern.structural.adapter.ValidadorIdentidad;
import pe.utp.saludcerca.pattern.structural.composite.ComponenteServicio;
import pe.utp.saludcerca.pattern.structural.composite.PaqueteServicios;
import pe.utp.saludcerca.pattern.structural.composite.ServicioMedico;
import pe.utp.saludcerca.pattern.structural.decorator.*;
import pe.utp.saludcerca.pattern.structural.facade.AgendamientoFacade;
import pe.utp.saludcerca.pattern.structural.facade.ResultadoAgendamiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Controlador de DEMOSTRACIÓN de los patrones estructurales.
 *
 * Rutas (app en http://localhost:8082):
 *   /demo/estructurales
 *   /demo/adapter?dni=45231890
 *   /demo/facade?idPaciente=1&idMedico=1&tipo=Teleconsulta&monto=80
 *   /demo/decorator?base=80&teleconsulta=true&seguro=true
 *   /demo/composite
 */
@RestController
public class DemoEstructuralesController {

    private static final String TXT = "text/plain;charset=UTF-8";

    private final AgendamientoFacade agendamientoFacade;

    public DemoEstructuralesController(AgendamientoFacade agendamientoFacade) {
        this.agendamientoFacade = agendamientoFacade;
    }

    @GetMapping(value = "/demo/estructurales", produces = TXT)
    public String indice() {
        return """
                SaludCerca · Demo de PATRONES ESTRUCTURALES
                ===========================================
                1) Adapter   → /demo/adapter?dni=45231890   (prueba también dni=00000000)
                2) Facade    → /demo/facade?idPaciente=1&idMedico=1&tipo=Teleconsulta&monto=80
                3) Decorator → /demo/decorator?base=80&teleconsulta=true&seguro=true
                4) Composite → /demo/composite
                """;
    }

    // ---------- 1. ADAPTER ----------
    @GetMapping(value = "/demo/adapter", produces = TXT)
    public String adapter(@RequestParam(defaultValue = "45231890") String dni) {
        // Nuestro sistema solo conoce la interfaz ValidadorIdentidad.
        // El adaptador traduce por dentro al servicio externo RENIEC.
        ValidadorIdentidad validador = new ReniecValidadorAdapter(new ReniecApiExterna());
        ResultadoValidacion r = validador.validar(dni);

        return "PATRÓN ADAPTER\n"
                + "==============\n"
                + "DNI consultado: " + dni + "\n"
                + "¿Válido?      : " + r.isValido() + "\n"
                + (r.isValido() ? "Nombre        : " + r.getNombreCompleto() + "\n" : "")
                + "Mensaje       : " + r.getMensaje() + "\n"
                + "\n(El servicio externo RENIEC devuelve 'APELLIDOS|NOMBRES'; el Adapter lo\n"
                + " convierte al formato de SaludCerca sin que el resto del sistema se entere.)\n";
    }

    // ---------- 2. FACADE ----------
    @GetMapping(value = "/demo/facade", produces = TXT)
    public String facade(@RequestParam(defaultValue = "1") Integer idPaciente,
                         @RequestParam(defaultValue = "1") Integer idMedico,
                         @RequestParam(defaultValue = "Teleconsulta") String tipo,
                         @RequestParam(defaultValue = "80.00") BigDecimal monto) {
        TipoAtencion tipoAtencion = "presencial".equalsIgnoreCase(tipo)
                ? TipoAtencion.Presencial : TipoAtencion.Teleconsulta;
        // fecha futura única por llamada (para no chocar con la disponibilidad)
        LocalDateTime fechaHora = LocalDateTime.now().plusDays(1).withNano(0);

        ResultadoAgendamiento resultado =
                agendamientoFacade.agendarCita(idPaciente, idMedico, fechaHora, tipoAtencion, monto);

        return "PATRÓN FACADE (agendamiento de cita)\n"
                + "====================================\n"
                + "Con UNA sola llamada (agendarCita) la fachada coordinó: validación,\n"
                + "disponibilidad, creación de cita, enlace de video, pago y notificación.\n"
                + "------------------------------------------\n"
                + resultado.resumen() + "\n";
    }

    // ---------- 3. DECORATOR ----------
    @GetMapping(value = "/demo/decorator", produces = TXT)
    public String decorator(@RequestParam(defaultValue = "80.00") BigDecimal base,
                            @RequestParam(defaultValue = "true") boolean teleconsulta,
                            @RequestParam(defaultValue = "true") boolean seguro) {
        // Partimos del costo base y le "envolvemos" responsabilidades dinámicamente.
        CostoConsulta costo = new ConsultaBase(base);
        if (teleconsulta) {
            costo = new RecargoTeleconsultaDecorator(costo);
        }
        if (seguro) {
            costo = new SeguroDecorator(costo, new BigDecimal("10.00"));
        }
        costo = new IgvDecorator(costo);   // el IGV se aplica al final, sobre el subtotal

        return "PATRÓN DECORATOR (costo de la consulta)\n"
                + "=======================================\n"
                + "Base: S/ " + base + "  | teleconsulta=" + teleconsulta + "  | seguro=" + seguro + "\n"
                + "------------------------------------------\n"
                + "Incluye: " + costo.descripcion() + "\n"
                + "TOTAL  : S/ " + costo.monto() + "\n"
                + "\n(Cada añadido es un decorador independiente; se apilan sin tocar la clase base.)\n";
    }

    // ---------- 4. COMPOSITE ----------
    @GetMapping(value = "/demo/composite", produces = TXT)
    public String composite() {
        // Hojas (servicios individuales)
        ServicioMedico consultaGeneral = new ServicioMedico("Consulta medicina general", new BigDecimal("80"));
        ServicioMedico analisis = new ServicioMedico("Análisis de sangre", new BigDecimal("60"));
        ServicioMedico ekg = new ServicioMedico("Electrocardiograma", new BigDecimal("90"));
        ServicioMedico nutricion = new ServicioMedico("Consulta nutricional", new BigDecimal("70"));

        // Composición: un paquete que agrupa servicios
        PaqueteServicios chequeo = new PaqueteServicios("Chequeo integral")
                .agregar(consultaGeneral)
                .agregar(analisis)
                .agregar(ekg);

        // Composición de composición: un combo que contiene el paquete anterior + un servicio
        PaqueteServicios combo = new PaqueteServicios("Combo familiar")
                .agregar(chequeo)
                .agregar(nutricion);

        ComponenteServicio raiz = combo;   // se trata igual una hoja que un compuesto
        return "PATRÓN COMPOSITE (catálogo de servicios/paquetes)\n"
                + "=================================================\n"
                + raiz.mostrar(0) + "\n"
                + "\nPrecio total del combo: S/ " + raiz.precio() + "\n"
                + "(El precio de un paquete es la suma recursiva de sus componentes.)\n";
    }
}
