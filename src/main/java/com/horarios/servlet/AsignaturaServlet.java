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

    private static class ErrorRespuesta {
        String error;
        ErrorRespuesta(String error) { this.error = error; }
    }
}