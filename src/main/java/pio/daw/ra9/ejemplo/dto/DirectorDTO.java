package pio.daw.ra9.ejemplo.dto;

import pio.daw.ra9.ejemplo.modelo.Director;

public class DirectorDTO {

    private Long id;
    private String nombre;
    private String apellidos;
    private String nacionalidad;
    private Integer anioNacimiento;
    private int totalPeliculas;

    public DirectorDTO() {}

    public static DirectorDTO desde(Director d) {
        DirectorDTO dto = new DirectorDTO();
        dto.id = d.getId();
        dto.nombre = d.getNombre();
        dto.apellidos = d.getApellidos();
        dto.nacionalidad = d.getNacionalidad();
        dto.anioNacimiento = d.getAnioNacimiento();
        dto.totalPeliculas = d.getPeliculas().size();
        return dto;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getNacionalidad() { return nacionalidad; }
    public Integer getAnioNacimiento() { return anioNacimiento; }
    public int getTotalPeliculas() { return totalPeliculas; }
}
