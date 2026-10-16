package pe.utp.saludcerca.pattern.creational.factorymethod;

/**
 * FACTORY METHOD — rol: PRODUCTO CONCRETO (ConcreteProduct).
 * Canal de notificación por correo electrónico.
 */
public class CanalEmail implements CanalNotificacion {

    @Override
    public String getNombre() {
        return "EMAIL";
    }

    @Override
    public String enviar(String destinatario, String mensaje) {
        // Aquí iría la integración real con un servidor SMTP.
        return "📧 Correo enviado a " + destinatario + " → \"" + mensaje + "\"";
    }
}
