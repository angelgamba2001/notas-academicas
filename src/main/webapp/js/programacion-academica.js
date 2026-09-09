// Cambiar entre pestañas
document.querySelectorAll('.tab-btn').forEach(boton => {
    boton.addEventListener('click', () => {
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('activo'));
        document.querySelectorAll('.tab-contenido').forEach(c => c.classList.remove('activo'));

        boton.classList.add('activo');
        document.getElementById(boton.dataset.tab).classList.add('activo');

        cargarDatosDeTab(boton.dataset.tab);
    });
});

// Marca la pestaña de Dashboard como activa desde el inicio
document.querySelector('[data-tab="dashboard"]').classList.add('activo');

// Decide qué datos cargar según la pestaña que se abrió
function cargarDatosDeTab(tab) {
    if (tab === 'dashboard') cargarDashboard();
    if (tab === 'cursos') cargarCursos();
    if (tab === 'docentes') cargarDocentes();
    if (tab === 'asignaturas') cargarAsignaturas();
    if (tab === 'horarios') { cargarHorarios(); cargarSelectsHorario(); }
    if (tab === 'horasextra') cargarSelectsHorasExtra();
}

function cargarSelectsHorasExtra() {
    fetch('api/docentes')
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById('heIdDocente');
            select.innerHTML = data.map(d => `<option value="${d.idDocente}">${d.nombre} ${d.apellido}</option>`).join('');
            cargarHorasExtra(); // carga las del primer docente por defecto
        });
}

function cargarHorasExtra() {
    const idDocente = document.getElementById('heIdDocente').value;
    if (!idDocente) return;

    fetch('api/horas-extra?idDocente=' + idDocente)
        .then(res => res.json())
        .then(data => {
            document.getElementById('totalesHoraExtra').innerHTML =
                `Total esta semana: <b>${data.totalSemana} / 1 hora</b> &nbsp;|&nbsp; Total este mes: <b>${data.totalMes} / 4 horas</b>`;

            const tbody = document.querySelector('#tablaHorasExtra tbody');
            tbody.innerHTML = '';
            data.registros.forEach(r => {
                tbody.innerHTML += `<tr><td>${r.fecha}</td><td>${r.horas}</td></tr>`;
            });
        })
        .catch(err => console.error('Error cargando horas extra:', err));
}

document.getElementById('heIdDocente').addEventListener('change', cargarHorasExtra);

document.getElementById('formHoraExtra').addEventListener('submit', function(e) {
    e.preventDefault();

    const datos = {
        idDocente: parseInt(document.getElementById('heIdDocente').value),
        fecha: document.getElementById('heFecha').value,
        horas: parseFloat(document.getElementById('heHoras').value)
    };

    const mensajeEl = document.getElementById('mensajeHoraExtra');

    fetch('api/horas-extra', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(datos)
    })
    .then(res => res.json().then(body => ({ status: res.status, body })))
    .then(({ status, body }) => {
        if (status === 201) {
            mensajeEl.className = 'mensaje exito';
            mensajeEl.textContent = body.mensaje;
            document.getElementById('formHoraExtra').reset();
            cargarHorasExtra();
        } else {
            mensajeEl.className = 'mensaje error';
            mensajeEl.textContent = body.error;
        }
    })
    .catch(err => {
        mensajeEl.className = 'mensaje error';
        mensajeEl.textContent = 'Error inesperado: ' + err;
    });
});

// --- DASHBOARD ---
function cargarDashboard() {
    fetch('api/dashboard')
        .then(res => res.json())
        .then(data => {
            document.getElementById('dashCursos').textContent = data.totalCursos;
            document.getElementById('dashDocentes').textContent = data.totalDocentes;
            document.getElementById('dashAsignaturas').textContent = data.totalAsignaturas;
            document.getElementById('dashHorarios').textContent = data.totalHorariosAsignados;
        })
        .catch(err => console.error('Error cargando dashboard:', err));
}

// --- CURSOS ---
function cargarCursos() {
    fetch('api/cursos')
        .then(res => res.json())
        .then(data => {
            const tbody = document.querySelector('#tablaCursos tbody');
            tbody.innerHTML = '';
            data.forEach(c => {
                tbody.innerHTML += `<tr>
                    <td>${c.grado}</td>
                    <td>${c.nombreCurso}</td>
                    <td>${c.jornada}</td>
                    <td>${c.numEstudiantes}</td>
                    <td class="acciones-fila"><button class="boton boton-peligro" onclick="eliminarCurso(${c.idCurso})">Eliminar</button></td>
                </tr>`;
            });
        })
        .catch(err => console.error('Error cargando cursos:', err));
}

