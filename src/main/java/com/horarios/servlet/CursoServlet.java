package com.horarios.servlet;

import com.google.gson.Gson;
import com.horarios.dao.CursoDAO;
import com.horarios.model.Curso;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.sql.SQLException;

@WebServlet("/api/cursos")
public class CursoServlet extends HttpServlet {

    private final CursoDAO cursoDAO = new CursoDAO();
    private final Gson gson = GsonUtil.crearGson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            List<Curso> cursos = cursoDAO.listarTodos();
            String json = gson.toJson(cursos);
            out.print(json);

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out_error(response, "Error al consultar cursos: " + e.getMessage());
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

        Curso nuevo = gson.fromJson(sb.toString(), Curso.class);
        cursoDAO.insertar(nuevo);

        response.setStatus(HttpServletResponse.SC_CREATED);
        out.print(gson.toJson(new MensajeRespuesta("Curso creado correctamente.")));

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
        cursoDAO.eliminar(id);
        out.print(gson.toJson(new MensajeRespuesta("Curso eliminado correctamente.")));

    } catch (SQLException e) {
        if (e.getErrorCode() == 1451) {
            response.setStatus(HttpServletResponse.SC_CONFLICT); // 409
            out.print(gson.toJson(new ErrorRespuesta(
                "No se puede eliminar este curso porque tiene horarios asignados. Elimina primero esos horarios.")));
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

        Curso curso = gson.fromJson(sb.toString(), Curso.class);
        cursoDAO.actualizar(curso);

        out.print(gson.toJson(new MensajeRespuesta("Curso actualizado correctamente.")));

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
    private void out_error(HttpServletResponse response, String mensaje) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(new ErrorRespuesta(mensaje)));
        }
    }

    // Clase simple solo para dar forma al mensaje de error en JSON
    private static class ErrorRespuesta {
        String error;
        ErrorRespuesta(String error) { this.error = error; }
    }
}
