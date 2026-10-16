package pe.utp.saludcerca.pattern.structural.adapter;

import java.util.Map;

/**
 * PATRÓN ADAPTER — rol: ADAPTADO (Adaptee).
 *
 * Simula el SDK/servicio EXTERNO de RENIEC. Tiene una interfaz INCOMPATIBLE con
 * la nuestra: el método se llama distinto y devuelve los datos en su propio
 * formato de texto ("APELLIDOS|NOMBRES"). No podemos cambiar este código (es de
 * un tercero), por eso necesitamos un Adapter.
 */
public class ReniecApiExterna {

    // "Base de datos" simulada de RENIEC (DNI → "APELLIDOS|NOMBRES")
    private static final Map<String, String> PADRON = Map.of(
            "45231890", "Effio Vargas|Karla",
            "41239876", "Ramos Paredes|Diego",
            "43567812", "Quiroz Salinas|Sofía",
            "70123456", "Quispe Huamán|José Luis",
            "48765432", "Torres Medina|Lucía",
            "23456789", "Castañeda Ruiz|Milagros"
    );

    /**
     * Método del proveedor externo (firma distinta a la de SaludCerca).
     * @return "APELLIDOS|NOMBRES"
     * @throws IllegalArgumentException si el documento no existe
     */
    public String consultarDocumento(String numeroDocumento) {
        String registro = PADRON.get(numeroDocumento);
        if (registro == null) {
            throw new IllegalArgumentException("Documento no encontrado en el padrón RENIEC");
        }
        return registro;
    }
}
