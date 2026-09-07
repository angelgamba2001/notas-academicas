package com.notas.dao;

import com.notas.model.Estudiante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Operaciones CRUD */
public class EstudianteDAO {

    public int insertar(Estudiante e) throws SQLException {
        String sql = "INSERT INTO estudiante (nombre, nota1, nota2, nota3, nota4, promedio, resultado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            llenarParametros(ps, e);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public List<Estudiante> listar() throws SQLException {
        String sql = "SELECT * FROM estudiante ORDER BY id DESC";
        List<Estudiante> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearFila(rs));
            }
        }
        return lista;
    }

    public Estudiante obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM estudiante WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearFila(rs);
                }
            }
        }
        return null;
    }

    public boolean actualizar(Estudiante e) throws SQLException {
        String sql = "UPDATE estudiante SET nombre = ?, nota1 = ?, nota2 = ?, nota3 = ?, "
                + "nota4 = ?, promedio = ?, resultado = ? WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            llenarParametros(ps, e);
            ps.setInt(8, e.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM estudiante WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private void llenarParametros(PreparedStatement ps, Estudiante e) throws SQLException {
        ps.setString(1, e.getNombre());
        ps.setDouble(2, e.getNota1());
        ps.setDouble(3, e.getNota2());
        ps.setDouble(4, e.getNota3());
        ps.setDouble(5, e.getNota4());
        ps.setDouble(6, e.getPromedio());
        ps.setString(7, e.getResultado());
    }

    private Estudiante mapearFila(ResultSet rs) throws SQLException {
        Estudiante e = new Estudiante();
        e.setId(rs.getInt("id"));
        e.setNombre(rs.getString("nombre"));
        e.setNota1(rs.getDouble("nota1"));
        e.setNota2(rs.getDouble("nota2"));
        e.setNota3(rs.getDouble("nota3"));
        e.setNota4(rs.getDouble("nota4"));
        e.calcularPromedio();
        e.determinarRendimiento();
        return e;
    }
}
