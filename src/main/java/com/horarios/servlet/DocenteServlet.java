package com.horarios.servlet;

import com.google.gson.Gson;
import com.horarios.dao.DocenteDAO;
import com.horarios.model.Docente;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.horarios.dao.HorarioDAO; 
import java.io.BufferedReader;
import java.sql.SQLException;

@WebServlet("/api/docentes")
public class DocenteServlet extends HttpServlet {

    private final DocenteDAO docenteDAO = new DocenteDAO();
    private final HorarioDAO horarioDAO = new HorarioDAO(); // agrega esta línea junto a docenteDAO
    private final Gson gson = GsonUtil.crearGson();

    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    PrintWriter out = response.getWriter();

    try {
        String idParam = request.getParameter("id");

        if (idParam != null) {
            Docente docente = docenteDAO.buscarPorId(Integer.parseInt(idParam));
            out.print(gson.toJson(docente));
        } else {
            List<Docente> docentes = docenteDAO.listarTodos();
            List<DocenteConCarga> resultado = new java.util.ArrayList<>();

            for (Docente d : docentes) {
                DocenteConCarga dc = new DocenteConCarga();
                dc.idDocente = d.getIdDocente();
                dc.nombre = d.getNombre();
                dc.apellido = d.getApellido();
                dc.disponibilidad = d.getDisponibilidad();
                dc.horasMaximasSemanales = d.getHorasMaximasSemanales();
                dc.cargaActual = horarioDAO.calcularCargaSemanal(d.getIdDocente());
                resultado.add(dc);
            }

            out.print(gson.toJson(resultado));
        }

    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        out.print(gson.toJson(new ErrorRespuesta(e.getMessage())));
    } finally {
        out.close();
    }
}

@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();

    try {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String linea;
        while ((linea = reader.readLine()) != null) sb.append(linea);

        Docente nuevo = gson.fromJson(sb.toString(), Docente.class);
        docenteDAO.insertar(nuevo);

        response.setStatus(HttpServletResponse.SC_CREATED);
        out.print(gson.toJson(new MensajeRespuesta("Docente creado correctamente.")));

    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        out.print(gson.toJson(new ErrorRespuesta(e.getMessage())));
    } finally {
        out.close();
    }
}

@Override
protected void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();

    try {
        int id = Integer.parseInt(request.getParameter("id"));
        docenteDAO.eliminar(id);
        out.print(gson.toJson(new MensajeRespuesta("Docente eliminado correctamente.")));

    } catch (SQLException e) {
        if (e.getErrorCode() == 1451) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            out.print(gson.toJson(new ErrorRespuesta(
                "No se puede eliminar este docente porque tiene horarios u horas extra asignadas. Elimina primero esos registros.")));
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ErrorRespuesta("Error al eliminar: " + e.getMessage())));
        }
    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        out.print(gson.toJson(new ErrorRespuesta(e.getMessage())));
    } finally {
        out.close();
    }
}

@Override
protected void doPut(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();

    try {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String linea;
        while ((linea = reader.readLine()) != null) sb.append(linea);

        Docente docente = gson.fromJson(sb.toString(), Docente.class);
        docenteDAO.actualizar(docente);

        out.print(gson.toJson(new MensajeRespuesta("Docente actualizado correctamente.")));

    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        out.print(gson.toJson(new ErrorRespuesta(e.getMessage())));
    } finally {
        out.close();
    }
}
private static class MensajeRespuesta {
    String mensaje;
    MensajeRespuesta(String mensaje) { this.mensaje = mensaje; }
}
// Clase auxiliar: un Docente pero con su carga actual incluida
private static class DocenteConCarga {
    int idDocente;
    String nombre;
    String apellido;
    String disponibilidad;
    int horasMaximasSemanales;
    double cargaActual;
}

    private static class ErrorRespuesta {
        String error;
        ErrorRespuesta(String error) { this.error = error; }
    }
}

 