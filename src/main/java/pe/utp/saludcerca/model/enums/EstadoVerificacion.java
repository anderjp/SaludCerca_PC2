package pe.utp.saludcerca.model.enums;

/**
 * Estado de verificación de un médico por parte del administrador.
 * Los nombres de las constantes coinciden EXACTAMENTE con los valores del
 * ENUM de la base de datos (columna medico.estado_verificacion), para que
 * JPA (@Enumerated STRING) lea y escriba el mismo texto.
 */
public enum EstadoVerificacion {
    Pendiente,
    Aprobado,
    Rechazado
}
