// ════════════════════════════════════════════════════════════
// app.js  –  Interfaz de usuario de CinemaApp (DAW RA9)
//
// Este archivo gestiona todo lo que el usuario VE e INTERACTÚA:
//  · Cambio de pestañas
//  · Renderizado de datos en tarjetas HTML
//  · Envío de formularios
//  · Mensajes de estado (cargando, éxito, error)
//
// Las llamadas HTTP están en api.js. Este archivo solo llama
// a las funciones definidas allí y actualiza el DOM con el
// resultado. El DOM (Document Object Model) es la representación
// en memoria del HTML que el navegador tiene cargado.
// ════════════════════════════════════════════════════════════


// ── 1. SELECCIÓN DE ELEMENTOS DEL DOM ────────────────────────
// Guardamos referencias a los elementos que vamos a usar muchas
// veces. Es más eficiente que buscarlos con getElementById
// en cada función.

const statusBar     = document.getElementById('status-bar');
const tabBtns       = document.querySelectorAll('.tab-btn');
const tabPanels     = document.querySelectorAll('.tab-panel');

const listaPeliculas   = document.getElementById('lista-peliculas');
const listaDirectores  = document.getElementById('lista-directores');
const listaGeneros     = document.getElementById('lista-generos');
const listaProyecciones = document.getElementById('lista-proyecciones');
const resultadoEntrada  = document.getElementById('resultado-entrada');


// ── 2. SISTEMA DE PESTAÑAS ────────────────────────────────────
// Cuando el usuario hace clic en una pestaña:
//  a) Ocultamos todos los paneles (añadimos clase 'd-none')
//  b) Mostramos el panel correspondiente (quitamos 'd-none')
//  c) Actualizamos qué botón tiene la clase 'active'
//  d) Cargamos los datos de ese panel si aún no se han cargado

function activarTab(nombreTab) {
  // a) Ocultar todos los paneles
  tabPanels.forEach(panel => panel.classList.add('d-none'));

  // b) Mostrar solo el panel activo
  const panelActivo = document.getElementById(`tab-${nombreTab}`);
  if (panelActivo) panelActivo.classList.remove('d-none');

  // c) Actualizar estilos de los botones
  tabBtns.forEach(btn => {
    btn.classList.toggle('active', btn.dataset.tab === nombreTab);
  });

  // d) Cargar datos al activar la pestaña por primera vez
  //    (solo si la lista está vacía o sigue mostrando el spinner)
  if (nombreTab === 'directores'   && !listaDirectores.dataset.cargado)   cargarDirectores();
  if (nombreTab === 'generos'      && !listaGeneros.dataset.cargado)      cargarGeneros();
  if (nombreTab === 'proyecciones' && !listaProyecciones.dataset.cargado) cargarProyecciones();
}

// Registramos el evento de clic en cada botón de pestaña
tabBtns.forEach(btn => {
  btn.addEventListener('click', () => activarTab(btn.dataset.tab));
});


// ── 3. MENSAJES DE ESTADO ─────────────────────────────────────
// Muestra un mensaje en la barra fija de arriba durante 3 segundos.
// tipo: 'loading' | 'success' | 'error'

let ocultarTimer = null;   // para cancelar el auto-cierre si llega otro mensaje

function mostrarMensaje(texto, tipo = 'loading') {
  // Limpiar el timer anterior (por si aún no se había ocultado)
  clearTimeout(ocultarTimer);

  // Actualizar texto y clase CSS
  statusBar.textContent = texto;
  statusBar.className   = `status-bar ${tipo} visible`;

  // En 'loading' no auto-ocultamos; esperamos a que llegue 'success' o 'error'
  if (tipo !== 'loading') {
    ocultarTimer = setTimeout(() => {
      statusBar.classList.remove('visible');
    }, 3000);
  }
}


// ── 4. HELPER: TOGGLE FORMULARIO ─────────────────────────────
// Muestra u oculta el formulario de creación de un recurso.
// Se llama desde los atributos onclick en el HTML.

function toggleForm(formId) {
  const form = document.getElementById(formId);
  form.classList.toggle('d-none');
}


// ════════════════════════════════════════════════════════════
// 5. PELÍCULAS
// ════════════════════════════════════════════════════════════

async function cargarPeliculas() {
  mostrarMensaje('Cargando películas…', 'loading');
  listaPeliculas.innerHTML = '<div class="loading-spinner">Cargando…</div>';

  try {
    // Llamamos a getPeliculas() de api.js → GET /peliculas
    const peliculas = await getPeliculas();
    renderizarPeliculas(peliculas);
    mostrarMensaje(`${peliculas.length} películas cargadas`, 'success');
  } catch (err) {
    listaPeliculas.innerHTML = `<p class="empty-state">Error: ${err.message}</p>`;
    mostrarMensaje(err.message, 'error');
  }
}

