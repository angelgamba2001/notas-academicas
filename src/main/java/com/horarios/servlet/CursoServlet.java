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
