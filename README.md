# Gestión de Notas Académicas

Aplicación web (HTML + CSS + JavaScript + POO + Servlets/JDBC + MySQL) para
registrar estudiantes, calcular su promedio y clasificar su rendimiento
académico, con CRUD completo contra una base de datos MySQL.

## 1. Crear la base de datos

1. Abre MySQL Workbench (o la consola `mysql`).
2. Ejecuta el script [database/schema.sql](database/schema.sql) completo.
   Esto crea la base `notas_academicas`, la tabla `estudiante` y 4 registros
   de ejemplo (uno por cada categoría de rendimiento).

## 2. Configurar la conexión

Edita [`src/main/java/com/notas/dao/ConexionBD.java`](src/main/java/com/notas/dao/ConexionBD.java)
y ajusta `USUARIO` y `CLAVE` con las credenciales de tu MySQL local (por
defecto están en `root` / `root`).

## 3. Abrir el proyecto en NetBeans 31

1. `File > Open Project` y selecciona la carpeta `notas-academicas`
   (NetBeans la reconoce automáticamente como proyecto Maven por el `pom.xml`).
2. En `Services > Servers`, agrega tu Apache Tomcat 9.0.110 si aún no está
   configurado.
3. Click derecho sobre el proyecto → `Properties > Run` → selecciona Tomcat 9
   como servidor.
4. Click derecho sobre el proyecto → `Clean and Build` (descarga las
   dependencias de Maven: driver de MySQL, Servlet API, org.json).
5. Click derecho → `Run` (o `Deploy`). NetBeans desplegará el WAR en Tomcat
   y abrirá `http://localhost:8080/notas-academicas/` en el navegador.

## 4. Estructura del proyecto

```
notas-academicas/
├── pom.xml
├── database/schema.sql                        → script de base de datos
├── src/main/java/com/notas/
│   ├── model/Estudiante.java                   → POO en Java (cálculo/validación en servidor)
│   ├── dao/ConexionBD.java                     → conexión JDBC a MySQL
│   ├── dao/EstudianteDAO.java                  → operaciones CRUD (SQL)
│   └── servlet/EstudianteServlet.java          → API REST (/api/estudiantes)
└── src/main/webapp/
    ├── index.html                              → formulario, resultados y tabla
    ├── css/style.css                           → diseño visual y responsive
    └── js/
        ├── estudiante.js                       → clase Estudiante (POO en JavaScript)
        └── app.js                              → validaciones, fetch al API, render de tabla
```

## 5. Reglas de negocio implementadas

- Cada nota debe estar entre **0.0 y 5.0**; no se aceptan negativos ni
  valores por encima de 5.0 (bloqueado en el teclado, validado en
  JavaScript y validado de nuevo en el Servlet/Java como segunda barrera).
- El promedio se redondea a un decimal y se limita (`clamp`) al rango
  0.0–5.0 tanto en el cliente como en el servidor, como respaldo ante
  cualquier caso límite de redondeo.
- Clasificación cualitativa:

  | Promedio | Resultado |
  | --- | --- |
  | 0.0 – 2.9 | Rendimiento insuficiente (R.I) |
  | 3.0 – 3.9 | Aprobado (A) |
  | 4.0 – 4.5 | Aprobado con sobresaliente (A.S) |
  | 4.6 – 5.0 | Aprobado con excelente (A.E) |

## 6. API REST (`/api/estudiantes`)

| Método | Endpoint | Acción |
| --- | --- | --- |
| GET | `/api/estudiantes` | Lista todos los estudiantes |
| GET | `/api/estudiantes?id=1` | Obtiene un estudiante |
| POST | `/api/estudiantes` | Crea un estudiante (cuerpo JSON) |
| PUT | `/api/estudiantes?id=1` | Actualiza un estudiante (cuerpo JSON) |
| DELETE | `/api/estudiantes?id=1` | Elimina un estudiante |

## 7. Casos de prueba sugeridos (sección 12 de la guía)

1. Notas con promedio < 3.0 → "Rendimiento insuficiente (R.I)".
2. Notas con promedio entre 3.0 y 3.9 → "Aprobado (A)".
3. Notas con promedio entre 4.0 y 4.5 → "Aprobado con sobresaliente (A.S)".
4. Notas con promedio entre 4.6 y 5.0 → "Aprobado con excelente (A.E)".
5. Ingresar una nota negativa o mayor a 5.0 → el formulario debe bloquear
   el guardado y mostrar el mensaje de error bajo el campo.
6. Registrar un estudiante y luego pulsar "Actualizar lista" → debe
   aparecer en la tabla; editarlo y eliminarlo para comprobar el CRUD
   completo.

Toma capturas de pantalla de cada prueba para las evidencias que pide la
guía (sección 14).
