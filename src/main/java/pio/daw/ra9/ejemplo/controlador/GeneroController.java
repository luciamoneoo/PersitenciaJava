package pio.daw.ra9.ejemplo.controlador;

import pio.daw.ra9.ejemplo.modelo.Genero;
import pio.daw.ra9.ejemplo.servicio.GeneroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/generos")
public class GeneroController {

    private final GeneroService generoService;

    public GeneroController(GeneroService generoService) {
        this.generoService = generoService;
    }

    @GetMapping
    public List<Genero> listarTodos() {
        return generoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Genero buscarPorId(@PathVariable Long id) {
        return generoService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Genero> crear(@Valid @RequestBody Genero genero) {
        return ResponseEntity.status(HttpStatus.CREATED).body(generoService.guardar(genero));
    }

    @PutMapping("/{id}")
    public Genero actualizar(@PathVariable Long id, @Valid @RequestBody Genero genero) {
        return generoService.actualizar(id, genero);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        generoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
