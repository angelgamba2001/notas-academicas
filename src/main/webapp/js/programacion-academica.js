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

// Decide qué datos cargar según la pestaña que se abrió
function cargarDatosDeTab(tab) {
    if (tab === 'dashboard') cargarDashboard();
    if (tab === 'cursos') cargarCursos();
    if (tab === 'docentes') cargarDocentes();
    if (tab === 'asignaturas') cargarAsignaturas();
    if (tab === 'horarios') cargarHorarios();
}

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
                    <td style="color: ${excedido ? 'red' : 'inherit'}">${d.cargaActual} hrs</td>
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
                </tr>`;
            });
        })
        .catch(err => console.error('Error cargando horarios:', err));
}

// Cargar el dashboard automáticamente al abrir la página (es la pestaña inicial)
document.addEventListener('DOMContentLoaded', cargarDashboard);