package pio.daw.ra9.ejemplo.dto;

import java.math.BigDecimal;

public class PeliculaEstadisticasDTO {

    private BigDecimal precioPromedio;
    private BigDecimal precioMaximo;
    private BigDecimal precioMinimo;
    private Long totalPeliculas;

    public PeliculaEstadisticasDTO() {}

    public PeliculaEstadisticasDTO(BigDecimal precioPromedio, BigDecimal precioMaximo,
                                   BigDecimal precioMinimo, Long totalPeliculas) {
        this.precioPromedio = precioPromedio;
        this.precioMaximo = precioMaximo;
        this.precioMinimo = precioMinimo;
        this.totalPeliculas = totalPeliculas;
    }

    public BigDecimal getPrecioPromedio() { return precioPromedio; }
    public BigDecimal getPrecioMaximo() { return precioMaximo; }
    public BigDecimal getPrecioMinimo() { return precioMinimo; }
    public Long getTotalPeliculas() { return totalPeliculas; }
}
