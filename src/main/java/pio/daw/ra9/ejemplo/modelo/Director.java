package pio.daw.ra9.ejemplo.modelo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "director")
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    @Column(nullable = false)
    private String apellidos;

    @Column
    private String nacionalidad;

    @Positive(message = "El año de nacimiento debe ser positivo")
    @Column(name = "anio_nacimiento")
    private Integer anioNacimiento;

    @OneToMany(mappedBy = "director", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Pelicula> peliculas = new ArrayList<>();

    public Director() {}

    public Director(String nombre, String apellidos, String nacionalidad, Integer anioNacimiento) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.nacionalidad = nacionalidad;
        this.anioNacimiento = anioNacimiento;
    }

    public void addPelicula(Pelicula pelicula) {
        peliculas.add(pelicula);
        pelicula.setDirector(this);
    }

    public void removePelicula(Pelicula pelicula) {
        peliculas.remove(pelicula);
        pelicula.setDirector(null);
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }
    public Integer getAnioNacimiento() { return anioNacimiento; }
    public void setAnioNacimiento(Integer anioNacimiento) { this.anioNacimiento = anioNacimiento; }
    public List<Pelicula> getPeliculas() { return peliculas; }
}
