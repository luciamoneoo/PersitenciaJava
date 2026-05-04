// ════════════════════════════════════════════════════════════
// api.js  –  Funciones para comunicarse con la REST API
//            de Spring Boot (CinemaApp · DAW RA9)
//
// Cada función aquí representa una llamada HTTP a un endpoint
// de nuestra API. El objetivo es que puedas ver claramente
// cómo se usa fetch() para hacer peticiones GET, POST y DELETE.
//
// Todas las funciones son "async": devuelven una Promesa que
// se resuelve con el objeto JavaScript ya parseado desde JSON.
// ════════════════════════════════════════════════════════════


// ── URL base ─────────────────────────────────────────────────
// Usamos rutas RELATIVAS (sin http://localhost:8080) porque el
// HTML lo sirve el MISMO servidor Spring Boot. El navegador
// añade automáticamente el host y el puerto actuales.
const API = {
  peliculas:   '/peliculas',
  directores:  '/directores',
  generos:     '/generos',
  proyecciones: '/proyecciones',
  entradas:    '/entradas',
};


// ════════════════════════════════════════════════════════════
// FUNCIÓN AUXILIAR: peticion()
//
// Es el núcleo de toda la comunicación HTTP. Las funciones
// públicas de abajo la llaman con distintos parámetros.
//
// Parámetros:
//   url     → la ruta del endpoint, p. ej. "/peliculas"
//   metodo  → "GET", "POST", "PUT" o "DELETE"
//   cuerpo  → objeto JavaScript a enviar como JSON (o null)
//
// Devuelve: el objeto JavaScript ya parseado, o null si la
//           respuesta no tiene cuerpo (HTTP 204 No Content).
// ════════════════════════════════════════════════════════════
async function peticion(url, metodo = 'GET', cuerpo = null) {

  // 1. Construimos las opciones que pasaremos a fetch()
  const opciones = {
    method: metodo,
    headers: {},
  };

  // 2. Si hay un cuerpo (p. ej. en POST/PUT), lo convertimos a
  //    JSON con JSON.stringify() e indicamos el tipo con un header.
  //    Sin este header el servidor no sabría que estamos enviando JSON.
  if (cuerpo !== null) {
    opciones.headers['Content-Type'] = 'application/json';
    opciones.body = JSON.stringify(cuerpo);
  }

  // 3. Ejecutamos la petición HTTP. "await" pausa la función hasta
  //    que el servidor responde. Durante ese tiempo el navegador sigue
  //    funcionando con normalidad (no se bloquea).
  const respuesta = await fetch(url, opciones);

  // 4. Comprobamos si la petición fue exitosa.
  //    respuesta.ok es true cuando el código HTTP está entre 200 y 299.
  //    Si no fue exitosa (p. ej. 404, 400, 500), lanzamos un Error.
  if (!respuesta.ok) {
    // Intentamos leer el mensaje de error que envía Spring Boot
    let mensajeError = `Error HTTP ${respuesta.status}`;
    try {
      const errorJson = await respuesta.json();
      if (errorJson.mensaje) mensajeError = errorJson.mensaje;
    } catch (_) { /* la respuesta de error no tenía JSON */ }
    throw new Error(mensajeError);
  }

  // 5. Si el código es 204 (No Content), la respuesta no tiene cuerpo.
  //    Esto ocurre con DELETE. Devolvemos null para indicar "sin datos".
  if (respuesta.status === 204) return null;

  // 6. Parseamos el JSON de la respuesta. El método .json() también es
  //    asíncrono, por eso necesitamos otro "await".
  return respuesta.json();
}


// ════════════════════════════════════════════════════════════
// PELÍCULAS
// ════════════════════════════════════════════════════════════

/**
 * Obtiene la lista de todas las películas.
 * Petición: GET /peliculas
 * Respuesta: array de PeliculaDTO [{id, titulo, anio, duracion,
 *            sinopsis, precioEntrada, nombreDirector, generos:[]}]
 */
async function getPeliculas() {
  return peticion(API.peliculas);
}

/**
 * Crea una nueva película.
 * Petición: POST /peliculas   (cuerpo: datos de la película en JSON)
 * Respuesta: PeliculaDTO con el id asignado (HTTP 201 Created)
 *
 * @param {object} datos - Ejemplo: { titulo, anio, duracion, precioEntrada, directorId }
 */
