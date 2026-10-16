package pe.utp.saludcerca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Catálogo de clínicas/consultorios registrados en la plataforma.
 */
@Entity
@Table(name = "centro_salud")
public class CentroSalud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_centro_salud")
    private Integer id;

    @NotBlank
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @NotBlank
    @Column(name = "distrito", nullable = false, length = 80)
    private String distrito;

    @Column(name = "telefono", length = 20)
    private String telefono;

    public CentroSalud() {
    }

    public CentroSalud(String nombre, String distrito, String telefono) {
        this.nombre = nombre;
        this.distrito = distrito;
        this.telefono = telefono;
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

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
