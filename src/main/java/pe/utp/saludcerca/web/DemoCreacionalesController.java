package pe.utp.saludcerca.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.utp.saludcerca.model.Especialidad;
import pe.utp.saludcerca.pattern.creational.abstractfactory.*;
import pe.utp.saludcerca.pattern.creational.builder.Reporte;
import pe.utp.saludcerca.pattern.creational.builder.ReporteAtencionesBuilder;
import pe.utp.saludcerca.pattern.creational.factorymethod.*;
import pe.utp.saludcerca.pattern.creational.prototype.PlantillaHistorial;
import pe.utp.saludcerca.pattern.creational.prototype.RegistroPlantillas;
import pe.utp.saludcerca.pattern.creational.singleton.ConfiguracionApp;
import pe.utp.saludcerca.repository.EspecialidadRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Controlador de DEMOSTRACIÓN de los patrones creacionales.
 * Cada endpoint invoca un patrón y devuelve texto plano para verlo en el
 * navegador durante la sustentación (Demo en vivo).
 *
 * Rutas (con la app corriendo en http://localhost:8082):
 *   /demo/creacionales          → índice
 *   /demo/singleton
 *   /demo/factory-method?tipo=email|sms&dest=...&msg=...
 *   /demo/prototype?clave=nutricion|psicologia
 *   /demo/abstract-factory?tipo=boleta|recibo&cliente=...&concepto=...&monto=...
 *   /demo/builder
 */
@RestController
public class DemoCreacionalesController {

    private static final String TXT = "text/plain;charset=UTF-8";

    private final RegistroPlantillas registroPlantillas;
    private final EspecialidadRepository especialidadRepository;

    public DemoCreacionalesController(RegistroPlantillas registroPlantillas,
                                      EspecialidadRepository especialidadRepository) {
        this.registroPlantillas = registroPlantillas;
        this.especialidadRepository = especialidadRepository;
    }

    @GetMapping(value = "/demo/creacionales", produces = TXT)
    public String indice() {
        return """
                SaludCerca · Demo de PATRONES CREACIONALES
                ==========================================
                1) Singleton        → /demo/singleton
                2) Factory Method   → /demo/factory-method?tipo=sms&dest=987654321&msg=Su cita fue confirmada
                3) Prototype        → /demo/prototype?clave=nutricion
                4) Abstract Factory → /demo/abstract-factory?tipo=boleta&cliente=Karla Effio&concepto=Consulta nutricional&monto=120
                5) Builder          → /demo/builder
                """;
    }

    // ---------- 1. SINGLETON ----------
    @GetMapping(value = "/demo/singleton", produces = TXT)
    public String singleton() {
        ConfiguracionApp c1 = ConfiguracionApp.getInstance();
        ConfiguracionApp c2 = ConfiguracionApp.getInstance();  // segunda llamada: misma instancia
        return "PATRÓN SINGLETON\n"
                + "================\n"
                + "getInstance() #1 → objeto " + Integer.toHexString(System.identityHashCode(c1)) + "\n"
                + "getInstance() #2 → objeto " + Integer.toHexString(System.identityHashCode(c2)) + "\n"
                + "¿Es la MISMA instancia?  " + (c1 == c2) + "\n"
                + "------------------------------------------\n"
                + "App     : " + c1.getNombreApp() + "\n"
                + "Moneda  : " + c1.getMoneda() + "\n"
                + "IGV     : " + c1.getIgv() + "\n"
                + "Recargo teleconsulta: " + c1.getMoneda() + " " + c1.getRecargoTeleconsulta() + "\n"
                + "Creada  : " + c1.getCreadaEn() + "\n";
    }

    // ---------- 2. FACTORY METHOD ----------
    @GetMapping(value = "/demo/factory-method", produces = TXT)
    public String factoryMethod(@RequestParam(defaultValue = "email") String tipo,
                                @RequestParam(defaultValue = "paciente@mail.com") String dest,
                                @RequestParam(defaultValue = "Su cita ha sido confirmada") String msg) {
        // El cliente elige la fábrica; NO conoce las clases concretas de canal.
        NotificacionFactory factory = switch (tipo.toLowerCase()) {
            case "sms" -> new SmsNotificacionFactory();
            default -> new EmailNotificacionFactory();
        };
        String resultado = factory.notificar(dest, msg);
        return "PATRÓN FACTORY METHOD\n"
                + "=====================\n"
                + "Fábrica usada: " + factory.getClass().getSimpleName() + "\n"
                + "Resultado    : " + resultado + "\n"
                + "\n(Cambia ?tipo=email o ?tipo=sms y el mismo código produce otro canal.)\n";
    }

    // ---------- 3. PROTOTYPE ----------
    @GetMapping(value = "/demo/prototype", produces = TXT)
    public String prototype(@RequestParam(defaultValue = "nutricion") String clave) {
        PlantillaHistorial clon = registroPlantillas.obtenerClon(clave);
        if (clon == null) {
            return "No existe la plantilla '" + clave + "'. Disponibles: "
                    + registroPlantillas.clavesDisponibles();
        }
        // Personalizamos el clon para un paciente concreto; el original NO cambia.
        clon.setObservacionesBase(clon.getObservacionesBase() + " — Paciente: Karla Effio");
        PlantillaHistorial otroClon = registroPlantillas.obtenerClon(clave);

        return "PATRÓN PROTOTYPE\n"
                + "================\n"
                + "CLON 1 (modificado para un paciente):\n" + clon + "\n\n"
                + "CLON 2 (nuevo, desde la misma plantilla base, intacto):\n" + otroClon + "\n\n"
                + "Cada clon es independiente: modificar uno no afecta al otro ni al original.\n";
    }

    // ---------- 4. ABSTRACT FACTORY ----------
    @GetMapping(value = "/demo/abstract-factory", produces = TXT)
    public String abstractFactory(@RequestParam(defaultValue = "boleta") String tipo,
                                  @RequestParam(defaultValue = "Karla Effio") String cliente,
                                  @RequestParam(defaultValue = "Consulta nutricional") String concepto,
                                  @RequestParam(defaultValue = "120.00") BigDecimal monto) {
        // Elegimos UNA familia; la fábrica garantiza que comprobante y leyenda son coherentes.
        ComprobanteFactory factory = switch (tipo.toLowerCase()) {
            case "recibo" -> new ReciboFactory();
            default -> new BoletaFactory();
        };
        Comprobante comprobante = factory.crearComprobante();
        LeyendaTributaria leyenda = factory.crearLeyenda();

        return "PATRÓN ABSTRACT FACTORY\n"
                + "=======================\n"
                + "Familia: " + factory.getClass().getSimpleName() + "\n"
                + "------------------------------------------\n"
                + comprobante.encabezado() + "\n"
                + comprobante.detalle(cliente, concepto, monto) + "\n"
                + "------------------------------------------\n"
                + leyenda.texto(monto) + "\n"
                + "\n(Cambia ?tipo=boleta o ?tipo=recibo: cada familia crea comprobante + leyenda coherentes.)\n";
    }

    // ---------- 5. BUILDER ----------
    @GetMapping(value = "/demo/builder", produces = TXT)
    public String builder() {
        // Construcción paso a paso; alimentamos el reporte con datos reales de la BD.
        ReporteAtencionesBuilder builder = new ReporteAtencionesBuilder()
                .titulo("Especialidades registradas en SaludCerca")
                .rango(LocalDate.now().minusMonths(1), LocalDate.now())
                .agruparPorRegion()
                .incluirTotales();

        for (Especialidad e : especialidadRepository.findAll()) {
            builder.agregarFila(e.getNombre()
                    + (e.isPermiteTeleconsulta() ? " (admite teleconsulta)" : " (solo presencial)"));
        }

        Reporte reporte = builder.construir();
        return "PATRÓN BUILDER\n==============\n\n" + reporte.generar();
    }
}
