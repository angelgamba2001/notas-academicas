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
import java.io.BufferedReader;
import java.sql.SQLException;

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
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    PrintWriter out = response.getWriter();

    try {
        // 1. Leer todo el cuerpo de la petición (el JSON que manda el frontend)
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String linea;
        while ((linea = reader.readLine()) != null) {
            sb.append(linea);
        }

        // 2. Convertir ese JSON en un objeto Horario
        Horario nuevoHorario = gson.fromJson(sb.toString(), Horario.class);

        // 3. Intentar insertarlo (aquí es donde se valida conflicto y horas máximas)
        horarioDAO.insertar(nuevoHorario);

        // 4. Si todo salió bien, responder con éxito
        response.setStatus(HttpServletResponse.SC_CREATED); // código 201: creado
        out.print(gson.toJson(new MensajeRespuesta("Horario creado correctamente.")));

    } catch (SQLException e) {
        // Este es el caso de conflicto o exceso de horas (las excepciones que lanzamos nosotros mismos)
        response.setStatus(HttpServletResponse.SC_CONFLICT); // código 409: conflicto
        out.print(gson.toJson(new ErrorRespuesta(e.getMessage())));

    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        out.print(gson.toJson(new ErrorRespuesta("Error inesperado: " + e.getMessage())));

    } finally {
        out.close();
    }
}

// Clase auxiliar para dar forma a mensajes de éxito
private static class MensajeRespuesta {
    String mensaje;
    MensajeRespuesta(String mensaje) { this.mensaje = mensaje; }
}

    private static class ErrorRespuesta {
        String error;
        ErrorRespuesta(String error) { this.error = error; }
    }
}

