package pio.daw.ra9.ejemplo.controlador;

import pio.daw.ra9.ejemplo.dto.PeliculaDTO;
import pio.daw.ra9.ejemplo.dto.PeliculaEstadisticasDTO;
import pio.daw.ra9.ejemplo.modelo.Pelicula;
import pio.daw.ra9.ejemplo.servicio.PeliculaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/peliculas")
public class PeliculaController {

    private final PeliculaService peliculaService;

    public PeliculaController(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }

    // GET /peliculas
    @GetMapping
    public List<PeliculaDTO> listarTodas() {
        return peliculaService.listarTodas();
    }

    // GET /peliculas/{id}
    @GetMapping("/{id}")
    public PeliculaDTO buscarPorId(@PathVariable Long id) {
        return peliculaService.buscarPorId(id);
    }

    // POST /peliculas  →  201 Created
    @PostMapping
    public ResponseEntity<PeliculaDTO> crear(@Valid @RequestBody Pelicula pelicula) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peliculaService.guardar(pelicula));
    }

    // PUT /peliculas/{id}
    @PutMapping("/{id}")
    public PeliculaDTO actualizar(@PathVariable Long id, @Valid @RequestBody Pelicula pelicula) {
        return peliculaService.actualizar(id, pelicula);
    }

    // DELETE /peliculas/{id}  →  204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        peliculaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /peliculas/anio/{anio}
    @GetMapping("/anio/{anio}")
    public List<PeliculaDTO> porAnio(@PathVariable Integer anio) {
        return peliculaService.buscarPorAnio(anio);
    }

    // GET /peliculas/baratas?max=8.50
    @GetMapping("/baratas")
    public List<PeliculaDTO> baratas(@RequestParam BigDecimal max) {
        return peliculaService.buscarBaratas(max);
    }

    // GET /peliculas/paginadas?page=0&size=4&sort=titulo,asc
    @GetMapping("/paginadas")
    public Page<PeliculaDTO> paginadas(Pageable pageable) {
        return peliculaService.listarPaginadas(pageable);
    }

    // GET /peliculas/estadisticas
    @GetMapping("/estadisticas")
    public PeliculaEstadisticasDTO estadisticas() {
        return peliculaService.estadisticas();
    }
}
