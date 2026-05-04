package pio.daw.ra9.ejemplo.controlador;

import pio.daw.ra9.ejemplo.dto.EntradaDTO;
import pio.daw.ra9.ejemplo.modelo.Proyeccion;
import pio.daw.ra9.ejemplo.servicio.ProyeccionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proyecciones")
public class ProyeccionController {

    private final ProyeccionService proyeccionService;

    public ProyeccionController(ProyeccionService proyeccionService) {
        this.proyeccionService = proyeccionService;
    }

    @GetMapping
    public List<Proyeccion> listarTodas() {
        return proyeccionService.listarTodas();
    }

    @GetMapping("/{id}")
    public Proyeccion buscarPorId(@PathVariable Long id) {
        return proyeccionService.buscarPorId(id);
    }

    // POST /proyecciones?peliculaId=1&salaId=2
    @PostMapping
    public ResponseEntity<Proyeccion> crear(@RequestParam Long peliculaId,
                                             @RequestParam Long salaId,
                                             @Valid @RequestBody Proyeccion proyeccion) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(proyeccionService.guardar(peliculaId, salaId, proyeccion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        proyeccionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // POST /proyecciones/{id}/entradas  →  201 Created
    @PostMapping("/{id}/entradas")
    public ResponseEntity<EntradaDTO> comprarEntrada(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(proyeccionService.comprarEntrada(id));
    }
}
