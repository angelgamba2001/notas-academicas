package com.horarios.dao;

import com.horarios.dao.HoraExtraDAO;
import com.horarios.model.HoraExtra;
import java.time.LocalDate;

public class PruebaHoraExtraDAO {
    public static void main(String[] args) {
        HoraExtraDAO dao = new HoraExtraDAO();

        try {
            // Recuerda que ya insertamos manualmente 1.0 hora extra el 2026-09-03 (Jueves) para el docente 1

            // 1. Probar el límite SEMANAL: intentar meter otra hora extra en la MISMA semana
            System.out.println("=== Probando límite semanal (debería fallar) ===");
            HoraExtra intento1 = new HoraExtra();
            intento1.setIdDocente(1);
            intento1.setFecha(LocalDate.of(2026, 9, 4)); // Viernes, misma semana que el 3
            intento1.setHoras(0.5);

            try {
                dao.insertar(intento1);
                System.out.println("Se insertó sin problema (no debería llegar aquí).");
            } catch (Exception e) {
                System.out.println("Resultado esperado: " + e.getMessage());
            }

            // 2. Probar que SÍ funcione en una semana distinta
            System.out.println("\n=== Probando en una semana distinta (debería funcionar) ===");
            HoraExtra intento2 = new HoraExtra();
            intento2.setIdDocente(1);
            intento2.setFecha(LocalDate.of(2026, 9, 10)); // Jueves de la semana siguiente
            intento2.setHoras(1.0);

            dao.insertar(intento2);
            System.out.println("Se insertó correctamente.");

            // 3. Revisar el total acumulado en el mes
            System.out.println("\n=== Total de horas extra en septiembre 2026 ===");
            double totalMes = dao.sumarHorasMes(1, LocalDate.of(2026, 9, 15));
            System.out.println("Total del mes: " + totalMes + " horas");

            // 4. Probar el límite MENSUAL: forzar que ya casi esté en el tope y ver si bloquea
            System.out.println("\n=== Probando límite mensual (debería fallar si ya suma cerca de 4) ===");
            HoraExtra intento3 = new HoraExtra();
            intento3.setIdDocente(1);
            intento3.setFecha(LocalDate.of(2026, 9, 17)); // otra semana distinta
            intento3.setHoras(5.0); // un valor grande a propósito para forzar que pase el límite mensual

            try {
                dao.insertar(intento3);
                System.out.println("Se insertó sin problema (revisar si esto es correcto según tu total actual).");
            } catch (Exception e) {
                System.out.println("Resultado esperado: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }
}