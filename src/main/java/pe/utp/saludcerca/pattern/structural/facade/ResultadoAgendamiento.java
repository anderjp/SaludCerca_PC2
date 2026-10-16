package pe.utp.saludcerca.pattern.structural.facade;

/**
 * Resultado que devuelve el {@link AgendamientoFacade} tras intentar agendar.
 * Resume en un solo objeto todo lo que ocurrió en los distintos subsistemas.
 */
public class ResultadoAgendamiento {

    private final boolean exito;
    private final String mensaje;
    private final Integer idCita;
    private final String enlaceVideollamada;
    private final String notificacion;

    private ResultadoAgendamiento(boolean exito, String mensaje, Integer idCita,
                                  String enlaceVideollamada, String notificacion) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.idCita = idCita;
        this.enlaceVideollamada = enlaceVideollamada;
        this.notificacion = notificacion;
    }

    public static ResultadoAgendamiento error(String mensaje) {
        return new ResultadoAgendamiento(false, mensaje, null, null, null);
    }

    public static ResultadoAgendamiento ok(Integer idCita, String enlace, String notificacion) {
        return new ResultadoAgendamiento(true, "Cita agendada correctamente", idCita, enlace, notificacion);
    }

    public boolean isExito() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Integer getIdCita() {
        return idCita;
    }

    public String getEnlaceVideollamada() {
        return enlaceVideollamada;
    }

    public String getNotificacion() {
        return notificacion;
    }

    public String resumen() {
        if (!exito) {
            return "❌ No se pudo agendar: " + mensaje;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("✅ ").append(mensaje).append("\n");
        sb.append("   N° de cita   : ").append(idCita).append("\n");
        sb.append("   Enlace video : ").append(enlaceVideollamada == null ? "(presencial)" : enlaceVideollamada).append("\n");
        sb.append("   Notificación : ").append(notificacion);
        return sb.toString();
    }
}
