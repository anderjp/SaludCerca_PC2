package pe.utp.saludcerca.pattern.structural.facade;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Subsistema usado por el Facade: genera el enlace externo de videollamada
 * (Zoom/Google Meet) para las teleconsultas. Aquí se simula el formato de Meet.
 */
@Service
public class GeneradorEnlaceVideollamada {

    private static final String LETRAS = "abcdefghijklmnopqrstuvwxyz";
    private final SecureRandom random = new SecureRandom();

    public String generar() {
        return "https://meet.google.com/" + bloque(3) + "-" + bloque(4) + "-" + bloque(3);
    }

    private String bloque(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(LETRAS.charAt(random.nextInt(LETRAS.length())));
        }
        return sb.toString();
    }
}
