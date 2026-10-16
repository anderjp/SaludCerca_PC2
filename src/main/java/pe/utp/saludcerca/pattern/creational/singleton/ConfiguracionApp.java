package pe.utp.saludcerca.pattern.creational.singleton;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PATRÓN SINGLETON (creacional).
 *
 * Intención: garantizar que exista UNA sola instancia de la configuración
 * central de la aplicación y ofrecer un punto de acceso global a ella.
 *
 * Problema que resuelve en SaludCerca: valores como el IGV, la moneda, el
 * recargo por teleconsulta o el tarifario de consultas deben ser únicos y
 * consistentes en todo el sistema. Si cada módulo creara su propia
 * configuración, podrían desincronizarse (p. ej. dos precios distintos).
 *
 * Implementación: constructor privado + instancia estática + getInstance() con
 * "double-checked locking" (seguro ante múltiples hilos). Es inmutable.
 *
 * Nota: Spring también maneja beans como singletons, pero aquí implementamos el
 * patrón GoF clásico de forma explícita, que es lo que pide el curso.
 */
public final class ConfiguracionApp {

    // 'volatile' asegura visibilidad entre hilos de la instancia creada
    private static volatile ConfiguracionApp instancia;

    private final String nombreApp = "SaludCerca";
    private final String moneda = "S/";
    private final BigDecimal igv = new BigDecimal("0.18");               // 18% (comprobantes e impuestos)
    private final BigDecimal recargoTeleconsulta = new BigDecimal("5.00");  // usado por el Decorator de costo
    private final BigDecimal costoSeguro = new BigDecimal("10.00");         // seguro opcional de atención
    private final BigDecimal tarifaPorDefecto = new BigDecimal("75.00");
    private final Map<String, BigDecimal> tarifario;   // tarifa de la consulta por especialidad
    private final Map<String, BigDecimal> indiceTarifas; // mismo tarifario, con clave normalizada
    private final LocalDateTime creadaEn = LocalDateTime.now();

    // Constructor PRIVADO: nadie puede hacer "new ConfiguracionApp()" desde fuera
    private ConfiguracionApp() {
        Map<String, BigDecimal> t = new LinkedHashMap<>();
        t.put("Nutrición Clínica", new BigDecimal("80.00"));
        t.put("Psicología", new BigDecimal("90.00"));
        t.put("Odontología", new BigDecimal("65.00"));
        t.put("Medicina General", new BigDecimal("70.00"));
        this.tarifario = Collections.unmodifiableMap(t);

        Map<String, BigDecimal> indice = new LinkedHashMap<>();
        t.forEach((nombre, precio) -> indice.put(normalizar(nombre), precio));
        this.indiceTarifas = Collections.unmodifiableMap(indice);
    }

    /** Único punto de acceso a la instancia. La crea solo la primera vez. */
    public static ConfiguracionApp getInstance() {
        if (instancia == null) {                       // 1ª comprobación (sin bloqueo)
            synchronized (ConfiguracionApp.class) {
                if (instancia == null) {               // 2ª comprobación (con bloqueo)
                    instancia = new ConfiguracionApp();
                }
            }
        }
        return instancia;
    }

    /** Tarifa de la consulta para una especialidad (ignora tildes y mayúsculas). */
    public BigDecimal getTarifa(String especialidad) {
        if (especialidad == null) {
            return tarifaPorDefecto;
        }
        return indiceTarifas.getOrDefault(normalizar(especialidad), tarifaPorDefecto);
    }

    private static String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.trim().toLowerCase();
    }

    public String getNombreApp() {
        return nombreApp;
    }

    public String getMoneda() {
        return moneda;
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public BigDecimal getRecargoTeleconsulta() {
        return recargoTeleconsulta;
    }

    public BigDecimal getCostoSeguro() {
        return costoSeguro;
    }

    public BigDecimal getTarifaPorDefecto() {
        return tarifaPorDefecto;
    }

    public Map<String, BigDecimal> getTarifario() {
        return tarifario;
    }

    public LocalDateTime getCreadaEn() {
        return creadaEn;
    }
}
