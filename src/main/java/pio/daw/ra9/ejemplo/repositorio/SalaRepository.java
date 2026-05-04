package pio.daw.ra9.ejemplo.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import pio.daw.ra9.ejemplo.modelo.Sala;

import java.util.Optional;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    Optional<Sala> findByNombreIgnoreCase(String nombre);
    java.util.List<Sala> findByAforoGreaterThanEqual(Integer aforo);
}
