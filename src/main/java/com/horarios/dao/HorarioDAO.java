package com.horarios.dao;

import com.horarios.model.Horario;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HorarioDAO {

    // Traer TODOS los horarios con nombres legibles (usando JOIN)
    public List<Horario> listarTodos() throws SQLException {
        String sql = "SELECT h.*, c.nombre_curso, a.nombre AS nombre_asignatura, "
                   + "CONCAT(d.nombre, ' ', d.apellido) AS nombre_docente "
                   + "FROM horario h "
                   + "JOIN curso c ON h.id_curso = c.id_curso "
                   + "JOIN asignatura a ON h.id_asignatura = a.id_asignatura "
                   + "JOIN docente d ON h.id_docente = d.id_docente";

        return ejecutarConsultaListado(sql, null);
    }

    // Consultar por curso
    public List<Horario> listarPorCurso(int idCurso) throws SQLException {
        String sql = "SELECT h.*, c.nombre_curso, a.nombre AS nombre_asignatura, "
                   + "CONCAT(d.nombre, ' ', d.apellido) AS nombre_docente "
                   + "FROM horario h "
                   + "JOIN curso c ON h.id_curso = c.id_curso "
                   + "JOIN asignatura a ON h.id_asignatura = a.id_asignatura "
                   + "JOIN docente d ON h.id_docente = d.id_docente "
                   + "WHERE h.id_curso = ?";

        return ejecutarConsultaListado(sql, idCurso);
    }

    // Consultar por docente (para ver su carga y estructura de horario)
    public List<Horario> listarPorDocente(int idDocente) throws SQLException {
        String sql = "SELECT h.*, c.nombre_curso, a.nombre AS nombre_asignatura, "
                   + "CONCAT(d.nombre, ' ', d.apellido) AS nombre_docente "
                   + "FROM horario h "
                   + "JOIN curso c ON h.id_curso = c.id_curso "
                   + "JOIN asignatura a ON h.id_asignatura = a.id_asignatura "
                   + "JOIN docente d ON h.id_docente = d.id_docente "
                   + "WHERE h.id_docente = ? "
                   + "ORDER BY FIELD(h.dia, 'Lunes','Martes','Miercoles','Jueves','Viernes'), h.hora_inicio";

        return ejecutarConsultaListado(sql, idDocente);
    }

    // Consultar por asignatura
    public List<Horario> listarPorAsignatura(int idAsignatura) throws SQLException {
        String sql = "SELECT h.*, c.nombre_curso, a.nombre AS nombre_asignatura, "
                   + "CONCAT(d.nombre, ' ', d.apellido) AS nombre_docente "
                   + "FROM horario h "
                   + "JOIN curso c ON h.id_curso = c.id_curso "
                   + "JOIN asignatura a ON h.id_asignatura = a.id_asignatura "
                   + "JOIN docente d ON h.id_docente = d.id_docente "
                   + "WHERE h.id_asignatura = ?";

        return ejecutarConsultaListado(sql, idAsignatura);
    }

    // Método auxiliar: ejecuta una consulta que trae una lista de horarios con un filtro opcional
    private List<Horario> ejecutarConsultaListado(String sql, Integer filtro) throws SQLException {
        List<Horario> lista = new ArrayList<>();

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (filtro != null) {
                ps.setInt(1, filtro);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearHorario(rs));
                }
            }
        }
        return lista;
    }

    // ¿Existe un conflicto de horario? (mismo docente O mismo curso, mismo día, y las horas se cruzan)
    // idHorarioExcluir se usa al editar (para no comparar el horario contra sí mismo). Pasa -1 si es uno nuevo.
    public boolean existeConflicto(int idDocente, int idCurso, String dia,
                                    LocalTime horaInicio, LocalTime horaFin, int idHorarioExcluir) throws SQLException {

        String sql = "SELECT COUNT(*) AS total FROM horario "
                   + "WHERE dia = ? "
                   + "AND (id_docente = ? OR id_curso = ?) "
                   + "AND hora_inicio < ? AND hora_fin > ? "
                   + "AND id_horario != ?";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dia);
            ps.setInt(2, idDocente);
            ps.setInt(3, idCurso);
            ps.setObject(4, horaFin);      // el horario existente empieza antes de que el nuevo termine
            ps.setObject(5, horaInicio);   // el horario existente termina después de que el nuevo empiece
            ps.setInt(6, idHorarioExcluir);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        }
        return false;
    }

    // Sumar el total de horas normales que ya tiene asignadas un docente en la semana
    public double calcularCargaSemanal(int idDocente) throws SQLException {
        String sql = "SELECT SUM(TIME_TO_SEC(TIMEDIFF(hora_fin, hora_inicio))) / 3600 AS total_horas "
                   + "FROM horario WHERE id_docente = ?";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idDocente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_horas");
                }
            }
        }
        return 0.0;
    }

    // Insertar un horario nuevo, VALIDANDO conflictos y límite de horas antes de guardar
    public void insertar(Horario h) throws SQLException {
        // 1. Verificar conflicto de cruce de horario
        if (existeConflicto(h.getIdDocente(), h.getIdCurso(), h.getDia(), h.getHoraInicio(), h.getHoraFin(), -1)) {
            throw new SQLException("Conflicto: el docente o el curso ya tienen una clase asignada en ese horario.");
        }

        // 2. Verificar que no se pase de las horas máximas semanales del docente
        DocenteDAO docenteDAO = new DocenteDAO();
        var docente = docenteDAO.buscarPorId(h.getIdDocente());
        double cargaActual = calcularCargaSemanal(h.getIdDocente());
        double horasNuevoBloque = java.time.Duration.between(h.getHoraInicio(), h.getHoraFin()).toMinutes() / 60.0;

        if (docente != null && (cargaActual + horasNuevoBloque) > docente.getHorasMaximasSemanales()) {
            throw new SQLException("El docente superaría sus horas máximas semanales permitidas (" 
                    + docente.getHorasMaximasSemanales() + " horas).");
        }

        // 3. Si todo está bien, insertar
        String sql = "INSERT INTO horario (id_curso, id_asignatura, id_docente, dia, hora_inicio, hora_fin) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, h.getIdCurso());
            ps.setInt(2, h.getIdAsignatura());
            ps.setInt(3, h.getIdDocente());
            ps.setString(4, h.getDia());
            ps.setObject(5, h.getHoraInicio());
            ps.setObject(6, h.getHoraFin());
            ps.executeUpdate();
        }
    }

    // Eliminar un horario
    public void eliminar(int idHorario) throws SQLException {
        String sql = "DELETE FROM horario WHERE id_horario = ?";

        try (Connection con = ConexionHorarios.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idHorario);
            ps.executeUpdate();
        }
    }

    private Horario mapearHorario(ResultSet rs) throws SQLException {
        Horario h = new Horario();
        h.setIdHorario(rs.getInt("id_horario"));
        h.setIdCurso(rs.getInt("id_curso"));
        h.setIdAsignatura(rs.getInt("id_asignatura"));
        h.setIdDocente(rs.getInt("id_docente"));
        h.setDia(rs.getString("dia"));
        h.setHoraInicio(rs.getObject("hora_inicio", LocalTime.class));
        h.setHoraFin(rs.getObject("hora_fin", LocalTime.class));
        h.setNombreCurso(rs.getString("nombre_curso"));
        h.setNombreAsignatura(rs.getString("nombre_asignatura"));
        h.setNombreDocente(rs.getString("nombre_docente"));
        return h;
    }
}