package pe.utp.saludcerca.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import pe.utp.saludcerca.model.enums.EstadoCita;
import pe.utp.saludcerca.model.enums.TipoAtencion;

import java.time.LocalDateTime;

/**
 * Cita agendada entre un paciente y un médico.
 * El {@code enlaceVideollamada} solo se genera cuando la atención es
 * Teleconsulta y la especialidad del médico la permite.
 */
@Entity
@Table(name = "cita", indexes = @Index(name = "idx_cita_fecha", columnList = "fecha_hora"))
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_medico", nullable = false)
    private Medico medico;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_atencion", nullable = false, length = 20)
    private TipoAtencion tipoAtencion;

    @Column(name = "enlace_videollamada", length = 255)
    private String enlaceVideollamada;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCita estado = EstadoCita.Pendiente;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    public Cita() {
    }

    public Cita(Paciente paciente, Medico medico, LocalDateTime fechaHora, TipoAtencion tipoAtencion) {
        this.paciente = paciente;
        this.medico = medico;
        this.fechaHora = fechaHora;
        this.tipoAtencion = tipoAtencion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public TipoAtencion getTipoAtencion() {
        return tipoAtencion;
    }

    public void setTipoAtencion(TipoAtencion tipoAtencion) {
        this.tipoAtencion = tipoAtencion;
    }

    public String getEnlaceVideollamada() {
        return enlaceVideollamada;
    }

    public void setEnlaceVideollamada(String enlaceVideollamada) {
        this.enlaceVideollamada = enlaceVideollamada;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
