package pe.utp.saludcerca.pattern.creational.factorymethod;

/**
 * FACTORY METHOD — rol: PRODUCTO CONCRETO (ConcreteProduct).
 * Canal de notificación por mensaje de texto (SMS).
 */
public class CanalSms implements CanalNotificacion {

    @Override
    public String getNombre() {
        return "SMS";
    }

    @Override
    public String enviar(String destinatario, String mensaje) {
        // Aquí iría la integración real con una pasarela de SMS.
        return "📱 SMS enviado a " + destinatario + " → \"" + mensaje + "\"";
    }
}
