package pe.utp.saludcerca.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.utp.saludcerca.model.Cita;
import pe.utp.saludcerca.model.Medico;
import pe.utp.saludcerca.model.enums.EstadoCita;
import pe.utp.saludcerca.model.enums.EstadoVerificacion;
import pe.utp.saludcerca.pattern.creational.prototype.PlantillaHistorial;
import pe.utp.saludcerca.pattern.creational.prototype.RegistroPlantillas;
import pe.utp.saludcerca.repository.CitaRepository;
import pe.utp.saludcerca.repository.HistorialClinicoRepository;
import pe.utp.saludcerca.repository.MedicoRepository;
import pe.utp.saludcerca.service.AtencionService;

import java.util.Comparator;
import java.util.List;

/**
 * Flujo del MÉDICO (prototipo de la PC1): Agenda → Atender y diagnosticar → Finalizar.
 *
 * Patrón que se ejecuta: PROTOTYPE. El formulario de diagnóstico se llena con
 * una COPIA de una plantilla base (RegistroPlantillas), que el médico edita sin
 * alterar el original. Al finalizar se notifica al paciente (Factory Method).
 */
@Controller
@RequestMapping("/medico")
public class MedicoController {

    private final MedicoRepository medicoRepository;
    private final CitaRepository citaRepository;
    private final HistorialClinicoRepository historialRepository;
    private final RegistroPlantillas registroPlantillas;
    private final AtencionService atencionService;

    public MedicoController(MedicoRepository medicoRepository, CitaRepository citaRepository,
                            HistorialClinicoRepository historialRepository, RegistroPlantillas registroPlantillas,
                            AtencionService atencionService) {
        this.medicoRepository = medicoRepository;
        this.citaRepository = citaRepository;
        this.historialRepository = historialRepository;
        this.registroPlantillas = registroPlantillas;
        this.atencionService = atencionService;
    }

    // ===================== 1. Agenda =====================
    @GetMapping
    public String agenda(@RequestParam(required = false) Integer medicoId, Model model) {
        List<Medico> medicos = medicoRepository.findByEstadoVerificacion(EstadoVerificacion.Aprobado);
        model.addAttribute("medicos", medicos);
        if (medicos.isEmpty()) {
            return "medico/agenda";
        }
        Medico medico = medicos.stream()
                .filter(m -> m.getId().equals(medicoId))
                .findFirst()
                .orElse(medicos.get(0));

        List<Cita> citas = citaRepository.findByMedico_IdOrderByFechaHoraDesc(medico.getId());
        List<Cita> porAtender = citas.stream()
                .filter(c -> c.getEstado() == EstadoCita.Pendiente || c.getEstado() == EstadoCita.Confirmada)
                .sorted(Comparator.comparing(Cita::getFechaHora))
                .toList();
        List<Cita> atendidas = citas.stream()
                .filter(c -> c.getEstado() == EstadoCita.Completada)
                .toList();

        model.addAttribute("medico", medico);
        model.addAttribute("porAtender", porAtender);
        model.addAttribute("atendidas", atendidas);
        return "medico/agenda";
    }

    // ===================== 2. Atender y registrar diagnóstico =====================
    @GetMapping("/cita/{id}")
    public String atender(@PathVariable Integer id,
                          @RequestParam(required = false) String plantilla,
                          Model model) {
        Cita cita = citaRepository.findById(id).orElse(null);
        if (cita == null) {
            return "redirect:/medico";
        }
        if (cita.getEstado() == EstadoCita.Completada) {
            return "redirect:/medico/cita/" + id + "/finalizada";
        }
        String clave = (plantilla != null && registroPlantillas.clavesDisponibles().contains(plantilla))
                ? plantilla
                : registroPlantillas.claveSugerida(cita.getMedico().getEspecialidad().getNombre());

        // PROTOTYPE: se obtiene un CLON de la plantilla y se personaliza el clon
        PlantillaHistorial clon = registroPlantillas.obtenerClon(clave);
        clon.setObservacionesBase(clon.getObservacionesBase() + " Paciente: " + cita.getPaciente().getNombreCompleto() + ".");

        model.addAttribute("cita", cita);
        model.addAttribute("clon", clon);
        model.addAttribute("clave", clave);
        model.addAttribute("plantillas", registroPlantillas.opciones());
        return "medico/atender";
    }

    // ===================== 3. Finalizar =====================
    @PostMapping("/cita/{id}/finalizar")
    public String finalizar(@PathVariable Integer id,
                            @RequestParam(required = false) String diagnostico,
                            @RequestParam(required = false) String receta,
                            @RequestParam(required = false) String observaciones,
                            RedirectAttributes ra) {
        Cita cita = citaRepository.findById(id).orElse(null);
        if (cita == null) {
            return "redirect:/medico";
        }
        if (diagnostico == null || diagnostico.isBlank()) {
            ra.addFlashAttribute("error", "El diagnóstico es obligatorio.");
            return "redirect:/medico/cita/" + id;
        }
        String envio = atencionService.finalizarAtencion(cita, diagnostico.trim(), receta, observaciones);
        ra.addFlashAttribute("envio", envio);
        return "redirect:/medico/cita/" + id + "/finalizada";
    }

    @GetMapping("/cita/{id}/finalizada")
    public String finalizada(@PathVariable Integer id, Model model) {
        Cita cita = citaRepository.findById(id).orElse(null);
        if (cita == null) {
            return "redirect:/medico";
        }
        model.addAttribute("cita", cita);
        model.addAttribute("historial", historialRepository.findByCita_Id(id).orElse(null));
        return "medico/finalizada";
    }
}