// --- DOCENTES ---
function cargarDocentes() {
    fetch('api/docentes')
        .then(res => res.json())
        .then(data => {
            const tbody = document.querySelector('#tablaDocentes tbody');
            tbody.innerHTML = '';
            data.forEach(d => {
                const excedido = d.cargaActual > d.horasMaximasSemanales;
                tbody.innerHTML += `<tr>
                    <td>${d.nombre} ${d.apellido}</td>
                    <td>${d.disponibilidad ?? '-'}</td>
                    <td>${d.horasMaximasSemanales}</td>
                    <td style="color: ${excedido ? 'var(--lapiz-rojo)' : 'inherit'}; font-weight: ${excedido ? '700' : 'inherit'}">${d.cargaActual} hrs</td>
                    <td class="acciones-fila"><button class="boton boton-peligro" onclick="eliminarDocente(${d.idDocente})">Eliminar</button></td>
                </tr>`;
            });
        })
        .catch(err => console.error('Error cargando docentes:', err));
}

// --- ASIGNATURAS ---
function cargarAsignaturas() {
    fetch('api/asignaturas')
        .then(res => res.json())
        .then(data => {
            const tbody = document.querySelector('#tablaAsignaturas tbody');
            tbody.innerHTML = '';
            data.forEach(a => {
                tbody.innerHTML += `<tr>
                    <td>${a.nombre}</td>
                    <td>${a.intensidadHoraria} hrs</td>
                    <td class="acciones-fila"><button class="boton boton-peligro" onclick="eliminarAsignatura(${a.idAsignatura})">Eliminar</button></td>
                </tr>`;
            });
        })
        .catch(err => console.error('Error cargando asignaturas:', err));
}

// --- HORARIOS ---
function cargarHorarios() {
    fetch('api/horarios')
        .then(res => res.json())
        .then(data => {
            const tbody = document.querySelector('#tablaHorarios tbody');
            tbody.innerHTML = '';
            data.forEach(h => {
                tbody.innerHTML += `<tr>
                    <td>${h.dia}</td>
                    <td>${h.horaInicio} - ${h.horaFin}</td>
                    <td>${h.nombreCurso}</td>
                    <td>${h.nombreAsignatura}</td>
                    <td>${h.nombreDocente}</td>
                    <td class="acciones-fila"><button class="boton boton-peligro" onclick="eliminarHorario(${h.idHorario})">Eliminar</button></td>
                </tr>`;
            });
        })
        .catch(err => console.error('Error cargando horarios:', err));
}

// --- Crear curso ---
document.getElementById('formCurso').addEventListener('submit', function(e) {
    e.preventDefault();

    const selectGrado = document.getElementById('cGrado');
    const numeroGrado = selectGrado.value;
    const nombreGrado = selectGrado.options[selectGrado.selectedIndex].text; // toma el texto visible, ej: "4°"
    const seccion = document.getElementById('cSeccion').value;

    const datos = {
        grado: nombreGrado,
        nombreCurso: numeroGrado + "-" + seccion,
        jornada: document.getElementById('cJornada').value,
        numEstudiantes: parseInt(document.getElementById('cNumEstudiantes').value)
    };

    fetch('api/cursos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(datos)
    })
    .then(res => res.json())
    .then(() => {
        document.getElementById('formCurso').reset();
        cargarCursos();
    })
    .catch(err => alert('Error al crear curso: ' + err));
});
// --- Eliminar curso ---
function eliminarCurso(id) {
    if (!confirm('¿Seguro que quieres eliminar este curso?')) return;

    fetch('api/cursos?id=' + id, { method: 'DELETE' })
        .then(res => res.json())
        .then(() => cargarCursos())
        .catch(err => alert('Error al eliminar: ' + err));
}

