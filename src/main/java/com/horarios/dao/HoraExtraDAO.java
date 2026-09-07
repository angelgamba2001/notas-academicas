package com.horarios.dao;

import com.horarios.model.HoraExtra;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoraExtraDAO {

    private static final double LIMITE_SEMANAL = 1.0;   // 1 hora extra por semana
    private static final double LIMITE_MENSUAL = 4.0;   // 4 horas extra por mes

    // Traer todas las horas extra de un docente
    public List<HoraExtra> listarPorDocente(int idDocente) throws SQLException {
        List<HoraExtra> lista = new ArrayList<>();
        String sql = "SELECT * FROM hora_extra WHERE id_docente = ? ORDER BY fecha DESC";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idDocente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearHoraExtra(rs));
                }
            }
        }
        return lista;
    }

    // Sumar las horas extra de un docente EN LA MISMA SEMANA que una fecha dada
    // YEARWEEK() agrupa por año+semana, así no se mezclan semanas de años distintos
    public double sumarHorasSemana(int idDocente, LocalDate fecha) throws SQLException {
        String sql = "SELECT COALESCE(SUM(horas), 0) AS total FROM hora_extra "
                   + "WHERE id_docente = ? AND YEARWEEK(fecha, 1) = YEARWEEK(?, 1)";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idDocente);
            ps.setObject(2, fecha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0.0;
    }

    // Sumar las horas extra de un docente EN EL MISMO MES que una fecha dada
    public double sumarHorasMes(int idDocente, LocalDate fecha) throws SQLException {
        String sql = "SELECT COALESCE(SUM(horas), 0) AS total FROM hora_extra "
                   + "WHERE id_docente = ? AND YEAR(fecha) = YEAR(?) AND MONTH(fecha) = MONTH(?)";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idDocente);
            ps.setObject(2, fecha);
            ps.setObject(3, fecha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0.0;
    }

    // Registrar una hora extra nueva, VALIDANDO los límites semanal y mensual antes de guardar
    public void insertar(HoraExtra he) throws SQLException {

        double totalSemanaActual = sumarHorasSemana(he.getIdDocente(), he.getFecha());
        if ((totalSemanaActual + he.getHoras()) > LIMITE_SEMANAL) {
            throw new SQLException("El docente superaría el límite de " + LIMITE_SEMANAL
                    + " hora(s) extra permitida(s) por semana.");
        }

        double totalMesActual = sumarHorasMes(he.getIdDocente(), he.getFecha());
        if ((totalMesActual + he.getHoras()) > LIMITE_MENSUAL) {
            throw new SQLException("El docente superaría el límite de " + LIMITE_MENSUAL
                    + " horas extra permitidas por mes.");
        }

        String sql = "INSERT INTO hora_extra (id_docente, fecha, horas) VALUES (?, ?, ?)";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, he.getIdDocente());
            ps.setObject(2, he.getFecha());
            ps.setDouble(3, he.getHoras());
            ps.executeUpdate();
        }
    }

    // Eliminar un registro de hora extra
    public void eliminar(int idHoraExtra) throws SQLException {
        String sql = "DELETE FROM hora_extra WHERE id_hora_extra = ?";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idHoraExtra);
            ps.executeUpdate();
        }
    }

    private HoraExtra mapearHoraExtra(ResultSet rs) throws SQLException {
        HoraExtra he = new HoraExtra();
        he.setIdHoraExtra(rs.getInt("id_hora_extra"));
        he.setIdDocente(rs.getInt("id_docente"));
        he.setFecha(rs.getObject("fecha", LocalDate.class));
        he.setHoras(rs.getDouble("horas"));
        return he;
    }
}
