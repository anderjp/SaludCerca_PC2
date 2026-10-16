package pe.utp.saludcerca.pattern.structural.decorator;

/**
 * PATRÓN DECORATOR — rol: DECORADOR (Decorator, abstracto).
 *
 * Mantiene una referencia a otro {@link CostoConsulta} (el "envuelto") y también
 * implementa la misma interfaz. Las subclases añaden su costo sobre el envuelto.
 */
public abstract class CostoDecorator implements CostoConsulta {

    protected final CostoConsulta envuelto;

    protected CostoDecorator(CostoConsulta envuelto) {
        this.envuelto = envuelto;
    }
}
