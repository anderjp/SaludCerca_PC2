package pe.utp.saludcerca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.utp.saludcerca.pattern.structural.adapter.ReniecApiExterna;
import pe.utp.saludcerca.pattern.structural.adapter.ReniecValidadorAdapter;
import pe.utp.saludcerca.pattern.structural.adapter.ValidadorIdentidad;

/**
 * Configuración de beans de los patrones.
 *
 * Los controladores dependen de la interfaz {@link ValidadorIdentidad} (principio
 * de Inversión de Dependencias); aquí se decide que la implementación concreta
 * sea el Adapter de RENIEC. Cambiar de proveedor solo requiere cambiar este bean.
 */
@Configuration
public class PatronesConfig {

    @Bean
    public ValidadorIdentidad validadorIdentidad() {
        return new ReniecValidadorAdapter(new ReniecApiExterna());
    }
}
