package pe.utp.saludcerca.pattern.structural.adapter;

/**
 * PATRÓN ADAPTER — rol: ADAPTADOR (Adapter).
 *
 * Implementa la interfaz que espera SaludCerca ({@link ValidadorIdentidad}) y por
 * dentro traduce las llamadas al servicio externo {@link ReniecApiExterna},
 * convirtiendo su formato propio ("APELLIDOS|NOMBRES") al que usamos nosotros.
 *
 * Así el resto del sistema valida identidades sin enterarse de cómo responde
 * RENIEC; si mañana cambia el proveedor, solo se cambia este adaptador.
 */
public class ReniecValidadorAdapter implements ValidadorIdentidad {

    private final ReniecApiExterna reniec;

    public ReniecValidadorAdapter(ReniecApiExterna reniec) {
        this.reniec = reniec;
    }

    @Override
    public ResultadoValidacion validar(String dni) {
        try {
            String formatoExterno = reniec.consultarDocumento(dni);   // "APELLIDOS|NOMBRES"
            String[] partes = formatoExterno.split("\\|");
            String apellidos = partes[0];
            String nombres = partes.length > 1 ? partes[1] : "";
            return new ResultadoValidacion(true, nombres, apellidos, "Identidad validada con RENIEC");
        } catch (IllegalArgumentException e) {
            // Adaptamos también el error al formato de nuestro sistema
            return new ResultadoValidacion(false, null, null, e.getMessage());
        }
    }
}
