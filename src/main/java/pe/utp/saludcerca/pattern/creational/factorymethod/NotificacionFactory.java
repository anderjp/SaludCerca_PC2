package pe.utp.saludcerca.pattern.creational.factorymethod;

import pe.utp.saludcerca.model.Paciente;

/**
 * PATRÓN FACTORY METHOD — rol: CREADOR (Creator).
 *
 * Intención: definir una interfaz para crear un objeto (el canal), pero dejar
 * que las SUBCLASES decidan qué clase concreta instanciar.
 *
 * Problema que resuelve en SaludCerca: el paciente elige cómo quiere ser
 * notificado (email o SMS). El código que notifica (por ejemplo el Facade de
 * agendamiento) no se acopla a las clases concretas de cada canal.
 *
 * {@link #crearCanal()} es el "factory method": cada subclase lo implementa.
 * {@link #destino(Paciente)} indica qué dato de contacto usa ese canal.
 */
public abstract class NotificacionFactory {

    /** FACTORY METHOD: las subclases deciden qué canal concreto crear. */
    protected abstract CanalNotificacion crearCanal();

    /** Dato de contacto del paciente que necesita el canal (email, teléfono...). */
    protected abstract String destino(Paciente paciente);

    /** Operación de alto nivel que usa el canal creado por la subclase. */
    public String notificar(String destinatario, String mensaje) {
        CanalNotificacion canal = crearCanal();
        return canal.enviar(destinatario, mensaje);
    }

    /** Notifica a un paciente usando el dato de contacto adecuado al canal. */
    public String notificarPaciente(Paciente paciente, String mensaje) {
        return notificar(destino(paciente), mensaje);
    }

    /** Nombre del canal que produce esta fábrica (EMAIL, SMS...). */
    public String getCanal() {
        return crearCanal().getNombre();
    }
}
