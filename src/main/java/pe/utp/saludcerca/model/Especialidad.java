package pe.utp.saludcerca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Catálogo de especialidades médicas.
 * El indicador {@code permiteTeleconsulta} lo configura el administrador y
 * determina si una cita de esa especialidad puede ser por videollamada.
 */
@Entity
@Table(name = "especialidad")
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especialidad")
    private Integer id;

    @NotBlank
    @Column(name = "nombre", nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(name = "permite_teleconsulta", nullable = false)
    private boolean permiteTeleconsulta = false;

    public Especialidad() {
    }

    public Especialidad(String nombre, boolean permiteTeleconsulta) {
        this.nombre = nombre;
        this.permiteTeleconsulta = permiteTeleconsulta;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isPermiteTeleconsulta() {
        return permiteTeleconsulta;
    }

    public void setPermiteTeleconsulta(boolean permiteTeleconsulta) {
        this.permiteTeleconsulta = permiteTeleconsulta;
    }
}