// Convierte el array de PeliculaDTO en tarjetas HTML.
// Para cada película creamos un string HTML con template literals
// (las comillas invertidas ` ` permiten incrustar variables con ${}).
function renderizarPeliculas(peliculas) {
  if (peliculas.length === 0) {
    listaPeliculas.innerHTML = '<p class="empty-state">No hay películas registradas.</p>';
    return;
  }

  // Array.map() transforma cada PeliculaDTO en un string HTML de tarjeta
  const html = peliculas.map(p => {
    // Convertimos el array de géneros en badges visuales
    const generosBadges = (p.generos || [])
      .map(g => `<span class="genero-badge">${g}</span>`)
      .join('');

    // Formateamos el precio como moneda europea (ej: "8,50 €")
    const precio = Number(p.precioEntrada).toLocaleString('es-ES', {
      style: 'currency', currency: 'EUR'
    });

    return `
      <div class="card-item">
        <div class="card-titulo">${p.titulo}</div>
        <div class="card-meta">${p.anio} · ${p.duracion} min · ${p.nombreDirector || '—'}</div>
        <div>${generosBadges}</div>
        ${p.sinopsis ? `<div class="card-meta" style="font-size:0.78rem;margin-top:0.3rem">${p.sinopsis.slice(0,100)}${p.sinopsis.length > 100 ? '…' : ''}</div>` : ''}
        <div class="precio-tag">${precio}</div>
        <div class="card-footer-actions">
          <button class="btn-danger" onclick="eliminarPeliculaUI(${p.id})">Eliminar</button>
        </div>
      </div>`;
  }).join('');

  listaPeliculas.innerHTML = html;
}

// Se llama cuando el usuario pulsa "Eliminar" en una tarjeta.
// Pide confirmación, llama a api.js y recarga la lista.
async function eliminarPeliculaUI(id) {
  if (!confirm('¿Eliminar esta película? Esta acción no se puede deshacer.')) return;

  mostrarMensaje('Eliminando…', 'loading');
  try {
    await eliminarPelicula(id);          // DELETE /peliculas/{id}
    mostrarMensaje('Película eliminada', 'success');
    cargarPeliculas();                   // Recargamos la lista completa
  } catch (err) {
    mostrarMensaje(err.message, 'error');
  }
}

// Formulario: nueva película
document.getElementById('crear-pelicula-form').addEventListener('submit', async (e) => {
  e.preventDefault(); // Evitamos que el formulario recargue la página

  // FormData recoge todos los campos del formulario en un objeto clave/valor
  const datos = Object.fromEntries(new FormData(e.target));

  mostrarMensaje('Creando película…', 'loading');
  try {
    await crearPelicula(datos);           // POST /peliculas
    e.target.reset();                     // Limpiamos el formulario
    toggleForm('form-nueva-pelicula');    // Lo ocultamos
    mostrarMensaje('Película creada', 'success');
    cargarPeliculas();
  } catch (err) {
    mostrarMensaje(err.message, 'error');
  }
});


// ════════════════════════════════════════════════════════════
// 6. DIRECTORES
// ════════════════════════════════════════════════════════════

async function cargarDirectores() {
  mostrarMensaje('Cargando directores…', 'loading');
  listaDirectores.innerHTML = '<div class="loading-spinner">Cargando…</div>';
  listaDirectores.dataset.cargado = 'true';

  try {
    const directores = await getDirectores();   // GET /directores
    renderizarDirectores(directores);
    mostrarMensaje(`${directores.length} directores cargados`, 'success');
  } catch (err) {
    listaDirectores.innerHTML = `<p class="empty-state">Error: ${err.message}</p>`;
    mostrarMensaje(err.message, 'error');
  }
}

function renderizarDirectores(directores) {
  if (directores.length === 0) {
    listaDirectores.innerHTML = '<p class="empty-state">No hay directores registrados.</p>';
    return;
  }

  listaDirectores.innerHTML = directores.map(d => `
    <div class="card-item">
      <div class="card-titulo">${d.nombre} ${d.apellidos}</div>
      <div class="card-meta">
        ${d.nacionalidad ? d.nacionalidad : 'Nacionalidad desconocida'}
        ${d.anioNacimiento ? ` · Nacido en ${d.anioNacimiento}` : ''}
      </div>
      <div><span class="stat-badge">🎬 ${d.totalPeliculas} película${d.totalPeliculas !== 1 ? 's' : ''}</span></div>
      <div class="card-footer-actions">
        <button class="btn-danger" onclick="eliminarDirectorUI(${d.id})">Eliminar</button>
      </div>
    </div>`).join('');
}

