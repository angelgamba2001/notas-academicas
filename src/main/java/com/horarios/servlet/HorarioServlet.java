package com.horarios.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.horarios.dao.HorarioDAO;
import com.horarios.model.Horario;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/horarios")
public class HorarioServlet extends HttpServlet {

    private final HorarioDAO horarioDAO = new HorarioDAO();
    private final Gson gson = GsonUtil.crearGson();

    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    String idCursoParam = request.getParameter("idCurso");
    String idDocenteParam = request.getParameter("idDocente");
    String idAsignaturaParam = request.getParameter("idAsignatura");

    PrintWriter out = response.getWriter();

    try {
        List<Horario> resultado;

        if (idCursoParam != null) {
            resultado = horarioDAO.listarPorCurso(Integer.parseInt(idCursoParam));
        } else if (idDocenteParam != null) {
            resultado = horarioDAO.listarPorDocente(Integer.parseInt(idDocenteParam));
        } else if (idAsignaturaParam != null) {
            resultado = horarioDAO.listarPorAsignatura(Integer.parseInt(idAsignaturaParam));
        } else {
            resultado = horarioDAO.listarTodos();
        }

        out.print(gson.toJson(resultado));

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