package pe.utp.saludcerca.pattern.creational.factorymethod;

/**
 * PATRÓN FACTORY METHOD — rol: PRODUCTO (Product).
 *
 * Abstrae un canal por el que se envía una notificación al paciente.
 * Las clases concretas (Email, SMS) implementan el envío real.
 */
public interface CanalNotificacion {

    String getNombre();

    /** Envía el mensaje y devuelve una descripción de lo enviado (para la demo). */
    String enviar(String destinatario, String mensaje);
}
