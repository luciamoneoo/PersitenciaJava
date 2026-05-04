package pio.daw.ra9.ejemplo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import pio.daw.ra9.ejemplo.modelo.Entrada;

public class EntradaDTO {

    private Long id;
    private String localizador;
    private LocalDateTime fechaCompra;
    private BigDecimal precio;
    private String tituloPelicula;
    private LocalDate fechaProyeccion;
    private LocalTime horaProyeccion;
    private String sala;

    public EntradaDTO() {}

    public static EntradaDTO desde(Entrada e) {
        EntradaDTO dto = new EntradaDTO();
        dto.id = e.getId();
        dto.localizador = e.getLocalizador();
        dto.fechaCompra = e.getFechaCompra();
        dto.precio = e.getPrecio();
        if (e.getProyeccion() != null) {
            dto.fechaProyeccion = e.getProyeccion().getFecha();
            dto.horaProyeccion = e.getProyeccion().getHora();
            if (e.getProyeccion().getPelicula() != null) {
                dto.tituloPelicula = e.getProyeccion().getPelicula().getTitulo();
            }
            if (e.getProyeccion().getSala() != null) {
                dto.sala = e.getProyeccion().getSala().getNombre();
            }
        }
        return dto;
    }

    public Long getId() { return id; }
    public String getLocalizador() { return localizador; }
    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public BigDecimal getPrecio() { return precio; }
    public String getTituloPelicula() { return tituloPelicula; }
    public LocalDate getFechaProyeccion() { return fechaProyeccion; }
    public LocalTime getHoraProyeccion() { return horaProyeccion; }
    public String getSala() { return sala; }
}
