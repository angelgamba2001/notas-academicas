package com.horarios.dao;

import com.horarios.model.Curso;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO {

    // Traer todos los cursos
    public List<Curso> listarTodos() throws SQLException {
        List<Curso> lista = new ArrayList<>();
        String sql = "SELECT * FROM curso";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCurso(rs));
            }
        }
        return lista;
    }

    // Traer un curso por su id
    public Curso buscarPorId(int idCurso) throws SQLException {
        String sql = "SELECT * FROM curso WHERE id_curso = ?";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCurso);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCurso(rs);
                }
            }
        }
        return null;
    }

    // Crear un curso nuevo
    public void insertar(Curso curso) throws SQLException {
        String sql = "INSERT INTO curso (grado, nombre_curso, jornada, num_estudiantes) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, curso.getGrado());
            ps.setString(2, curso.getNombreCurso());
            ps.setString(3, curso.getJornada());
            ps.setInt(4, curso.getNumEstudiantes());
            ps.executeUpdate();
        }
    }

    // Modificar un curso existente
    public void actualizar(Curso curso) throws SQLException {
        String sql = "UPDATE curso SET grado = ?, nombre_curso = ?, jornada = ?, num_estudiantes = ? WHERE id_curso = ?";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, curso.getGrado());
            ps.setString(2, curso.getNombreCurso());
            ps.setString(3, curso.getJornada());
            ps.setInt(4, curso.getNumEstudiantes());
            ps.setInt(5, curso.getIdCurso());
            ps.executeUpdate();
        }
    }

    // Eliminar un curso
    public void eliminar(int idCurso) throws SQLException {
        String sql = "DELETE FROM curso WHERE id_curso = ?";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCurso);
            ps.executeUpdate();
        }
    }

    // Método privado auxiliar: convierte una fila del ResultSet en un objeto Curso
    private Curso mapearCurso(ResultSet rs) throws SQLException {
        Curso c = new Curso();
        c.setIdCurso(rs.getInt("id_curso"));
        c.setGrado(rs.getString("grado"));
        c.setNombreCurso(rs.getString("nombre_curso"));
        c.setJornada(rs.getString("jornada"));
        c.setNumEstudiantes(rs.getInt("num_estudiantes"));
        return c;
    }
}