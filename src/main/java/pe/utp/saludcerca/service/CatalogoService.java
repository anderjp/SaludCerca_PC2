package pe.utp.saludcerca.service;

import org.springframework.stereotype.Service;
import pe.utp.saludcerca.model.Especialidad;
import pe.utp.saludcerca.pattern.creational.singleton.ConfiguracionApp;
import pe.utp.saludcerca.pattern.structural.composite.ComponenteServicio;
import pe.utp.saludcerca.pattern.structural.composite.PaqueteServicios;
import pe.utp.saludcerca.pattern.structural.composite.ServicioMedico;
import pe.utp.saludcerca.repository.EspecialidadRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Construye el catálogo de servicios como un árbol COMPOSITE:
 * paquetes (compuestos) que contienen servicios (hojas) u otros paquetes.
 * Los servicios por especialidad salen de la base de datos y sus precios del
 * tarifario central (Singleton).
 */
@Service
public class CatalogoService {

    private static final BigDecimal FACTOR_CONTROL = new BigDecimal("0.60");

    private final EspecialidadRepository especialidadRepository;

    public CatalogoService(EspecialidadRepository especialidadRepository) {
        this.especialidadRepository = especialidadRepository;
    }

    public ComponenteServicio construirCatalogo() {
        ConfiguracionApp cfg = ConfiguracionApp.getInstance();

        PaqueteServicios porEspecialidad = new PaqueteServicios("Servicios por especialidad");
        for (Especialidad e : especialidadRepository.findAll()) {
            BigDecimal tarifa = cfg.getTarifa(e.getNombre());
            porEspecialidad.agregar(new PaqueteServicios(e.getNombre())
                    .agregar(new ServicioMedico("Consulta de " + e.getNombre(), tarifa))
                    .agregar(new ServicioMedico("Control y seguimiento",
                            tarifa.multiply(FACTOR_CONTROL).setScale(2, RoundingMode.HALF_UP))));
        }

        PaqueteServicios preventivo = new PaqueteServicios("Paquete preventivo")
                .agregar(new ServicioMedico("Análisis de sangre completo", new BigDecimal("60.00")))
                .agregar(new ServicioMedico("Electrocardiograma", new BigDecimal("90.00")));

        PaqueteServicios chequeo = new PaqueteServicios("Combo Chequeo integral")
                .agregar(new ServicioMedico("Consulta de medicina general", cfg.getTarifa("Medicina General")))
                .agregar(new ServicioMedico("Consulta nutricional", cfg.getTarifa("Nutrición Clínica")))
                .agregar(preventivo);   // un paquete dentro de otro paquete

        return new PaqueteServicios("Catálogo de servicios SaludCerca")
                .agregar(porEspecialidad)
                .agregar(new PaqueteServicios("Paquetes y combos").agregar(chequeo));
    }
}
