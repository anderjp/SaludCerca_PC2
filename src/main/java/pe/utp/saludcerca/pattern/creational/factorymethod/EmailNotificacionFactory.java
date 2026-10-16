package pe.utp.saludcerca.pattern.creational.factorymethod;

import pe.utp.saludcerca.model.Paciente;

/**
 * FACTORY METHOD — rol: CREADOR CONCRETO (ConcreteCreator).
 * Decide crear el canal de correo electrónico.
 */
public class EmailNotificacionFactory extends NotificacionFactory {

    @Override
    protected CanalNotificacion crearCanal() {
        return new CanalEmail();
    }

    @Override
    protected String destino(Paciente paciente) {
        return paciente.getEmail();
    }
}
