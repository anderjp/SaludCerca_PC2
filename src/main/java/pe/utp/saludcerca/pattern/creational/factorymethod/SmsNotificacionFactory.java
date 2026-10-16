package pe.utp.saludcerca.pattern.creational.factorymethod;

import pe.utp.saludcerca.model.Paciente;

/**
 * FACTORY METHOD — rol: CREADOR CONCRETO (ConcreteCreator).
 * Decide crear el canal de SMS.
 */
public class SmsNotificacionFactory extends NotificacionFactory {

    @Override
    protected CanalNotificacion crearCanal() {
        return new CanalSms();
    }

    @Override
    protected String destino(Paciente paciente) {
        String telefono = paciente.getTelefono();
        return (telefono == null || telefono.isBlank()) ? "(sin teléfono registrado)" : telefono;
    }
}
