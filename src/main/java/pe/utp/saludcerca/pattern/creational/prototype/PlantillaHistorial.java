package pe.utp.saludcerca.pattern.creational.prototype;

/**
 * PATRÓN PROTOTYPE — rol: PROTOTIPO CONCRETO (ConcretePrototype).
 *
 * Problema que resuelve en SaludCerca: un médico reutiliza "plantillas" de
 * historial/receta (p. ej. una plantilla de nutrición) para cada paciente.
 * En lugar de volver a escribir todo, CLONA la plantilla base y solo ajusta los
 * datos del paciente. Clonar es más barato y evita modificar el original.
 */
public class PlantillaHistorial implements Prototipo<PlantillaHistorial> {

    private String nombrePlantilla;
    private String diagnosticoBase;
    private String recetaBase;
    private String observacionesBase;

    public PlantillaHistorial(String nombrePlantilla, String diagnosticoBase,
                              String recetaBase, String observacionesBase) {
        this.nombrePlantilla = nombrePlantilla;
        this.diagnosticoBase = diagnosticoBase;
        this.recetaBase = recetaBase;
        this.observacionesBase = observacionesBase;
    }

    /** Constructor de copia: base de una clonación segura (copia campo por campo). */
    public PlantillaHistorial(PlantillaHistorial original) {
        this.nombrePlantilla = original.nombrePlantilla;
        this.diagnosticoBase = original.diagnosticoBase;
        this.recetaBase = original.recetaBase;
        this.observacionesBase = original.observacionesBase;
    }

    @Override
    public PlantillaHistorial clonar() {
        return new PlantillaHistorial(this);
    }

    public String getNombrePlantilla() {
        return nombrePlantilla;
    }

    public void setNombrePlantilla(String nombrePlantilla) {
        this.nombrePlantilla = nombrePlantilla;
    }

    public String getDiagnosticoBase() {
        return diagnosticoBase;
    }

    public void setDiagnosticoBase(String diagnosticoBase) {
        this.diagnosticoBase = diagnosticoBase;
    }

    public String getRecetaBase() {
        return recetaBase;
    }

    public void setRecetaBase(String recetaBase) {
        this.recetaBase = recetaBase;
    }

    public String getObservacionesBase() {
        return observacionesBase;
    }

    public void setObservacionesBase(String observacionesBase) {
        this.observacionesBase = observacionesBase;
    }

    @Override
    public String toString() {
        return "Plantilla '" + nombrePlantilla + "'"
                + "\n  Diagnóstico: " + diagnosticoBase
                + "\n  Receta: " + recetaBase
                + "\n  Observaciones: " + observacionesBase;
    }
}
