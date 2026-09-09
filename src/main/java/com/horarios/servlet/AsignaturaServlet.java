package com.horarios.servlet;

import com.google.gson.Gson;
import com.horarios.dao.AsignaturaDAO;
import com.horarios.model.Asignatura;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.sql.SQLException;


@WebServlet("/api/asignaturas")
public class AsignaturaServlet extends HttpServlet {

    private final AsignaturaDAO asignaturaDAO = new AsignaturaDAO();
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
                Asignatura asignatura = asignaturaDAO.buscarPorId(Integer.parseInt(idParam));
                out.print(gson.toJson(asignatura));
            } else {
                List<Asignatura> asignaturas = asignaturaDAO.listarTodos();
                out.print(gson.toJson(asignaturas));
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

        Asignatura nueva = gson.fromJson(sb.toString(), Asignatura.class);
        asignaturaDAO.insertar(nueva);

        response.setStatus(HttpServletResponse.SC_CREATED);
        out.print(gson.toJson(new MensajeRespuesta("Asignatura creada correctamente.")));

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
        asignaturaDAO.eliminar(id);
        out.print(gson.toJson(new MensajeRespuesta("Asignatura eliminada correctamente.")));

    } catch (SQLException e) {
        if (e.getErrorCode() == 1451) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            out.print(gson.toJson(new ErrorRespuesta(
                "No se puede eliminar esta asignatura porque está asignada a uno o más docentes u horarios. Elimina primero esas relaciones.")));
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

        Asignatura asignatura = gson.fromJson(sb.toString(), Asignatura.class);
        asignaturaDAO.actualizar(asignatura);

        out.print(gson.toJson(new MensajeRespuesta("Asignatura actualizada correctamente.")));

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

    private static class ErrorRespuesta {
        String error;
        ErrorRespuesta(String error) { this.error = error; }
    }
}