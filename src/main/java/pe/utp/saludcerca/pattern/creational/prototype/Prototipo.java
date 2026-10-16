package pe.utp.saludcerca.pattern.creational.prototype;

/**
 * PATRÓN PROTOTYPE — rol: PROTOTIPO (Prototype).
 *
 * Declara la operación de clonación. Cualquier objeto que sepa "copiarse a sí
 * mismo" implementa esta interfaz.
 *
 * @param <T> el tipo que se devuelve al clonar
 */
public interface Prototipo<T> {

    /** Devuelve una copia independiente de este objeto. */
    T clonar();
}
