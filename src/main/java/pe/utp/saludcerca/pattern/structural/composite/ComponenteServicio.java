package pe.utp.saludcerca.pattern.structural.composite;

import java.math.BigDecimal;
import java.util.List;

/**
 * PATRÓN COMPOSITE — rol: COMPONENTE (Component).
 *
 * Interfaz común para objetos individuales (un servicio médico) y para
 * composiciones (un paquete que agrupa varios servicios). Gracias a esto, el
 * cliente trata a ambos de la misma forma (p. ej. pedir su precio total).
 *
 * Se usa la variante "transparente" del Composite: el componente también
 * expone a sus hijos ({@link #getHijos()}), así la vista puede recorrer el
 * árbol completo sin preguntar de qué tipo es cada nodo.
 */
public interface ComponenteServicio {

    String getNombre();

    /** Precio: en una hoja es su precio; en un paquete, la suma de sus hijos. */
    BigDecimal precio();

    /** Representación en árbol con sangría según el nivel. */
    String mostrar(int nivel);

    /** ¿Es un compuesto (paquete) o una hoja (servicio)? */
    default boolean esCompuesto() {
        return false;
    }

    /** Hijos del componente; una hoja no tiene hijos. */
    default List<ComponenteServicio> getHijos() {
        return List.of();
    }
}
