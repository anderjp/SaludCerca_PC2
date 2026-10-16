package pe.utp.saludcerca.web;

/** Un horario de atención y si ya está ocupado (para la pantalla de agendar). */
public class Horario {

    private final String hora;
    private final boolean ocupado;

    public Horario(String hora, boolean ocupado) {
        this.hora = hora;
        this.ocupado = ocupado;
    }

    public String getHora() {
        return hora;
    }

    public boolean isOcupado() {
        return ocupado;
    }
}