async function crearPelicula(datos) {
  // Convertimos los campos numéricos (llegan como strings del formulario)
  const cuerpo = {
    titulo:        datos.titulo,
    anio:          Number(datos.anio),
    duracion:      Number(datos.duracion),
    sinopsis:      datos.sinopsis || null,
    precioEntrada: Number(datos.precioEntrada),
    director: { id: Number(datos.directorId) },
  };
  return peticion(API.peliculas, 'POST', cuerpo);
}

/**
 * Elimina una película por su ID.
 * Petición: DELETE /peliculas/{id}
 * Respuesta: HTTP 204 No Content (sin cuerpo → devuelve null)
 *
 * @param {number} id - ID de la película a eliminar
 */
async function eliminarPelicula(id) {
  return peticion(`${API.peliculas}/${id}`, 'DELETE');
}


// ════════════════════════════════════════════════════════════
// DIRECTORES
// ════════════════════════════════════════════════════════════

/**
 * Obtiene la lista de todos los directores.
 * Petición: GET /directores
 * Respuesta: array de DirectorDTO [{id, nombre, apellidos,
 *            nacionalidad, anioNacimiento, totalPeliculas}]
 */
async function getDirectores() {
  return peticion(API.directores);
}

/**
 * Crea un nuevo director.
 * Petición: POST /directores
 * Respuesta: DirectorDTO (HTTP 201 Created)
 *
 * @param {object} datos - { nombre, apellidos, nacionalidad, anioNacimiento }
 */
async function crearDirector(datos) {
  const cuerpo = {
    nombre:          datos.nombre,
    apellidos:       datos.apellidos,
    nacionalidad:    datos.nacionalidad  || null,
    anioNacimiento:  datos.anioNacimiento ? Number(datos.anioNacimiento) : null,
  };
  return peticion(API.directores, 'POST', cuerpo);
}

/**
 * Elimina un director por su ID.
 * Petición: DELETE /directores/{id}
 * Respuesta: HTTP 204 No Content
 */
async function eliminarDirector(id) {
  return peticion(`${API.directores}/${id}`, 'DELETE');
}


// ════════════════════════════════════════════════════════════
// GÉNEROS
// ════════════════════════════════════════════════════════════

/**
 * Obtiene la lista de todos los géneros.
 * Petición: GET /generos
 * Respuesta: array de Genero [{id, nombre, descripcion}]
 */
async function getGeneros() {
  return peticion(API.generos);
}

/**
 * Crea un nuevo género.
 * Petición: POST /generos
 * Respuesta: Genero (HTTP 201 Created)
 */
async function crearGenero(datos) {
  const cuerpo = {
    nombre:      datos.nombre,
    descripcion: datos.descripcion || null,
  };
  return peticion(API.generos, 'POST', cuerpo);
}

/**
 * Elimina un género por su ID.
 * Petición: DELETE /generos/{id}
 * Respuesta: HTTP 204 No Content
 */
async function eliminarGenero(id) {
  return peticion(`${API.generos}/${id}`, 'DELETE');
}


// ════════════════════════════════════════════════════════════
// PROYECCIONES
// ════════════════════════════════════════════════════════════

/**
 * Obtiene todas las proyecciones programadas.
 * Petición: GET /proyecciones
 * Respuesta: array de Proyeccion [{id, fecha, hora,
 *            asientosDisponibles, pelicula:{...}, sala:{...}}]
 */
async function getProyecciones() {
  return peticion(API.proyecciones);
}

/**
 * Compra una entrada para una proyección.
 * Petición: POST /proyecciones/{id}/entradas
 * Respuesta: EntradaDTO {localizador, fechaCompra, precio,
 *            tituloPelicula, fechaProyeccion, horaProyeccion, sala}
 *
 * @param {number} proyeccionId - ID de la proyección
 */
async function comprarEntrada(proyeccionId) {
  // No hay body: el servidor crea la entrada automáticamente
  // con la información de la proyección.
  return peticion(`${API.proyecciones}/${proyeccionId}/entradas`, 'POST');
}


// ════════════════════════════════════════════════════════════
// ENTRADAS
// ════════════════════════════════════════════════════════════

/**
 * Busca una entrada por su localizador (UUID).
 * Petición: GET /entradas/{localizador}
 * Respuesta: EntradaDTO con todos los datos de la entrada
 *
 * @param {string} localizador - UUID de la entrada
 */
async function getEntrada(localizador) {
  return peticion(`${API.entradas}/${localizador}`);
}
