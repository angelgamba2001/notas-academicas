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

@WebServlet("/api/docentes")
public class DocenteServlet extends HttpServlet {

    private final DocenteDAO docenteDAO = new DocenteDAO();
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
                out.print(gson.toJson(docentes));
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