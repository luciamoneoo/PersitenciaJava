package pio.daw.ra9.ejemplo.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "entrada")
public class Entrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String localizador;

    @NotNull
    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fechaCompra;

    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal precio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyeccion_id", nullable = false)
    private Proyeccion proyeccion;

    public Entrada() {
        this.localizador = UUID.randomUUID().toString();
        this.fechaCompra = LocalDateTime.now();
    }

    public Entrada(BigDecimal precio, Proyeccion proyeccion) {
        this();
        this.precio = precio;
        this.proyeccion = proyeccion;
    }

    public Long getId() { return id; }
    public String getLocalizador() { return localizador; }
    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public Proyeccion getProyeccion() { return proyeccion; }
    public void setProyeccion(Proyeccion proyeccion) { this.proyeccion = proyeccion; }
}
