package pio.daw.ra9.ejemplo.servicio;

import pio.daw.ra9.ejemplo.dto.PeliculaDTO;
import pio.daw.ra9.ejemplo.dto.PeliculaEstadisticasDTO;
import pio.daw.ra9.ejemplo.excepcion.RecursoNoEncontradoException;
import pio.daw.ra9.ejemplo.modelo.Pelicula;
import pio.daw.ra9.ejemplo.repositorio.PeliculaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class PeliculaService {

    private final PeliculaRepository peliculaRepo;

    public PeliculaService(PeliculaRepository peliculaRepo) {
        this.peliculaRepo = peliculaRepo;
    }

    @Transactional(readOnly = true)
    public List<PeliculaDTO> listarTodas() {
        return peliculaRepo.findAll().stream().map(PeliculaDTO::desde).toList();
    }

    @Transactional(readOnly = true)
    public PeliculaDTO buscarPorId(Long id) {
        Pelicula p = peliculaRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Película no encontrada con id " + id));
        return PeliculaDTO.desde(p);
    }

    public PeliculaDTO guardar(Pelicula pelicula) {
        return PeliculaDTO.desde(peliculaRepo.save(pelicula));
    }

    public PeliculaDTO actualizar(Long id, Pelicula datos) {
        Pelicula existente = peliculaRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Película no encontrada con id " + id));
        existente.setTitulo(datos.getTitulo());
        existente.setAnio(datos.getAnio());
        existente.setDuracion(datos.getDuracion());
        existente.setSinopsis(datos.getSinopsis());
        existente.setPrecioEntrada(datos.getPrecioEntrada());
        return PeliculaDTO.desde(peliculaRepo.save(existente));
    }

    public void eliminar(Long id) {
        if (!peliculaRepo.existsById(id)) {
            throw new RecursoNoEncontradoException("Película no encontrada con id " + id);
        }
        peliculaRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PeliculaDTO> buscarPorAnio(Integer anio) {
        return peliculaRepo.findByAnio(anio).stream().map(PeliculaDTO::desde).toList();
    }

    @Transactional(readOnly = true)
    public List<PeliculaDTO> buscarBaratas(BigDecimal maxPrecio) {
        return peliculaRepo.findBaratas(maxPrecio).stream().map(PeliculaDTO::desde).toList();
    }

    @Transactional(readOnly = true)
    public Page<PeliculaDTO> listarPaginadas(Pageable pageable) {
        return peliculaRepo.findAll(pageable).map(PeliculaDTO::desde);
    }

    @Transactional(readOnly = true)
    public PeliculaEstadisticasDTO estadisticas() {
        return new PeliculaEstadisticasDTO(
                peliculaRepo.avgPrecio(),
                peliculaRepo.maxPrecio(),
                peliculaRepo.minPrecio(),
                peliculaRepo.totalPeliculas()
        );
    }
}
