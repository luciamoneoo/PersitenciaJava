package pio.daw.ra9.ejemplo;

import pio.daw.ra9.ejemplo.modelo.*;
import pio.daw.ra9.ejemplo.repositorio.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Inserta datos de prueba al arrancar la aplicación si la base de datos está vacía.
 * Los alumnos pueden ver qué datos hay en MySQL con:
 *   docker exec -it mysql_ra9 mysql -u ra9user -pra9pass ra9db -e "SELECT * FROM pelicula;"
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final DirectorRepository directorRepo;
    private final GeneroRepository generoRepo;
    private final PeliculaRepository peliculaRepo;
    private final SalaRepository salaRepo;
    private final ProyeccionRepository proyeccionRepo;

    public DataLoader(DirectorRepository directorRepo, GeneroRepository generoRepo,
                      PeliculaRepository peliculaRepo, SalaRepository salaRepo,
                      ProyeccionRepository proyeccionRepo) {
        this.directorRepo = directorRepo;
        this.generoRepo = generoRepo;
        this.peliculaRepo = peliculaRepo;
        this.salaRepo = salaRepo;
        this.proyeccionRepo = proyeccionRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (peliculaRepo.count() > 0) return; // ya hay datos, no recargar

        // ── Géneros ────────────────────────────────────────────────
        Genero drama   = generoRepo.save(new Genero("Drama",     "Historias de personajes en situaciones emocionales"));
        Genero accion  = generoRepo.save(new Genero("Acción",    "Secuencias de alto impacto y aventura"));
        Genero scifi   = generoRepo.save(new Genero("Ciencia ficción", "Mundos futuros, tecnología y especulación"));
        Genero thriller = generoRepo.save(new Genero("Thriller", "Suspense y tensión narrativa"));

        // ── Directores ─────────────────────────────────────────────
        Director nolan  = directorRepo.save(new Director("Christopher", "Nolan",  "Británico",  1970));
        Director villeneuve = directorRepo.save(new Director("Denis",  "Villeneuve", "Canadiense", 1967));
        Director peele  = directorRepo.save(new Director("Jordan",    "Peele",   "Estadounidense", 1979));

        // ── Películas ──────────────────────────────────────────────
        Pelicula inception = crearPelicula("Inception",      2010, 148, "9.50", nolan,
            "Un ladrón que roba secretos a través de sueños recibe la misión de implantar una idea.",
            List.of(accion, scifi, thriller));

        Pelicula interstellar = crearPelicula("Interstellar", 2014, 169, "9.00", nolan,
            "Un grupo de astronautas viaja a través de un agujero de gusano en busca de un nuevo hogar.",
            List.of(drama, scifi));

        Pelicula dune = crearPelicula("Dune",                2021, 155, "8.50", villeneuve,
            "El hijo de una familia noble acepta su destino en un planeta desértico peligroso.",
            List.of(accion, scifi, drama));

        Pelicula arrival = crearPelicula("La llegada",       2016, 116, "7.50", villeneuve,
            "Una lingüista es reclutada para comunicarse con extraterrestres.",
            List.of(drama, scifi));

        Pelicula getOut = crearPelicula("Déjame salir",      2017, 104, "7.00", peele,
            "Un joven afroamericano visita a la familia de su novia y descubre algo perturbador.",
            List.of(thriller, drama));

        peliculaRepo.saveAll(List.of(inception, interstellar, dune, arrival, getOut));

        // ── Salas ──────────────────────────────────────────────────
        Sala sala1 = salaRepo.save(new Sala("Sala 1 – Gran Pantalla", 120));
        Sala sala2 = salaRepo.save(new Sala("Sala 2 – Premium",        60));
        Sala sala3 = salaRepo.save(new Sala("Sala 3",                  80));

        // ── Proyecciones ───────────────────────────────────────────
        LocalDate hoy = LocalDate.now();
        proyeccionRepo.save(new Proyeccion(hoy, LocalTime.of(17, 0), 120, inception, sala1));
        proyeccionRepo.save(new Proyeccion(hoy, LocalTime.of(20, 30), 60, dune, sala2));
        proyeccionRepo.save(new Proyeccion(hoy.plusDays(1), LocalTime.of(18, 15), 80, interstellar, sala3));
        proyeccionRepo.save(new Proyeccion(hoy.plusDays(1), LocalTime.of(21, 0), 0, getOut, sala1)); // agotada

        System.out.println(">>> DataLoader: base de datos inicializada con datos de ejemplo.");
    }

    private Pelicula crearPelicula(String titulo, int anio, int duracion, String precio,
                                    Director director, String sinopsis, List<Genero> generos) {
        Pelicula p = new Pelicula(titulo, anio, duracion, new BigDecimal(precio));
        p.setSinopsis(sinopsis);
        p.setDirector(director);
        p.setGeneros(generos);
        return p;
    }
}
