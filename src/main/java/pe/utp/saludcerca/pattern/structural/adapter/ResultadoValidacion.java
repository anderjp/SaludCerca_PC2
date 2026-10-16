package pe.utp.saludcerca.pattern.structural.adapter;

/**
 * PATRÓN ADAPTER — objeto de resultado en el formato que usa SaludCerca.
 * (Es lo que nuestro sistema entiende, independiente de cómo responda RENIEC.)
 */
public class ResultadoValidacion {

    private final boolean valido;
    private final String nombres;
    private final String apellidos;
    private final String mensaje;

    public ResultadoValidacion(boolean valido, String nombres, String apellidos, String mensaje) {
        this.valido = valido;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.mensaje = mensaje;
    }

    public boolean isValido() {
        return valido;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getNombreCompleto() {
        return (nombres == null ? "" : nombres) + " " + (apellidos == null ? "" : apellidos);
    }
}
