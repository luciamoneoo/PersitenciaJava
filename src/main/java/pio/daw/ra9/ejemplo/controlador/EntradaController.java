package pio.daw.ra9.ejemplo.controlador;

import pio.daw.ra9.ejemplo.dto.EntradaDTO;
import pio.daw.ra9.ejemplo.excepcion.RecursoNoEncontradoException;
import pio.daw.ra9.ejemplo.repositorio.EntradaRepository;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/entradas")
public class EntradaController {

    private final EntradaRepository entradaRepo;

    public EntradaController(EntradaRepository entradaRepo) {
        this.entradaRepo = entradaRepo;
    }

    // GET /entradas/{localizador}
    @GetMapping("/{localizador}")
    public EntradaDTO buscarPorLocalizador(@PathVariable String localizador) {
        return entradaRepo.findByLocalizador(localizador)
                .map(EntradaDTO::desde)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entrada no encontrada: " + localizador));
    }
}
