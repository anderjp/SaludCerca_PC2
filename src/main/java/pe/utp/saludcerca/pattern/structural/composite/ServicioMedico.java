package pe.utp.saludcerca.pattern.structural.composite;

import java.math.BigDecimal;

/**
 * PATRÓN COMPOSITE — rol: HOJA (Leaf).
 * Un servicio médico individual (no contiene otros servicios).
 */
public class ServicioMedico implements ComponenteServicio {

    private final String nombre;
    private final BigDecimal precio;

    public ServicioMedico(String nombre, BigDecimal precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public BigDecimal precio() {
        return precio;
    }

    @Override
    public String mostrar(int nivel) {
        return "  ".repeat(nivel) + "• " + nombre + " (S/ " + precio + ")";
    }
}
