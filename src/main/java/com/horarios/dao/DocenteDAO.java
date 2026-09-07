package com.horarios.dao;

import com.horarios.model.Docente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocenteDAO {

    // Traer todos los docentes
    public List<Docente> listarTodos() throws SQLException {
        List<Docente> lista = new ArrayList<>();
        String sql = "SELECT * FROM docente";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDocente(rs));
            }
        }
        return lista;
    }

    // Traer un docente por su id
    public Docente buscarPorId(int idDocente) throws SQLException {
        String sql = "SELECT * FROM docente WHERE id_docente = ?";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idDocente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearDocente(rs);
                }
            }
        }
        return null;
    }

    // Crear un docente nuevo
    public void insertar(Docente docente) throws SQLException {
        String sql = "INSERT INTO docente (nombre, apellido, disponibilidad, horas_maximas_semanales) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, docente.getNombre());
            ps.setString(2, docente.getApellido());
            ps.setString(3, docente.getDisponibilidad());
            ps.setInt(4, docente.getHorasMaximasSemanales());
            ps.executeUpdate();
        }
    }

    // Modificar un docente existente
    public void actualizar(Docente docente) throws SQLException {
        String sql = "UPDATE docente SET nombre = ?, apellido = ?, disponibilidad = ?, horas_maximas_semanales = ? WHERE id_docente = ?";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, docente.getNombre());
            ps.setString(2, docente.getApellido());
            ps.setString(3, docente.getDisponibilidad());
            ps.setInt(4, docente.getHorasMaximasSemanales());
            ps.setInt(5, docente.getIdDocente());
            ps.executeUpdate();
        }
    }

    // Eliminar un docente
    public void eliminar(int idDocente) throws SQLException {
        String sql = "DELETE FROM docente WHERE id_docente = ?";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idDocente);
            ps.executeUpdate();
        }
    }

    private Docente mapearDocente(ResultSet rs) throws SQLException {
        Docente d = new Docente();
        d.setIdDocente(rs.getInt("id_docente"));
        d.setNombre(rs.getString("nombre"));
        d.setApellido(rs.getString("apellido"));
        d.setDisponibilidad(rs.getString("disponibilidad"));
        d.setHorasMaximasSemanales(rs.getInt("horas_maximas_semanales"));
        return d;
    }
}