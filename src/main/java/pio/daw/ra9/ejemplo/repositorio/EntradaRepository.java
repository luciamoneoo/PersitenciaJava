package pio.daw.ra9.ejemplo.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import pio.daw.ra9.ejemplo.modelo.Entrada;

import java.util.List;
import java.util.Optional;

public interface EntradaRepository extends JpaRepository<Entrada, Long> {

    Optional<Entrada> findByLocalizador(String localizador);
    List<Entrada> findByProyeccionId(Long proyeccionId);
}
