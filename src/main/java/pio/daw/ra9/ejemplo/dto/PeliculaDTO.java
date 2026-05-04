package pio.daw.ra9.ejemplo.dto;

import java.math.BigDecimal;
import java.util.List;

import pio.daw.ra9.ejemplo.modelo.Pelicula;

/**
 * DTO para Pelicula: evita la referencia circular Director↔Pelicula en JSON
 * e incluye solo los campos relevantes para el cliente.
 */
public class PeliculaDTO {

    private Long id;
    private String titulo;
    private Integer anio;
    private Integer duracion;
    private String sinopsis;
    private BigDecimal precioEntrada;
    private String nombreDirector;
    private List<String> generos;

    public PeliculaDTO() {}

    public static PeliculaDTO desde(Pelicula p) {
        PeliculaDTO dto = new PeliculaDTO();
        dto.id = p.getId();
        dto.titulo = p.getTitulo();
        dto.anio = p.getAnio();
        dto.duracion = p.getDuracion();
        dto.sinopsis = p.getSinopsis();
        dto.precioEntrada = p.getPrecioEntrada();
        if (p.getDirector() != null) {
            dto.nombreDirector = p.getDirector().getNombre() + " " + p.getDirector().getApellidos();
        }
        dto.generos = p.getGeneros().stream().map(g -> g.getNombre()).toList();
        return dto;
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public Integer getAnio() { return anio; }
    public Integer getDuracion() { return duracion; }
    public String getSinopsis() { return sinopsis; }
    public BigDecimal getPrecioEntrada() { return precioEntrada; }
    public String getNombreDirector() { return nombreDirector; }
    public List<String> getGeneros() { return generos; }
}
