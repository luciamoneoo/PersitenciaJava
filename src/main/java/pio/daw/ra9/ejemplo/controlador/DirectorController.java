package pio.daw.ra9.ejemplo.controlador;

import pio.daw.ra9.ejemplo.dto.DirectorDTO;
import pio.daw.ra9.ejemplo.dto.PeliculaDTO;
import pio.daw.ra9.ejemplo.modelo.Director;
import pio.daw.ra9.ejemplo.modelo.Pelicula;
import pio.daw.ra9.ejemplo.servicio.DirectorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/directores")
public class DirectorController {

    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    // GET /directores
    @GetMapping
    public List<DirectorDTO> listarTodos() {
        return directorService.listarTodos();
    }

    // GET /directores/{id}
    @GetMapping("/{id}")
    public DirectorDTO buscarPorId(@PathVariable Long id) {
        return directorService.buscarPorId(id);
    }

    // POST /directores  →  201 Created
    @PostMapping
    public ResponseEntity<DirectorDTO> crear(@Valid @RequestBody Director director) {
        return ResponseEntity.status(HttpStatus.CREATED).body(directorService.guardar(director));
    }

    // PUT /directores/{id}
    @PutMapping("/{id}")
    public DirectorDTO actualizar(@PathVariable Long id, @Valid @RequestBody Director director) {
        return directorService.actualizar(id, director);
    }

    // DELETE /directores/{id}  →  204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        directorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /directores/{id}/peliculas
    @GetMapping("/{id}/peliculas")
    public List<PeliculaDTO> peliculasDeDirector(@PathVariable Long id) {
        return directorService.peliculasDeDirector(id);
    }

    // POST /directores/{id}/peliculas  →  201 Created
    @PostMapping("/{id}/peliculas")
    public ResponseEntity<PeliculaDTO> agregarPelicula(@PathVariable Long id,
                                                        @Valid @RequestBody Pelicula pelicula) {
        return ResponseEntity.status(HttpStatus.CREATED).body(directorService.agregarPelicula(id, pelicula));
    }
}
