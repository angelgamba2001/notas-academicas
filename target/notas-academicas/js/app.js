/**
 * Lógica de interacción de la página: captura del formulario, validación,
 * cálculo en pantalla y comunicación CRUD con el Servlet (API REST).
 */
(function () {
    const API_URL = "api/estudiantes";

    const form = document.getElementById("formEstudiante");
    const campoId = document.getElementById("estudianteId");
    const campoNombre = document.getElementById("nombre");
    const camposNota = {
        nota1: document.getElementById("nota1"),
        nota2: document.getElementById("nota2"),
        nota3: document.getElementById("nota3"),
        nota4: document.getElementById("nota4"),
    };

    const btnCalcular = document.getElementById("btnCalcular");
    const btnGuardar = document.getElementById("btnGuardar");
    const btnCancelar = document.getElementById("btnCancelar");
    const btnRefrescar = document.getElementById("btnRefrescar");

    const areaResultado = document.getElementById("areaResultado");
    const valorPromedio = document.getElementById("valorPromedio");
    const valorEstado = document.getElementById("valorEstado");
    const insignia = document.getElementById("insigniaRendimiento");

    const mensajeGlobal = document.getElementById("mensajeGlobal");
    const cuerpoTabla = document.getElementById("cuerpoTabla");
    const tablaVacia = document.getElementById("tablaVacia");

    const NOMBRES_CAMPOS = { nota1: "La nota 1", nota2: "La nota 2", nota3: "La nota 3", nota4: "La nota 4" };

    // ---------- Bloquear caracteres inválidos mientras se escribe (UX) ----------
    Object.values(camposNota).forEach((input) => {
        input.addEventListener("keydown", (evento) => {
            if (["-", "+", "e", "E"].includes(evento.key)) {
                evento.preventDefault();
            }
        });
        input.addEventListener("input", () => limpiarError(input));
    });
    campoNombre.addEventListener("input", () => limpiarError(campoNombre));

    // ---------- Validación del formulario ----------
    function limpiarError(input) {
        input.classList.remove("invalido");
        const small = document.getElementById("error" + capitalizar(input.id));
        if (small) small.textContent = "";
    }

    function capitalizar(texto) {
        return texto.charAt(0).toUpperCase() + texto.slice(1);
    }

    function marcarError(input, mensaje) {
        input.classList.add("invalido");
        const small = document.getElementById("error" + capitalizar(input.id));
        if (small) small.textContent = mensaje;
    }

    function validarFormulario() {
        let valido = true;

        const nombre = campoNombre.value.trim();
        if (nombre === "") {
            marcarError(campoNombre, "El nombre del estudiante es obligatorio.");
            valido = false;
        }

        const notas = {};
        for (const clave of Object.keys(camposNota)) {
            const input = camposNota[clave];
            const error = Estudiante.validarNota(input.value, NOMBRES_CAMPOS[clave]);
            if (error) {
                marcarError(input, error);
                valido = false;
            } else {
                notas[clave] = parseFloat(input.value);
            }
        }

        if (!valido) return { valido: false };

        const estudiante = new Estudiante(
            nombre, notas.nota1, notas.nota2, notas.nota3, notas.nota4,
            campoId.value ? parseInt(campoId.value, 10) : null
        );
        estudiante.calcularPromedio();
        estudiante.determinarRendimiento();
        return { valido: true, estudiante };
    }

    // ---------- Mensajes de retroalimentación ----------
    function mostrarMensaje(texto, tipo) {
        mensajeGlobal.textContent = texto;
        mensajeGlobal.className = "mensaje " + tipo;
        setTimeout(() => mensajeGlobal.classList.add("oculto"), 4000);
    }

    // ---------- Mostrar resultado calculado ----------
    function mostrarResultado(estudiante) {
        areaResultado.classList.remove("oculto");
        valorPromedio.textContent = estudiante.promedio.toFixed(1);
        valorEstado.textContent = estudiante.determinarAprobacion() ? "Aprobado" : "No aprobado";
        insignia.textContent = estudiante.resultado;
        insignia.className = "insignia " + estudiante.claseResultado();
    }

    btnCalcular.addEventListener("click", () => {
        const { valido, estudiante } = validarFormulario();
        if (!valido) {
            mostrarMensaje("Revisa los campos marcados antes de calcular.", "error");
            return;
        }
        mostrarResultado(estudiante);
    });

    // ---------- Guardar (crear o actualizar) ----------
    form.addEventListener("submit", async (evento) => {
        evento.preventDefault();
        const { valido, estudiante } = validarFormulario();
        if (!valido) {
            mostrarMensaje("No se puede guardar: revisa los campos marcados.", "error");
            return;
        }
        mostrarResultado(estudiante);

        const payload = {
            nombre: estudiante.nombre,
            nota1: estudiante.nota1,
            nota2: estudiante.nota2,
            nota3: estudiante.nota3,
            nota4: estudiante.nota4,
        };

        try {
            let respuesta;
            if (estudiante.id) {
                respuesta = await fetch(`${API_URL}?id=${estudiante.id}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload),
                });
            } else {
                respuesta = await fetch(API_URL, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload),
                });
            }

            const datos = await respuesta.json();
            if (!respuesta.ok) {
                mostrarMensaje(datos.error || "No se pudo guardar el registro.", "error");
                return;
            }

            mostrarMensaje(
                estudiante.id ? "Registro actualizado correctamente." : "Estudiante guardado correctamente.",
                "exito"
            );
            salirModoEdicion();
            form.reset();
            await cargarEstudiantes();
        } catch (error) {
            mostrarMensaje("No fue posible conectar con el servidor.", "error");
        }
    });

    // ---------- Modo edición ----------
    function entrarModoEdicion(estudiante) {
        campoId.value = estudiante.id;
        campoNombre.value = estudiante.nombre;
        camposNota.nota1.value = estudiante.nota1;
        camposNota.nota2.value = estudiante.nota2;
        camposNota.nota3.value = estudiante.nota3;
        camposNota.nota4.value = estudiante.nota4;
        btnGuardar.textContent = "Actualizar";
        btnCancelar.classList.remove("oculto");
        window.scrollTo({ top: 0, behavior: "smooth" });
    }

    function salirModoEdicion() {
        campoId.value = "";
        btnGuardar.textContent = "Guardar";
        btnCancelar.classList.add("oculto");
    }

    btnCancelar.addEventListener("click", () => {
        form.reset();
        salirModoEdicion();
        areaResultado.classList.add("oculto");
    });

    // ---------- Consulta de registros ----------
    async function cargarEstudiantes() {
        try {
            const respuesta = await fetch(API_URL);
            const lista = await respuesta.json();
            renderizarTabla(lista);
        } catch (error) {
            mostrarMensaje("No fue posible cargar los estudiantes registrados.", "error");
        }
    }

    function renderizarTabla(lista) {
        cuerpoTabla.innerHTML = "";
        tablaVacia.classList.toggle("oculto", lista.length > 0);

        lista.forEach((datos) => {
            const estudiante = new Estudiante(
                datos.nombre, datos.nota1, datos.nota2, datos.nota3, datos.nota4, datos.id
            );
            estudiante.promedio = datos.promedio;
            estudiante.resultado = datos.resultado;

            const fila = document.createElement("tr");
            fila.innerHTML = `
                <td>${escaparHtml(estudiante.nombre)}</td>
                <td>${estudiante.nota1.toFixed(1)}</td>
                <td>${estudiante.nota2.toFixed(1)}</td>
                <td>${estudiante.nota3.toFixed(1)}</td>
                <td>${estudiante.nota4.toFixed(1)}</td>
                <td><strong>${estudiante.promedio.toFixed(1)}</strong></td>
                <td><span class="etiqueta-resultado ${estudiante.claseResultado()}">${estudiante.resultado}</span></td>
                <td class="acciones-fila">
                    <button type="button" class="boton boton-secundario" data-accion="editar">Editar</button>
                    <button type="button" class="boton boton-peligro" data-accion="eliminar">Eliminar</button>
                </td>
            `;

            fila.querySelector('[data-accion="editar"]').addEventListener("click", () => entrarModoEdicion(estudiante));
            fila.querySelector('[data-accion="eliminar"]').addEventListener("click", () => eliminarEstudiante(estudiante));

            cuerpoTabla.appendChild(fila);
        });
    }

    async function eliminarEstudiante(estudiante) {
        const confirmado = window.confirm(`¿Eliminar el registro de "${estudiante.nombre}"? Esta acción no se puede deshacer.`);
        if (!confirmado) return;

        try {
            const respuesta = await fetch(`${API_URL}?id=${estudiante.id}`, { method: "DELETE" });
            const datos = await respuesta.json();
            if (!respuesta.ok) {
                mostrarMensaje(datos.error || "No se pudo eliminar el registro.", "error");
                return;
            }
            mostrarMensaje("Registro eliminado.", "exito");
            if (campoId.value == estudiante.id) {
                form.reset();
                salirModoEdicion();
            }
            await cargarEstudiantes();
        } catch (error) {
            mostrarMensaje("No fue posible conectar con el servidor.", "error");
        }
    }

    function escaparHtml(texto) {
        const div = document.createElement("div");
        div.textContent = texto;
        return div.innerHTML;
    }

    btnRefrescar.addEventListener("click", cargarEstudiantes);

    // ---------- Inicio ----------
    document.addEventListener("DOMContentLoaded", cargarEstudiantes);
})();
