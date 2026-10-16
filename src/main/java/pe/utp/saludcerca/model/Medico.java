package pe.utp.saludcerca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import pe.utp.saludcerca.model.enums.EstadoVerificacion;

/**
 * Médico (especialista) que atiende citas. Pertenece a una especialidad y a un
 * centro de salud, y debe ser verificado por el administrador antes de operar.
 */
@Entity
@Table(name = "medico")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medico")
    private Integer id;

    @NotBlank
    @Column(name = "colegiatura", nullable = false, unique = true, length = 20)
    private String colegiatura;

    @NotBlank
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @NotBlank
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Email
    @NotBlank
    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_especialidad", nullable = false)
    private Especialidad especialidad;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_centro_salud", nullable = false)
    private CentroSalud centroSalud;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_verificacion", nullable = false, length = 20)
    private EstadoVerificacion estadoVerificacion = EstadoVerificacion.Pendiente;

    public Medico() {
    }

    public Medico(String colegiatura, String nombres, String apellidos, String email,
                  String telefono, Especialidad especialidad, CentroSalud centroSalud) {
        this.colegiatura = colegiatura;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.especialidad = especialidad;
        this.centroSalud = centroSalud;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getColegiatura() {
        return colegiatura;
    }

    public void setColegiatura(String colegiatura) {
        this.colegiatura = colegiatura;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public CentroSalud getCentroSalud() {
        return centroSalud;
    }

    public void setCentroSalud(CentroSalud centroSalud) {
        this.centroSalud = centroSalud;
    }

    public EstadoVerificacion getEstadoVerificacion() {
        return estadoVerificacion;
    }

    public void setEstadoVerificacion(EstadoVerificacion estadoVerificacion) {
        this.estadoVerificacion = estadoVerificacion;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}
