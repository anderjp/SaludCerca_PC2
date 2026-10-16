package pe.utp.saludcerca.pattern.creational.prototype;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * PATRÓN PROTOTYPE — rol: REGISTRO DE PROTOTIPOS (Prototype Registry).
 *
 * Guarda plantillas "base" ya configuradas y entrega SIEMPRE una CLONACIÓN de
 * ellas, para que quien la use (el médico al registrar un diagnóstico) pueda
 * modificarla sin alterar el original.
 */
@Component
public class RegistroPlantillas {

    private final Map<String, PlantillaHistorial> plantillas = new LinkedHashMap<>();

    public RegistroPlantillas() {
        // Plantillas base precargadas (el original nunca se entrega directamente)
        plantillas.put("nutricion", new PlantillaHistorial(
                "Control nutricional",
                "Evaluación nutricional de rutina. Evolución favorable del plan alimenticio.",
                "Dieta hipocalórica 1500 kcal/día; 2 L de agua diarios.",
                "Reevaluar en 30 días."));
        plantillas.put("psicologia", new PlantillaHistorial(
                "Sesión psicológica",
                "Evaluación del estado anímico y manejo del estrés.",
                "Ejercicios de respiración; registro diario de emociones.",
                "Continuar terapia semanal."));
        plantillas.put("medicina-general", new PlantillaHistorial(
                "Consulta de medicina general",
                "Paciente en buen estado general. Funciones vitales dentro de lo normal.",
                "Paracetamol 500 mg cada 8 horas por 3 días si hay malestar.",
                "Control en caso de persistir los síntomas."));
        plantillas.put("odontologia", new PlantillaHistorial(
                "Control odontológico",
                "Revisión dental de rutina. Se observa placa bacteriana leve.",
                "Cepillado 3 veces al día con pasta fluorada; uso de hilo dental.",
                "Profilaxis programada en 6 meses."));
    }

    /** Devuelve una COPIA de la plantilla pedida (o null si no existe). */
    public PlantillaHistorial obtenerClon(String clave) {
        PlantillaHistorial base = plantillas.get(clave);
        return (base == null) ? null : base.clonar();
    }

    public Set<String> clavesDisponibles() {
        return plantillas.keySet();
    }

    /** Clave → nombre visible de cada plantilla (para el selector del médico). */
    public Map<String, String> opciones() {
        Map<String, String> opciones = new LinkedHashMap<>();
        plantillas.forEach((clave, p) -> opciones.put(clave, p.getNombrePlantilla()));
        return opciones;
    }

    /** Plantilla sugerida según la especialidad del médico. */
    public String claveSugerida(String especialidad) {
        if (especialidad == null) {
            return "medicina-general";
        }
        String e = Normalizer.normalize(especialidad, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase();
        if (e.contains("nutri")) return "nutricion";
        if (e.contains("psico")) return "psicologia";
        if (e.contains("odonto")) return "odontologia";
        return "medicina-general";
    }
}
