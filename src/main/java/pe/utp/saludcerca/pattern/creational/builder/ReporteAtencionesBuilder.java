package pe.utp.saludcerca.pattern.creational.builder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * PATRÓN BUILDER — rol: CONSTRUCTOR (Builder).
 *
 * Intención: separar la construcción de un objeto complejo de su representación,
 * permitiendo crearlo paso a paso. Cada método devuelve {@code this} para poder
 * encadenar llamadas (API fluida) y solo {@link #construir()} entrega el objeto.
 *
 * Ejemplo de uso:
 * <pre>
 * Reporte r = new ReporteAtencionesBuilder()
 *         .titulo("Atenciones por especialidad")
 *         .rango(LocalDate.now().minusMonths(1), LocalDate.now())
 *         .filtrarEspecialidad("Nutricion Clinica")
 *         .agruparPorRegion()
 *         .incluirTotales()
 *         .agregarFila("Nutricion Clinica - 12 atenciones")
 *         .construir();
 * </pre>
 */
public class ReporteAtencionesBuilder {

    // Campos accesibles por Reporte (mismo paquete). Con valores por defecto.
    String titulo = "Reporte de atenciones";
    LocalDate desde;
    LocalDate hasta;
    String especialidad;
    boolean agruparPorRegion = false;
    boolean incluirTotales = false;
    List<String> filas = new ArrayList<>();

    public ReporteAtencionesBuilder titulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    public ReporteAtencionesBuilder rango(LocalDate desde, LocalDate hasta) {
        this.desde = desde;
        this.hasta = hasta;
        return this;
    }

    public ReporteAtencionesBuilder filtrarEspecialidad(String especialidad) {
        this.especialidad = especialidad;
        return this;
    }

    public ReporteAtencionesBuilder agruparPorRegion() {
        this.agruparPorRegion = true;
        return this;
    }

    public ReporteAtencionesBuilder incluirTotales() {
        this.incluirTotales = true;
        return this;
    }

    public ReporteAtencionesBuilder agregarFila(String fila) {
        this.filas.add(fila);
        return this;
    }

    /** Entrega el objeto Reporte ya construido con las opciones elegidas. */
    public Reporte construir() {
        return new Reporte(this);
    }
}
