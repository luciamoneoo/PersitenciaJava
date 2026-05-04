package pio.daw.ra9.ejemplo.servicio;

import pio.daw.ra9.ejemplo.dto.EntradaDTO;
import pio.daw.ra9.ejemplo.excepcion.RecursoNoEncontradoException;
import pio.daw.ra9.ejemplo.excepcion.SinAsientosException;
import pio.daw.ra9.ejemplo.modelo.Entrada;
import pio.daw.ra9.ejemplo.modelo.Pelicula;
import pio.daw.ra9.ejemplo.modelo.Proyeccion;
import pio.daw.ra9.ejemplo.modelo.Sala;
import pio.daw.ra9.ejemplo.repositorio.EntradaRepository;
import pio.daw.ra9.ejemplo.repositorio.PeliculaRepository;
import pio.daw.ra9.ejemplo.repositorio.ProyeccionRepository;
import pio.daw.ra9.ejemplo.repositorio.SalaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProyeccionService {

    private final ProyeccionRepository proyeccionRepo;
    private final PeliculaRepository peliculaRepo;
    private final SalaRepository salaRepo;
    private final EntradaRepository entradaRepo;

    public ProyeccionService(ProyeccionRepository proyeccionRepo,
                             PeliculaRepository peliculaRepo,
                             SalaRepository salaRepo,
                             EntradaRepository entradaRepo) {
        this.proyeccionRepo = proyeccionRepo;
        this.peliculaRepo = peliculaRepo;
        this.salaRepo = salaRepo;
        this.entradaRepo = entradaRepo;
    }

    @Transactional(readOnly = true)
    public List<Proyeccion> listarTodas() {
        return proyeccionRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Proyeccion buscarPorId(Long id) {
        return proyeccionRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proyección no encontrada con id " + id));
    }

    public Proyeccion guardar(Long peliculaId, Long salaId, Proyeccion proyeccion) {
        Pelicula pelicula = peliculaRepo.findById(peliculaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Película no encontrada con id " + peliculaId));
        Sala sala = salaRepo.findById(salaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sala no encontrada con id " + salaId));
        proyeccion.setPelicula(pelicula);
        proyeccion.setSala(sala);
        if (proyeccion.getAsientosDisponibles() == null) {
            proyeccion.setAsientosDisponibles(sala.getAforo());
        }
        return proyeccionRepo.save(proyeccion);
    }

    public void eliminar(Long id) {
        if (!proyeccionRepo.existsById(id)) {
            throw new RecursoNoEncontradoException("Proyección no encontrada con id " + id);
        }
        proyeccionRepo.deleteById(id);
    }

    /**
     * Compra una entrada para la proyección indicada.
     *
     * Toda la operación ocurre en una única transacción: si no hay asientos
     * o falla el INSERT de la entrada, @Transactional hace rollback automático
     * y asientos_disponibles queda intacto en MySQL.
     */
    @Transactional
    public EntradaDTO comprarEntrada(Long proyeccionId) {
        Proyeccion proyeccion = proyeccionRepo.findById(proyeccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proyección no encontrada con id " + proyeccionId));

        if (proyeccion.getAsientosDisponibles() <= 0) {
            throw new SinAsientosException(proyeccionId);
        }

        proyeccion.setAsientosDisponibles(proyeccion.getAsientosDisponibles() - 1);
        proyeccionRepo.save(proyeccion);

        Entrada entrada = new Entrada(proyeccion.getPelicula().getPrecioEntrada(), proyeccion);
        entradaRepo.save(entrada);

        return EntradaDTO.desde(entrada);
    }
}
