package pio.daw.ra9.ejemplo.servicio;

import pio.daw.ra9.ejemplo.dto.DirectorDTO;
import pio.daw.ra9.ejemplo.dto.PeliculaDTO;
import pio.daw.ra9.ejemplo.excepcion.RecursoNoEncontradoException;
import pio.daw.ra9.ejemplo.modelo.Director;
import pio.daw.ra9.ejemplo.modelo.Pelicula;
import pio.daw.ra9.ejemplo.repositorio.DirectorRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DirectorService {

    private final DirectorRepository directorRepo;

    public DirectorService(DirectorRepository directorRepo) {
        this.directorRepo = directorRepo;
    }

    @Transactional(readOnly = true)
    public List<DirectorDTO> listarTodos() {
        return directorRepo.findAll().stream().map(DirectorDTO::desde).toList();
    }

    @Transactional(readOnly = true)
    public DirectorDTO buscarPorId(Long id) {
        Director d = directorRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Director no encontrado con id " + id));
        return DirectorDTO.desde(d);
    }

    public DirectorDTO guardar(Director director) {
        return DirectorDTO.desde(directorRepo.save(director));
    }

    public DirectorDTO actualizar(Long id, Director datos) {
        Director existente = directorRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Director no encontrado con id " + id));
        existente.setNombre(datos.getNombre());
        existente.setApellidos(datos.getApellidos());
        existente.setNacionalidad(datos.getNacionalidad());
        existente.setAnioNacimiento(datos.getAnioNacimiento());
        return DirectorDTO.desde(directorRepo.save(existente));
    }

    public void eliminar(Long id) {
        if (!directorRepo.existsById(id)) {
            throw new RecursoNoEncontradoException("Director no encontrado con id " + id);
        }
        directorRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PeliculaDTO> peliculasDeDirector(Long id) {
        Director d = directorRepo.findByIdConPeliculas(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Director no encontrado con id " + id));
        return d.getPeliculas().stream().map(PeliculaDTO::desde).toList();
    }

    public PeliculaDTO agregarPelicula(Long directorId, Pelicula pelicula) {
        Director d = directorRepo.findById(directorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Director no encontrado con id " + directorId));
        d.addPelicula(pelicula);
        directorRepo.save(d);
        return PeliculaDTO.desde(pelicula);
    }
}