// --- Crear docente ---
document.getElementById('formDocente').addEventListener('submit', function(e) {
    e.preventDefault();

    const datos = {
        nombre: document.getElementById('dNombre').value,
        apellido: document.getElementById('dApellido').value,
        disponibilidad: document.getElementById('dDisponibilidad').value,
        horasMaximasSemanales: parseInt(document.getElementById('dHorasMax').value)
    };

    fetch('api/docentes', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(datos)
    })
    .then(res => res.json())
    .then(() => {
        document.getElementById('formDocente').reset();
        cargarDocentes();
    })
    .catch(err => alert('Error al crear docente: ' + err));
});

// --- Eliminar docente ---
function eliminarDocente(id) {
    if (!confirm('¿Seguro que quieres eliminar este docente?')) return;

    fetch('api/docentes?id=' + id, { method: 'DELETE' })
        .then(res => res.json())
        .then(() => cargarDocentes())
        .catch(err => alert('Error al eliminar: ' + err));
}

// --- Crear asignatura ---
document.getElementById('formAsignatura').addEventListener('submit', function(e) {
    e.preventDefault();

    const datos = {
        nombre: document.getElementById('aNombre').value,
        intensidadHoraria: parseInt(document.getElementById('aIntensidad').value)
    };

    fetch('api/asignaturas', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(datos)
    })
    .then(res => res.json())
    .then(() => {
        document.getElementById('formAsignatura').reset();
        cargarAsignaturas();
    })
    .catch(err => alert('Error al crear asignatura: ' + err));
});

// --- Eliminar asignatura ---
function eliminarAsignatura(id) {
    if (!confirm('¿Seguro que quieres eliminar esta asignatura?')) return;

    fetch('api/asignaturas?id=' + id, { method: 'DELETE' })
        .then(res => res.json())
        .then(() => cargarAsignaturas())
        .catch(err => alert('Error al eliminar: ' + err));
}

// Llena los <select> del formulario de horarios con datos reales
function cargarSelectsHorario() {
    fetch('api/cursos')
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById('hIdCurso');
            select.innerHTML = data.map(c => `<option value="${c.idCurso}">${c.nombreCurso}</option>`).join('');
        });

    fetch('api/asignaturas')
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById('hIdAsignatura');
            select.innerHTML = data.map(a => `<option value="${a.idAsignatura}">${a.nombre}</option>`).join('');
        });

    fetch('api/docentes')
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById('hIdDocente');
            select.innerHTML = data.map(d => `<option value="${d.idDocente}">${d.nombre} ${d.apellido}</option>`).join('');
        });
}

// --- Crear horario (aquí es donde se ve la validación de conflictos) ---
document.getElementById('formHorarioReal').addEventListener('submit', function(e) {
    e.preventDefault();

    const datos = {
        idCurso: parseInt(document.getElementById('hIdCurso').value),
        idAsignatura: parseInt(document.getElementById('hIdAsignatura').value),
        idDocente: parseInt(document.getElementById('hIdDocente').value),
        dia: document.getElementById('hDia').value,
        horaInicio: document.getElementById('hHoraInicio').value,
        horaFin: document.getElementById('hHoraFin').value
    };

    const mensajeEl = document.getElementById('mensajeHorario');

    fetch('api/horarios', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(datos)
    })
    .then(res => res.json().then(body => ({ status: res.status, body })))
    .then(({ status, body }) => {
        if (status === 201) {
            mensajeEl.className = 'mensaje exito';
            mensajeEl.textContent = body.mensaje;
            document.getElementById('formHorarioReal').reset();
            cargarHorarios();
            cargarDocentes(); // para refrescar la carga actual del docente
        } else {
            mensajeEl.className = 'mensaje error';
            mensajeEl.textContent = body.error; // aquí sale el mensaje de conflicto o exceso de horas
        }
    })
    .catch(err => {
        mensajeEl.className = 'mensaje error';
        mensajeEl.textContent = 'Error inesperado: ' + err;
    });
});

// --- Eliminar horario ---
function eliminarHorario(id) {
    if (!confirm('¿Seguro que quieres eliminar este horario?')) return;

    fetch('api/horarios?id=' + id, { method: 'DELETE' })
        .then(res => res.json())
        .then(() => {
            cargarHorarios();
            cargarDocentes();
        })
        .catch(err => alert('Error al eliminar: ' + err));
}

// Cargar el dashboard automáticamente al abrir la página (es la pestaña inicial)
document.addEventListener('DOMContentLoaded', cargarDashboard);