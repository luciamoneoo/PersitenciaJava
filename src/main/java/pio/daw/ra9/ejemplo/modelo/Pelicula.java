package pio.daw.ra9.ejemplo.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pelicula")
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título no puede estar vacío")
    @Column(nullable = false)
    private String titulo;

    @Min(value = 1888, message = "El año debe ser 1888 o posterior")
    @Column
    private Integer anio;

    @Positive(message = "La duración debe ser positiva")
    @Column
    private Integer duracion; // minutos

    @Column(columnDefinition = "TEXT")
    private String sinopsis;

    @DecimalMin(value = "0.0", message = "El precio de entrada no puede ser negativo")
    @Column(name = "precio_entrada", precision = 6, scale = 2)
    private BigDecimal precioEntrada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "director_id")
    @JsonBackReference
    private Director director;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "pelicula_genero",
        joinColumns = @JoinColumn(name = "pelicula_id"),
        inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    private List<Genero> generos = new ArrayList<>();

    public Pelicula() {}

    public Pelicula(String titulo, Integer anio, Integer duracion, BigDecimal precioEntrada) {
        this.titulo = titulo;
        this.anio = anio;
        this.duracion = duracion;
        this.precioEntrada = precioEntrada;
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }
    public Integer getDuracion() { return duracion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }
    public String getSinopsis() { return sinopsis; }
    public void setSinopsis(String sinopsis) { this.sinopsis = sinopsis; }
    public BigDecimal getPrecioEntrada() { return precioEntrada; }
    public void setPrecioEntrada(BigDecimal precioEntrada) { this.precioEntrada = precioEntrada; }
    public Director getDirector() { return director; }
    public void setDirector(Director director) { this.director = director; }
    public List<Genero> getGeneros() { return generos; }
    public void setGeneros(List<Genero> generos) { this.generos = generos; }
}
