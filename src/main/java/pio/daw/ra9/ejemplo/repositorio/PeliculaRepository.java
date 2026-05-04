package pio.daw.ra9.ejemplo.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pio.daw.ra9.ejemplo.modelo.Pelicula;

import java.math.BigDecimal;
import java.util.List;

public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {

    // Métodos derivados — Spring genera el SQL automáticamente
    List<Pelicula> findByAnio(Integer anio);
    List<Pelicula> findByDuracionLessThan(Integer maxMinutos);
    List<Pelicula> findByDirectorNacionalidad(String nacionalidad);
    List<Pelicula> findByTituloContainingIgnoreCase(String texto);

    // Paginación
    Page<Pelicula> findAll(Pageable pageable);

    // @Query JPQL — misma sintaxis que RA8
    @Query("SELECT p FROM Pelicula p WHERE p.precioEntrada <= :max ORDER BY p.precioEntrada ASC")
    List<Pelicula> findBaratas(@Param("max") BigDecimal max);

    @Query("SELECT AVG(p.precioEntrada) FROM Pelicula p")
    BigDecimal avgPrecio();

    @Query("SELECT MAX(p.precioEntrada) FROM Pelicula p")
    BigDecimal maxPrecio();

    @Query("SELECT MIN(p.precioEntrada) FROM Pelicula p")
    BigDecimal minPrecio();

    @Query("SELECT COUNT(p) FROM Pelicula p")
    Long totalPeliculas();

    // SQL nativo — útil cuando JPQL no es suficiente
    @Query(value = "SELECT * FROM pelicula WHERE anio BETWEEN :inicio AND :fin ORDER BY anio DESC",
           nativeQuery = true)
    List<Pelicula> findByRangoAnio(@Param("inicio") int inicio, @Param("fin") int fin);
}
