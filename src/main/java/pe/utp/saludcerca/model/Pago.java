package pe.utp.saludcerca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import pe.utp.saludcerca.model.enums.EstadoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pago simulado asociado a una cita (relación 1 a 1).
 * En esta etapa académica no hay pasarela real; el monto y el estado se
 * registran dentro del propio sistema.
 */
@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_cita", nullable = false, unique = true)
    private Cita cita;

    @DecimalMin(value = "0.0", message = "El monto no puede ser negativo")
    @Column(name = "monto", nullable = false, precision = 8, scale = 2)
    private BigDecimal monto;

    @Column(name = "metodo_pago", nullable = false, length = 40)
    private String metodoPago = "Simulado";

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false, length = 20)
    private EstadoPago estadoPago = EstadoPago.Pendiente;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    public Pago() {
    }

    public Pago(Cita cita, BigDecimal monto, String metodoPago) {
        this.cita = cita;
        this.monto = monto;
        this.metodoPago = metodoPago;
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

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public EstadoPago getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(EstadoPago estadoPago) {
        this.estadoPago = estadoPago;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }
}