async function eliminarDirectorUI(id) {
  if (!confirm('¿Eliminar este director? Se eliminarán también sus películas.')) return;

  mostrarMensaje('Eliminando…', 'loading');
  try {
    await eliminarDirector(id);
    mostrarMensaje('Director eliminado', 'success');
    cargarDirectores();
  } catch (err) {
    mostrarMensaje(err.message, 'error');
  }
}

document.getElementById('crear-director-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const datos = Object.fromEntries(new FormData(e.target));

  mostrarMensaje('Creando director…', 'loading');
  try {
    await crearDirector(datos);
    e.target.reset();
    toggleForm('form-nuevo-director');
    mostrarMensaje('Director creado', 'success');
    cargarDirectores();
  } catch (err) {
    mostrarMensaje(err.message, 'error');
  }
});


// ════════════════════════════════════════════════════════════
// 7. GÉNEROS
// ════════════════════════════════════════════════════════════

async function cargarGeneros() {
  mostrarMensaje('Cargando géneros…', 'loading');
  listaGeneros.innerHTML = '<div class="loading-spinner">Cargando…</div>';
  listaGeneros.dataset.cargado = 'true';

  try {
    const generos = await getGeneros();     // GET /generos
    renderizarGeneros(generos);
    mostrarMensaje(`${generos.length} géneros cargados`, 'success');
  } catch (err) {
    listaGeneros.innerHTML = `<p class="empty-state">Error: ${err.message}</p>`;
    mostrarMensaje(err.message, 'error');
  }
}

function renderizarGeneros(generos) {
  if (generos.length === 0) {
    listaGeneros.innerHTML = '<p class="empty-state">No hay géneros registrados.</p>';
    return;
  }

  listaGeneros.innerHTML = generos.map(g => `
    <div class="card-item">
      <div class="card-titulo">${g.nombre}</div>
      ${g.descripcion ? `<div class="card-meta">${g.descripcion}</div>` : ''}
      <div class="card-footer-actions">
        <button class="btn-danger" onclick="eliminarGeneroUI(${g.id})">Eliminar</button>
      </div>
    </div>`).join('');
}

async function eliminarGeneroUI(id) {
  if (!confirm('¿Eliminar este género?')) return;

  mostrarMensaje('Eliminando…', 'loading');
  try {
    await eliminarGenero(id);
    mostrarMensaje('Género eliminado', 'success');
    cargarGeneros();
  } catch (err) {
    mostrarMensaje(err.message, 'error');
  }
}

document.getElementById('crear-genero-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const datos = Object.fromEntries(new FormData(e.target));

  mostrarMensaje('Creando género…', 'loading');
  try {
    await crearGenero(datos);
    e.target.reset();
    toggleForm('form-nuevo-genero');
    mostrarMensaje('Género creado', 'success');
    cargarGeneros();
  } catch (err) {
    mostrarMensaje(err.message, 'error');
  }
});


// ════════════════════════════════════════════════════════════
// 8. PROYECCIONES
// ════════════════════════════════════════════════════════════

async function cargarProyecciones() {
  mostrarMensaje('Cargando proyecciones…', 'loading');
  listaProyecciones.innerHTML = '<div class="loading-spinner">Cargando…</div>';
  listaProyecciones.dataset.cargado = 'true';

  try {
    const proyecciones = await getProyecciones();   // GET /proyecciones
    renderizarProyecciones(proyecciones);
    mostrarMensaje(`${proyecciones.length} proyecciones cargadas`, 'success');
  } catch (err) {
    listaProyecciones.innerHTML = `<p class="empty-state">Error: ${err.message}</p>`;
    mostrarMensaje(err.message, 'error');
  }
}

function renderizarProyecciones(proyecciones) {
  if (proyecciones.length === 0) {
    listaProyecciones.innerHTML = '<p class="empty-state">No hay proyecciones programadas.</p>';
    return;
  }

  listaProyecciones.innerHTML = proyecciones.map(p => {
    // La API devuelve la entidad Proyeccion directamente.
    // Accedemos a los campos anidados: p.pelicula.titulo, p.sala.nombre
    const titulo  = p.pelicula?.titulo  ?? 'Sin título';
    const sala    = p.sala?.nombre      ?? 'Sala desconocida';
    const libres  = p.asientosDisponibles;
    const classBadge = libres === 0 ? 'asientos-badge sin-asientos' : 'asientos-badge';
    const labelBadge = libres === 0 ? 'Agotado' : `${libres} asientos`;

    return `
      <div class="proyeccion-row">
        <div class="proy-titulo">${titulo}</div>
        <div class="proy-fecha">📅 ${p.fecha}</div>
        <div class="proy-hora">🕐 ${p.hora}</div>
        <div class="proy-sala">🏛️ ${sala}</div>
        <span class="${classBadge}">${labelBadge}</span>
        ${libres > 0
          ? `<button class="btn-comprar" onclick="comprarEntradaUI(${p.id})">🎟️ Comprar</button>`
          : ''}
      </div>`;
  }).join('');
}

