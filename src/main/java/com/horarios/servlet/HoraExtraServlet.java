package com.horarios.servlet;

import com.google.gson.Gson;
import com.horarios.dao.HoraExtraDAO;
import com.horarios.model.HoraExtra;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/horas-extra")
public class HoraExtraServlet extends HttpServlet {

    private final HoraExtraDAO horaExtraDAO = new HoraExtraDAO();
    private final Gson gson = GsonUtil.crearGson();

    // Consultar las horas extra de un docente
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            String idDocenteParam = request.getParameter("idDocente");

            if (idDocenteParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ErrorRespuesta("Debes indicar el parámetro 'idDocente'.")));
                return;
            }

            int idDocente = Integer.parseInt(idDocenteParam);
            List<HoraExtra> lista = horaExtraDAO.listarPorDocente(idDocente);

            // Además de la lista, mandamos los totales acumulados (útil para mostrar en pantalla)
            double totalSemana = horaExtraDAO.sumarHorasSemana(idDocente, LocalDate.now());
            double totalMes = horaExtraDAO.sumarHorasMes(idDocente, LocalDate.now());

            RespuestaHorasExtra respuesta = new RespuestaHorasExtra(lista, totalSemana, totalMes);
            out.print(gson.toJson(respuesta));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ErrorRespuesta(e.getMessage())));
        } finally {
            out.close();
        }
    }

    // Registrar una hora extra nueva
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
            while ((linea = reader.readLine()) != null) {
                sb.append(linea);
            }

            HoraExtra nueva = gson.fromJson(sb.toString(), HoraExtra.class);
            horaExtraDAO.insertar(nueva);

            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.toJson(new MensajeRespuesta("Hora extra registrada correctamente.")));

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            out.print(gson.toJson(new ErrorRespuesta(e.getMessage())));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ErrorRespuesta("Error inesperado: " + e.getMessage())));

        } finally {
            out.close();
        }
    }

    // Clase auxiliar para devolver la lista JUNTO con los totales acumulados
    private static class RespuestaHorasExtra {
        List<HoraExtra> registros;
        double totalSemana;
        double totalMes;

        RespuestaHorasExtra(List<HoraExtra> registros, double totalSemana, double totalMes) {
            this.registros = registros;
            this.totalSemana = totalSemana;
            this.totalMes = totalMes;
        }
    }

    private static class ErrorRespuesta {
        String error;
        ErrorRespuesta(String error) { this.error = error; }
    }

    private static class MensajeRespuesta {
        String mensaje;
        MensajeRespuesta(String mensaje) { this.mensaje = mensaje; }
    }
}