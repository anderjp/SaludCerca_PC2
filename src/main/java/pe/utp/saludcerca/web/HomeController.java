package pe.utp.saludcerca.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Página de inicio: elección de rol (Paciente, Médico o Administrador). */
@Controller
public class HomeController {

    @GetMapping("/")
    public String inicio() {
        return "index";
    }
}