// Compra una entrada y muestra el ticket en un modal
async function comprarEntradaUI(proyeccionId) {
  mostrarMensaje('Procesando compra…', 'loading');
  try {
    // POST /proyecciones/{id}/entradas → devuelve EntradaDTO
    const entrada = await comprarEntrada(proyeccionId);
    mostrarMensaje('¡Entrada comprada!', 'success');

    // Mostramos el ticket en el modal
    mostrarTicket(entrada);

    // Recargamos proyecciones para actualizar asientos disponibles
    cargarProyecciones();
  } catch (err) {
    mostrarMensaje(err.message, 'error');
  }
}

// Muestra el modal con los datos del ticket
function mostrarTicket(entrada) {
  const precio = Number(entrada.precio).toLocaleString('es-ES', {
    style: 'currency', currency: 'EUR'
  });

  document.getElementById('ticket-contenido').innerHTML = `
    <div class="ticket-row localizador">
      <span class="label">Localizador</span>
      <span class="valor">${entrada.localizador}</span>
    </div>
    <div class="ticket-row">
      <span class="label">Película</span>
      <span class="valor">${entrada.tituloPelicula}</span>
    </div>
    <div class="ticket-row">
      <span class="label">Fecha</span>
      <span class="valor">${entrada.fechaProyeccion}</span>
    </div>
    <div class="ticket-row">
      <span class="label">Hora</span>
      <span class="valor">${entrada.horaProyeccion}</span>
    </div>
    <div class="ticket-row">
      <span class="label">Sala</span>
      <span class="valor">${entrada.sala}</span>
    </div>
    <div class="ticket-row">
      <span class="label">Precio</span>
      <span class="valor" style="color:var(--cinema-gold)">${precio}</span>
    </div>`;

  document.getElementById('entrada-modal').classList.remove('d-none');
}


// ════════════════════════════════════════════════════════════
// 9. BUSCAR ENTRADA
// ════════════════════════════════════════════════════════════

document.getElementById('buscar-entrada-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const localizador = document.getElementById('input-localizador').value.trim();

  mostrarMensaje('Buscando entrada…', 'loading');
  resultadoEntrada.innerHTML = '';

  try {
    // GET /entradas/{localizador}
    const entrada = await getEntrada(localizador);
    renderizarEntrada(entrada);
    mostrarMensaje('Entrada encontrada', 'success');
  } catch (err) {
    resultadoEntrada.innerHTML = `<p class="empty-state" style="color:var(--cinema-red)">
      ${err.message}
    </p>`;
    mostrarMensaje(err.message, 'error');
  }
});

// Renderiza la EntradaDTO como una tarjeta tipo "resguardo de ticket"
function renderizarEntrada(entrada) {
  const precio = Number(entrada.precio).toLocaleString('es-ES', {
    style: 'currency', currency: 'EUR'
  });
  const fechaCompra = new Date(entrada.fechaCompra).toLocaleString('es-ES');

  resultadoEntrada.innerHTML = `
    <div class="entrada-resultado-card">
      <div class="ticket-header">🎟️ Entrada válida</div>
      <div class="ticket-row localizador">
        <span class="label">Localizador</span>
        <span class="valor">${entrada.localizador}</span>
      </div>
      <div class="ticket-row">
        <span class="label">Película</span>
        <span class="valor">${entrada.tituloPelicula}</span>
      </div>
      <div class="ticket-row">
        <span class="label">Fecha proyección</span>
        <span class="valor">${entrada.fechaProyeccion}</span>
      </div>
      <div class="ticket-row">
        <span class="label">Hora</span>
        <span class="valor">${entrada.horaProyeccion}</span>
      </div>
      <div class="ticket-row">
        <span class="label">Sala</span>
        <span class="valor">${entrada.sala}</span>
      </div>
      <div class="ticket-row">
        <span class="label">Precio</span>
        <span class="valor" style="color:var(--cinema-gold)">${precio}</span>
      </div>
      <div class="ticket-row">
        <span class="label">Comprada</span>
        <span class="valor">${fechaCompra}</span>
      </div>
    </div>`;
}


// ════════════════════════════════════════════════════════════
// 10. ARRANQUE DE LA APLICACIÓN
// ════════════════════════════════════════════════════════════
// Cuando el navegador termina de cargar el HTML, activamos la
// pestaña de películas y cargamos los datos iniciales.
// El evento 'DOMContentLoaded' garantiza que el DOM está listo.

document.addEventListener('DOMContentLoaded', () => {
  activarTab('peliculas');   // Mostramos la primera pestaña
  cargarPeliculas();         // Primera petición fetch al servidor
});
