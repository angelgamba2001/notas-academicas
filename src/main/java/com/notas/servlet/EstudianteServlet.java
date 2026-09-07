package com.notas.servlet;

import com.notas.dao.EstudianteDAO;
import com.notas.model.Estudiante;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

/**
 * API REST para el CRUD de estudiantes.
 * GET    /api/estudiantes           -> lista todos los estudiantes
 * GET    /api/estudiantes?id=1      -> obtiene un estudiante
 * POST   /api/estudiantes           -> crea un estudiante (cuerpo JSON)
 * PUT    /api/estudiantes?id=1      -> actualiza un estudiante (cuerpo JSON)
 * DELETE /api/estudiantes?id=1      -> elimina un estudiante
 */
@WebServlet("/api/estudiantes")
public class EstudianteServlet extends HttpServlet {

    private final EstudianteDAO dao = new EstudianteDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String idParam = req.getParameter("id");

        try {
            if (idParam == null) {
                List<Estudiante> lista = dao.listar();
                JSONArray arreglo = new JSONArray();
                for (Estudiante e : lista) {
                    arreglo.put(aJson(e));
                }
                escribir(resp, arreglo.toString());
            } else {
                Estudiante e = dao.obtenerPorId(Integer.parseInt(idParam));
                if (e == null) {
                    responderError(resp, HttpServletResponse.SC_NOT_FOUND, "Estudiante no encontrado.");
                } else {
                    escribir(resp, aJson(e).toString());
                }
            }
        } catch (SQLException ex) {
            responderError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error de base de datos: " + ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        try {
            JSONObject cuerpo = leerCuerpo(req);
            Estudiante e = construirDesdeJson(cuerpo);

            String error = validar(e);
            if (error != null) {
                responderError(resp, HttpServletResponse.SC_BAD_REQUEST, error);
                return;
            }

            e.calcularPromedio();
            e.determinarRendimiento();

            int id = dao.insertar(e);
            e.setId(id);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            escribir(resp, aJson(e).toString());
        } catch (SQLException ex) {
            responderError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error de base de datos: " + ex.getMessage());
        } catch (NumberFormatException | org.json.JSONException ex) {
            responderError(resp, HttpServletResponse.SC_BAD_REQUEST, "Datos invalidos en la solicitud.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String idParam = req.getParameter("id");

        if (idParam == null) {
            responderError(resp, HttpServletResponse.SC_BAD_REQUEST, "Falta el parametro id.");
            return;
        }

        try {
            JSONObject cuerpo = leerCuerpo(req);
            Estudiante e = construirDesdeJson(cuerpo);
            e.setId(Integer.parseInt(idParam));

            String error = validar(e);
            if (error != null) {
                responderError(resp, HttpServletResponse.SC_BAD_REQUEST, error);
                return;
            }

            e.calcularPromedio();
            e.determinarRendimiento();

            boolean actualizado = dao.actualizar(e);
            if (!actualizado) {
                responderError(resp, HttpServletResponse.SC_NOT_FOUND, "Estudiante no encontrado.");
                return;
            }
            escribir(resp, aJson(e).toString());
        } catch (SQLException ex) {
            responderError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error de base de datos: " + ex.getMessage());
        } catch (NumberFormatException | org.json.JSONException ex) {
            responderError(resp, HttpServletResponse.SC_BAD_REQUEST, "Datos invalidos en la solicitud.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String idParam = req.getParameter("id");

        if (idParam == null) {
            responderError(resp, HttpServletResponse.SC_BAD_REQUEST, "Falta el parametro id.");
            return;
        }

        try {
            boolean eliminado = dao.eliminar(Integer.parseInt(idParam));
            if (!eliminado) {
                responderError(resp, HttpServletResponse.SC_NOT_FOUND, "Estudiante no encontrado.");
                return;
            }
            JSONObject ok = new JSONObject();
            ok.put("mensaje", "Estudiante eliminado correctamente.");
            escribir(resp, ok.toString());
        } catch (SQLException ex) {
            responderError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error de base de datos: " + ex.getMessage());
        }
    }

    // ---------- utilidades privadas ----------

    private String validar(Estudiante e) {
        if (e.getNombre() == null || e.getNombre().trim().isEmpty()) {
            return "El nombre del estudiante es obligatorio.";
        }
        String[] errores = {
                Estudiante.validarNota(e.getNota1(), "La nota 1"),
                Estudiante.validarNota(e.getNota2(), "La nota 2"),
                Estudiante.validarNota(e.getNota3(), "La nota 3"),
                Estudiante.validarNota(e.getNota4(), "La nota 4")
        };
        for (String err : errores) {
            if (err != null) {
                return err;
            }
        }
        return null;
    }

    private Estudiante construirDesdeJson(JSONObject json) {
        Estudiante e = new Estudiante();
        e.setNombre(json.optString("nombre", "").trim());
        e.setNota1(json.optDouble("nota1", Double.NaN));
        e.setNota2(json.optDouble("nota2", Double.NaN));
        e.setNota3(json.optDouble("nota3", Double.NaN));
        e.setNota4(json.optDouble("nota4", Double.NaN));
        return e;
    }

    private JSONObject aJson(Estudiante e) {
        JSONObject json = new JSONObject();
        json.put("id", e.getId());
        json.put("nombre", e.getNombre());
        json.put("nota1", e.getNota1());
        json.put("nota2", e.getNota2());
        json.put("nota3", e.getNota3());
        json.put("nota4", e.getNota4());
        json.put("promedio", e.getPromedio());
        json.put("resultado", e.getResultado());
        json.put("aprobado", e.determinarAprobacion());
        return json;
    }

    private JSONObject leerCuerpo(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                sb.append(linea);
            }
        }
        return new JSONObject(sb.toString());
    }

    private void escribir(HttpServletResponse resp, String json) throws IOException {
        try (PrintWriter out = resp.getWriter()) {
            out.print(json);
        }
    }

    private void responderError(HttpServletResponse resp, int status, String mensaje) throws IOException {
        resp.setStatus(status);
        JSONObject error = new JSONObject();
        error.put("error", mensaje);
        escribir(resp, error.toString());
    }
}
