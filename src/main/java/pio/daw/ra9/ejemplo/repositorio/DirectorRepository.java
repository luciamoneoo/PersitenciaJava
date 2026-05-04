package pio.daw.ra9.ejemplo.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pio.daw.ra9.ejemplo.modelo.Director;

import java.util.List;

public interface DirectorRepository extends JpaRepository<Director, Long> {

    List<Director> findByNacionalidad(String nacionalidad);
    List<Director> findByNombreContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombre, String apellidos);

    @Query("SELECT d FROM Director d LEFT JOIN FETCH d.peliculas WHERE d.id = :id")
    java.util.Optional<Director> findByIdConPeliculas(@Param("id") Long id);
}
