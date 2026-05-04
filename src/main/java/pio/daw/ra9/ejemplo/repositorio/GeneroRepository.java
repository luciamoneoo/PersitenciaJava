package pio.daw.ra9.ejemplo.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import pio.daw.ra9.ejemplo.modelo.Genero;

import java.util.Optional;

public interface GeneroRepository extends JpaRepository<Genero, Long> {

    Optional<Genero> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}
