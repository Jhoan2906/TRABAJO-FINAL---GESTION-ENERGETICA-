
/* ======================================
   GEMELO DIGITAL - HU-08
   Monitoreo de sensores IoT simulados
   ====================================== */

const API_URL = "http://localhost:8080/api/datos-sensor";
const INTERVALO_ACTUALIZACION = 10000;

// Elementos de la interfaz
const estadoConexion = document.getElementById(
    "estado-conexion"
);

const tablaMediciones = document.getElementById(
    "tabla-mediciones"
);

const botonActualizar = document.getElementById(
    "btn-actualizar"
);

// ======================================
// 1. SINCRONIZAR AMBOS MENUS
// ======================================

const enlacesMenu = document.querySelectorAll(
    ".menu-horizontal a, .menu-lateral nav a"
);

function activarSeccion(idSeccion) {

    enlacesMenu.forEach(enlace => {
        const activo =
            enlace.getAttribute("href") === idSeccion;

        enlace.classList.toggle("activo", activo);

        if (activo) {
            enlace.setAttribute("aria-current", "location");
        } else {
            enlace.removeAttribute("aria-current");
        }
    });
}

enlacesMenu.forEach(enlace => {

    enlace.addEventListener("click", () => {
        activarSeccion(enlace.getAttribute("href"));
    });

});

window.addEventListener("hashchange", () => {
    activarSeccion(window.location.hash || "#monitoreo");
});

activarSeccion(window.location.hash || "#monitoreo");

// ======================================
// 2. ESTADO DE CONEXION
// ======================================

function cambiarEstado(texto, conectado) {

    estadoConexion.textContent = texto;

    estadoConexion.classList.remove(
        "conectado",
        "desconectado"
    );

    estadoConexion.classList.add(
        conectado ? "conectado" : "desconectado"
    );
}

// ======================================
// 3. ACTUALIZAR INDICADORES
// ======================================

const indicadores = [
    {
        variable: "temperatura",
        elemento: "temperatura",
        decimales: 2
    },
    {
        variable: "humedad relativa",
        elemento: "humedad",
        decimales: 2
    },
    {
        variable: "potencia electrica",
        elemento: "potencia",
        decimales: 2
    },
    {
        variable: "energia electrica",
        elemento: "energia",
        decimales: 4
    }
];

function normalizarVariable(texto) {
    return String(texto || "")
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .trim()
        .toLowerCase();
}

function actualizarIndicadores(mediciones) {

    indicadores.forEach(indicador => {

        const elemento = document.getElementById(
            indicador.elemento
        );

        if (!elemento) return;

        const ultima = mediciones.find(m =>
            normalizarVariable(m.variable) ===
            indicador.variable
        );

        if (ultima && Number.isFinite(Number(ultima.valor))) {
            elemento.textContent = Number(
                ultima.valor
            ).toFixed(indicador.decimales);
        } else {
            elemento.textContent = "--";
        }
    });
}

// ======================================
// 4. MOSTRAR TABLA
// ======================================

function crearCelda(fila, texto) {
    const celda = document.createElement("td");
    celda.textContent = texto ?? "--";
    fila.appendChild(celda);
}

function mostrarTabla(mediciones) {

    tablaMediciones.replaceChildren();

    if (mediciones.length === 0) {

        const fila = document.createElement("tr");
        const celda = document.createElement("td");

        celda.colSpan = 6;
        celda.textContent = "No hay mediciones disponibles.";

        fila.appendChild(celda);
        tablaMediciones.appendChild(fila);
        return;
    }

    mediciones.forEach(medicion => {

        const fila = document.createElement("tr");

        const fechaUtc = medicion.timestamp
            ? medicion.timestamp.replace("T", " ")
                .substring(0, 19) + " UTC"
            : "--";

        crearCelda(fila, fechaUtc);
        crearCelda(fila, medicion.idSensor);
        crearCelda(fila, medicion.variable);
        crearCelda(fila, medicion.valor);
        crearCelda(fila, medicion.unidad);
        crearCelda(fila, medicion.idEquipo);

        tablaMediciones.appendChild(fila);
    });
}

// ======================================
// 5. CONSULTAR API SPRING BOOT
// ======================================

let consultaEnCurso = false;

async function cargarMediciones() {

    if (consultaEnCurso) return;

    consultaEnCurso = true;
    botonActualizar.disabled = true;

    try {

        // Consultar hasta 100 registros para localizar
        // la ultima lectura de cada variable.
        const respuesta = await fetch(
            API_URL + "?limite=100",
            { cache: "no-store" }
        );

        if (!respuesta.ok) {
            throw new Error(
                "Error HTTP " + respuesta.status
            );
        }

        const mediciones = await respuesta.json();

        if (!Array.isArray(mediciones)) {
            throw new Error("Respuesta inesperada de la API");
        }

        // El backend devuelve las lecturas mas
        // recientes primero.
        actualizarIndicadores(mediciones);

        // Mostrar solo las 10 ultimas en la tabla.
        mostrarTabla(mediciones.slice(0, 10));

        cambiarEstado("Conectado a Spring Boot", true);

    } catch (error) {

        console.error("Error al consultar mediciones:", error);

        cambiarEstado("Sin conexión con la API", false);

        // No conservar valores anteriores como si
        // fueran datos actuales.
        actualizarIndicadores([]);
        mostrarTabla([]);

    } finally {

        consultaEnCurso = false;
        botonActualizar.disabled = false;
    }
}

// ======================================
// 6. EVENTOS Y ACTUALIZACION PERIODICA
// ======================================

botonActualizar.addEventListener(
    "click",
    cargarMediciones
);

cargarMediciones();

setInterval(
    cargarMediciones,
    INTERVALO_ACTUALIZACION
);
