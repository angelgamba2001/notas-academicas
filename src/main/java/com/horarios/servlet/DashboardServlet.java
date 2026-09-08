package com.horarios.servlet;

import com.google.gson.Gson;
import com.horarios.dao.AsignaturaDAO;
import com.horarios.dao.CursoDAO;
import com.horarios.dao.DocenteDAO;
import com.horarios.dao.HorarioDAO;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/dashboard")
public class DashboardServlet extends HttpServlet {

    private final CursoDAO cursoDAO = new CursoDAO();
    private final DocenteDAO docenteDAO = new DocenteDAO();
    private final AsignaturaDAO asignaturaDAO = new AsignaturaDAO();
    private final HorarioDAO horarioDAO = new HorarioDAO();
    private final Gson gson = GsonUtil.crearGson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            ResumenDashboard resumen = new ResumenDashboard();
            resumen.totalCursos = cursoDAO.listarTodos().size();
            resumen.totalDocentes = docenteDAO.listarTodos().size();
            resumen.totalAsignaturas = asignaturaDAO.listarTodos().size();
            resumen.totalHorariosAsignados = horarioDAO.listarTodos().size();

            out.print(gson.toJson(resumen));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ErrorRespuesta(e.getMessage())));
        } finally {
            out.close();
        }
    }

    private static class ResumenDashboard {
        int totalCursos;
        int totalDocentes;
        int totalAsignaturas;
        int totalHorariosAsignados;
    }

    private static class ErrorRespuesta {
        String error;
        ErrorRespuesta(String error) { this.error = error; }
    }
}