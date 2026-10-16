package pe.utp.saludcerca.pattern.structural.composite;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PATRÓN COMPOSITE — rol: COMPUESTO (Composite).
 *
 * Un paquete/combo que agrupa varios {@link ComponenteServicio}, que pueden ser
 * servicios individuales (hojas) U OTROS paquetes (composición en árbol). Su
 * precio es la suma recursiva de sus hijos.
 */
public class PaqueteServicios implements ComponenteServicio {

    private final String nombre;
    private final List<ComponenteServicio> items = new ArrayList<>();

    public PaqueteServicios(String nombre) {
        this.nombre = nombre;
    }

    /** Agrega un hijo (servicio o sub-paquete). Devuelve this para encadenar. */
    public PaqueteServicios agregar(ComponenteServicio componente) {
        items.add(componente);
        return this;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public BigDecimal precio() {
        return items.stream()
                .map(ComponenteServicio::precio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String mostrar(int nivel) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ".repeat(nivel)).append("📦 ").append(nombre)
          .append(" (total S/ ").append(precio()).append(")");
        for (ComponenteServicio item : items) {
            sb.append("\n").append(item.mostrar(nivel + 1));
        }
        return sb.toString();
    }

    @Override
    public boolean esCompuesto() {
        return true;
    }

    @Override
    public List<ComponenteServicio> getHijos() {
        return Collections.unmodifiableList(items);
    }
}
