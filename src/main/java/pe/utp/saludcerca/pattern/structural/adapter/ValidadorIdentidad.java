package pe.utp.saludcerca.pattern.structural.adapter;

/**
 * PATRÓN ADAPTER — rol: DESTINO (Target).
 *
 * Es la interfaz que SaludCerca espera para validar la identidad de un paciente.
 * Nuestro código depende de ESTA interfaz, no del proveedor externo concreto.
 */
public interface ValidadorIdentidad {

    ResultadoValidacion validar(String dni);
}
