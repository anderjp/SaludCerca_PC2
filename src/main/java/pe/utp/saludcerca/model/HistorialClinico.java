package pe.utp.saludcerca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Historial clínico asociado a una cita (relación 1 a 1).
 * Registra el diagnóstico y la receta de la atención.
 */
@Entity
@Table(name = "historial_clinico")
public class HistorialClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_cita", nullable = false, unique = true)
    private Cita cita;

    @NotBlank
    @Column(name = "diagnostico", nullable = false, columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "receta", columnDefinition = "TEXT")
    private String receta;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public HistorialClinico() {
    }

    public HistorialClinico(Cita cita, String diagnostico, String receta, String observaciones) {
        this.cita = cita;
        this.diagnostico = diagnostico;
        this.receta = receta;
        this.observaciones = observaciones;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getReceta() {
        return receta;
    }

    public void setReceta(String receta) {
        this.receta = receta;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
