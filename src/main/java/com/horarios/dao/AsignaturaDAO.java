package com.horarios.dao;

import com.horarios.model.Asignatura;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsignaturaDAO {

    // Traer todas las asignaturas
    public List<Asignatura> listarTodos() throws SQLException {
        List<Asignatura> lista = new ArrayList<>();
        String sql = "SELECT * FROM asignatura";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearAsignatura(rs));
            }
        }
        return lista;
    }

    // Traer una asignatura por su id
    public Asignatura buscarPorId(int idAsignatura) throws SQLException {
        String sql = "SELECT * FROM asignatura WHERE id_asignatura = ?";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAsignatura);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearAsignatura(rs);
                }
            }
        }
        return null;
    }

    // Crear una asignatura nueva
    public void insertar(Asignatura asignatura) throws SQLException {
        String sql = "INSERT INTO asignatura (nombre, intensidad_horaria) VALUES (?, ?)";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, asignatura.getNombre());
            ps.setInt(2, asignatura.getIntensidadHoraria());
            ps.executeUpdate();
        }
    }

    // Modificar una asignatura existente
    public void actualizar(Asignatura asignatura) throws SQLException {
        String sql = "UPDATE asignatura SET nombre = ?, intensidad_horaria = ? WHERE id_asignatura = ?";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, asignatura.getNombre());
            ps.setInt(2, asignatura.getIntensidadHoraria());
            ps.setInt(3, asignatura.getIdAsignatura());
            ps.executeUpdate();
        }
    }

    // Eliminar una asignatura
    public void eliminar(int idAsignatura) throws SQLException {
        String sql = "DELETE FROM asignatura WHERE id_asignatura = ?";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAsignatura);
            ps.executeUpdate();
        }
    }

    private Asignatura mapearAsignatura(ResultSet rs) throws SQLException {
        Asignatura a = new Asignatura();
        a.setIdAsignatura(rs.getInt("id_asignatura"));
        a.setNombre(rs.getString("nombre"));
        a.setIntensidadHoraria(rs.getInt("intensidad_horaria"));
        return a;
    }
}