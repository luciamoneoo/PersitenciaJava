package pio.daw.ra9.ejemplo.servicio;

import pio.daw.ra9.ejemplo.excepcion.RecursoNoEncontradoException;
import pio.daw.ra9.ejemplo.modelo.Genero;
import pio.daw.ra9.ejemplo.repositorio.GeneroRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GeneroService {

    private final GeneroRepository generoRepo;

    public GeneroService(GeneroRepository generoRepo) {
        this.generoRepo = generoRepo;
    }

    @Transactional(readOnly = true)
    public List<Genero> listarTodos() {
        return generoRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Genero buscarPorId(Long id) {
        return generoRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Género no encontrado con id " + id));
    }

    public Genero guardar(Genero genero) {
        return generoRepo.save(genero);
    }

    public Genero actualizar(Long id, Genero datos) {
        Genero existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        return generoRepo.save(existente);
    }

    public void eliminar(Long id) {
        if (!generoRepo.existsById(id)) {
            throw new RecursoNoEncontradoException("Género no encontrado con id " + id);
        }
        generoRepo.deleteById(id);
    }
}
