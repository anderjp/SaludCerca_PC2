package pe.utp.saludcerca.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.utp.saludcerca.model.CentroSalud;
import pe.utp.saludcerca.model.Especialidad;
import pe.utp.saludcerca.model.Medico;
import pe.utp.saludcerca.model.enums.EstadoVerificacion;
import pe.utp.saludcerca.pattern.creational.singleton.ConfiguracionApp;
import pe.utp.saludcerca.repository.*;
import pe.utp.saludcerca.service.CatalogoService;
import pe.utp.saludcerca.service.ReporteService;

import java.util.Comparator;
import java.util.List;

/**
 * Flujo del ADMINISTRADOR (prototipo de la PC1): Panel, Verificar médicos,
 * Catálogo, Reportes y Configuración.
 *
 * Patrones que se ejecutan:
 *  - Composite: árbol de servicios y paquetes del catálogo (CatalogoService).
 *  - Builder: reporte de atenciones construido paso a paso (ReporteService).
 *  - Singleton: configuración central de la aplicación (ConfiguracionApp).
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MedicoRepository medicoRepository;
    private final EspecialidadRepository especialidadRepository;
    private final CentroSaludRepository centroRepository;
    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final CatalogoService catalogoService;
    private final ReporteService reporteService;

    public AdminController(MedicoRepository medicoRepository, EspecialidadRepository especialidadRepository,
                           CentroSaludRepository centroRepository, CitaRepository citaRepository,
                           PacienteRepository pacienteRepository, CatalogoService catalogoService,
                           ReporteService reporteService) {
        this.medicoRepository = medicoRepository;
        this.especialidadRepository = especialidadRepository;
        this.centroRepository = centroRepository;
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.catalogoService = catalogoService;
        this.reporteService = reporteService;
    }

    // ===================== Panel =====================
    @GetMapping
    public String panel(Model model) {
        model.addAttribute("totalCitas", citaRepository.count());
        model.addAttribute("medicosActivos", medicoRepository.findByEstadoVerificacion(EstadoVerificacion.Aprobado).size());
        model.addAttribute("totalCentros", centroRepository.count());
        model.addAttribute("pendientes", medicoRepository.findByEstadoVerificacion(EstadoVerificacion.Pendiente).size());
        model.addAttribute("totalPacientes", pacienteRepository.count());
        return "admin/panel";
    }

    // ===================== Verificar médicos =====================
    @GetMapping("/medicos")
    public String medicos(Model model) {
        List<Medico> resueltos = medicoRepository.findAll().stream()
                .filter(m -> m.getEstadoVerificacion() != EstadoVerificacion.Pendiente)
                .sorted(Comparator.comparing(Medico::getApellidos))
                .toList();
        model.addAttribute("pendientes", medicoRepository.findByEstadoVerificacion(EstadoVerificacion.Pendiente));
        model.addAttribute("resueltos", resueltos);
        return "admin/medicos";
    }

    @PostMapping("/medicos/{id}/estado")
    public String cambiarEstado(@PathVariable Integer id, @RequestParam String estado, RedirectAttributes ra) {
        Medico medico = medicoRepository.findById(id).orElse(null);
        boolean valido = "Aprobado".equals(estado) || "Rechazado".equals(estado);
        if (medico == null || !valido) {
            return "redirect:/admin/medicos";
        }
        medico.setEstadoVerificacion(EstadoVerificacion.valueOf(estado));
        medicoRepository.save(medico);
        ra.addFlashAttribute("ok", "Aprobado".equals(estado)
                ? medico.getNombreCompleto() + " fue aprobado(a): ya aparece en la búsqueda de pacientes."
                : medico.getNombreCompleto() + " fue rechazado(a).");
        return "redirect:/admin/medicos";
    }

    // ===================== Catálogo =====================
    @GetMapping("/catalogo")
    public String catalogo(Model model) {
        model.addAttribute("especialidades", especialidadRepository.findAll());
        model.addAttribute("centros", centroRepository.findAll());
        model.addAttribute("catalogo", catalogoService.construirCatalogo());   // COMPOSITE
        return "admin/catalogo";
    }

    @PostMapping("/especialidades")
    public String agregarEspecialidad(@RequestParam String nombre,
                                      @RequestParam(defaultValue = "false") boolean teleconsulta,
                                      RedirectAttributes ra) {
        String n = nombre == null ? "" : nombre.trim();
        if (n.isEmpty() || n.length() > 80) {
            ra.addFlashAttribute("error", "Ingresa un nombre de especialidad (máximo 80 caracteres).");
        } else if (especialidadRepository.findByNombre(n).isPresent()) {
            ra.addFlashAttribute("error", "La especialidad " + n + " ya existe.");
        } else {
            especialidadRepository.save(new Especialidad(n, teleconsulta));
            ra.addFlashAttribute("ok", "Especialidad agregada: " + n + ".");
        }
        return "redirect:/admin/catalogo";
    }

    @PostMapping("/especialidades/{id}/teleconsulta")
    public String alternarTeleconsulta(@PathVariable Integer id, RedirectAttributes ra) {
        especialidadRepository.findById(id).ifPresent(e -> {
            e.setPermiteTeleconsulta(!e.isPermiteTeleconsulta());
            especialidadRepository.save(e);
            ra.addFlashAttribute("ok", e.getNombre() + (e.isPermiteTeleconsulta()
                    ? " ahora permite teleconsulta." : " ahora es solo presencial."));
        });
        return "redirect:/admin/catalogo";
    }

    @PostMapping("/centros")
    public String agregarCentro(@RequestParam String nombre, @RequestParam String distrito,
                                @RequestParam(required = false) String telefono, RedirectAttributes ra) {
        String n = nombre == null ? "" : nombre.trim();
        String d = distrito == null ? "" : distrito.trim();
        if (n.isEmpty() || d.isEmpty() || n.length() > 120 || d.length() > 80) {
            ra.addFlashAttribute("error", "Ingresa el nombre y el distrito del centro de salud.");
        } else {
            centroRepository.save(new CentroSalud(n, d, telefono == null || telefono.isBlank() ? null : telefono.trim()));
            ra.addFlashAttribute("ok", "Centro de salud agregado: " + n + ".");
        }
        return "redirect:/admin/catalogo";
    }

    // ===================== Reportes (BUILDER) =====================
    @GetMapping("/reportes")
    public String reportes(@RequestParam(defaultValue = "0") int dias,
                           @RequestParam(required = false) Integer especialidad,
                           @RequestParam(defaultValue = "false") boolean agrupar,
                           Model model) {
        model.addAttribute("resultado", reporteService.generar(especialidad, dias, agrupar));
        model.addAttribute("especialidades", especialidadRepository.findAll());
        model.addAttribute("dias", dias);
        model.addAttribute("especialidadSel", especialidad);
        model.addAttribute("agrupar", agrupar);
        return "admin/reportes";
    }

    // ===================== Configuración (SINGLETON) =====================
    @GetMapping("/configuracion")
    public String configuracion(Model model) {
        ConfiguracionApp primera = ConfiguracionApp.getInstance();
        ConfiguracionApp segunda = ConfiguracionApp.getInstance();
        model.addAttribute("cfg", primera);
        model.addAttribute("mismaInstancia", primera == segunda);
        model.addAttribute("idObjeto", Integer.toHexString(System.identityHashCode(primera)));
        return "admin/configuracion";
    }
}
