package com.horarios.dao;

import com.horarios.dao.HorarioDAO;
import com.horarios.model.Horario;
import java.time.LocalTime;
import java.util.List;

public class PruebaHorarioDAO {
    public static void main(String[] args) {
        HorarioDAO dao = new HorarioDAO();

        try {
            // 1. Probar listar el horario del docente Carlos (id 1)
            System.out.println("=== Horario del docente 1 (Carlos) ===");
            List<Horario> horarios = dao.listarPorDocente(1);
            for (Horario h : horarios) {
                System.out.println(h.getDia() + " " + h.getHoraInicio() + "-" + h.getHoraFin()
                        + " | " + h.getNombreAsignatura() + " | " + h.getNombreCurso());
            }

            // 2. Probar la carga semanal de ese docente
            System.out.println("\n=== Carga semanal del docente 1 ===");
            double carga = dao.calcularCargaSemanal(1);
            System.out.println("Horas actuales: " + carga);

            // 3. Probar intentar insertar un horario QUE SÍ debería generar conflicto
            // (mismo docente, mismo día, misma hora que ya existe en tus datos de prueba)
            System.out.println("\n=== Probando insertar un horario en conflicto ===");
            Horario nuevo = new Horario();
            nuevo.setIdCurso(2);
            nuevo.setIdAsignatura(1);
            nuevo.setIdDocente(1);
            nuevo.setDia("Lunes");
            nuevo.setHoraInicio(LocalTime.of(7, 30));
            nuevo.setHoraFin(LocalTime.of(8, 30));

            dao.insertar(nuevo);
            System.out.println("Se insertó sin problema (no debería llegar aquí si hay conflicto).");

        } catch (Exception e) {
            System.out.println("Resultado esperado si hay conflicto: " + e.getMessage());
        }
    }
}