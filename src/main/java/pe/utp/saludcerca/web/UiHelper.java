package pe.utp.saludcerca.web;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Period;

/**
 * Utilidades de presentación usadas desde las plantillas Thymeleaf
 * (por ejemplo: ${@ui.iniciales(m.nombres, m.apellidos)}).
 */
@Component("ui")
public class UiHelper {

    private static final String[] DIAS = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
    private static final String[] DIAS_LARGOS = {"lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"};
    private static final String[] MESES = {"enero", "febrero", "marzo", "abril", "mayo", "junio", "julio",
            "agosto", "setiembre", "octubre", "noviembre", "diciembre"};
    private static final String[] PALETA = {"#5B4B8A", "#125952", "#B97C1E", "#0B3E39"};

    public String iniciales(String nombres, String apellidos) {
        String a = (nombres == null || nombres.isBlank()) ? "" : nombres.trim().substring(0, 1);
        String b = (apellidos == null || apellidos.isBlank()) ? "" : apellidos.trim().substring(0, 1);
        return (a + b).toUpperCase();
    }

    /** Color de avatar según la especialidad (igual que en los prototipos de la PC1). */
    public String colorEspecialidad(String especialidad) {
        String e = normalizar(especialidad);
        if (e.contains("nutri")) return "#125952";
        if (e.contains("psico")) return "#5B4B8A";
        if (e.contains("odonto")) return "#B97C1E";
        if (e.contains("general")) return "#0B3E39";
        return "#52655D";
    }

    public String colorPersona(Integer id) {
        return PALETA[(id == null ? 0 : Math.abs(id)) % PALETA.length];
    }

    public int edad(LocalDate fechaNacimiento) {
        return fechaNacimiento == null ? 0 : Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    public String dia(LocalDate fecha) {
        return DIAS[fecha.getDayOfWeek().getValue() - 1];
    }

    /** Ej.: "viernes 16 de octubre". */
    public String fechaLarga(LocalDate fecha) {
        return DIAS_LARGOS[fecha.getDayOfWeek().getValue() - 1] + " " + fecha.getDayOfMonth()
                + " de " + MESES[fecha.getMonthValue() - 1];
    }

    public String codigoCita(Integer id) {
        return String.format("SC-%06d", id == null ? 0 : id);
    }

    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase();
    }
}
