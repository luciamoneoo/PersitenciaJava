package pio.daw.ra9.ejemplo.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sala")
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la sala no puede estar vacío")
    @Column(nullable = false, unique = true)
    private String nombre;

    @Positive(message = "El aforo debe ser mayor que cero")
    @Column(nullable = false)
    private Integer aforo;

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Proyeccion> proyecciones = new ArrayList<>();

    public Sala() {}

    public Sala(String nombre, Integer aforo) {
        this.nombre = nombre;
        this.aforo = aforo;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getAforo() { return aforo; }
    public void setAforo(Integer aforo) { this.aforo = aforo; }
    public List<Proyeccion> getProyecciones() { return proyecciones; }
}
