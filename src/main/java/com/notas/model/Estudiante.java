package com.notas.model;

/**
 * Modelo que representa a un estudiante y sus calificaciones.
 * Contiene la logica de calculo (promedio, aprobacion y rendimiento
 * cualitativo) para que el Servlet y el DAO trabajen siempre con un
 * objeto ya validado y calculado, sin duplicar reglas de negocio.
 */
public class Estudiante {

    public static final double NOTA_MINIMA = 0.0;
    public static final double NOTA_MAXIMA = 5.0;

    private int id;
    private String nombre;
    private double nota1;
    private double nota2;
    private double nota3;
    private double nota4;
    private double promedio;
    private String resultado;

    public Estudiante() {
    }

    public Estudiante(String nombre, double nota1, double nota2, double nota3, double nota4) {
        this.nombre = nombre;
        this.nota1 = nota1;
        this.nota2 = nota2;
        this.nota3 = nota3;
        this.nota4 = nota4;
        calcularPromedio();
        determinarRendimiento();
    }

    /** Calcula el promedio de las cuatro notas y lo limita al rango 0.0 - 5.0. */
    public double calcularPromedio() {
        double suma = nota1 + nota2 + nota3 + nota4;
        double valor = suma / 4.0;
        valor = Math.round(valor * 10.0) / 10.0;
        this.promedio = Math.max(NOTA_MINIMA, Math.min(NOTA_MAXIMA, valor));
        return this.promedio;
    }

    /** Un estudiante aprueba cuando el promedio es igual o superior a 3.0. */
    public boolean determinarAprobacion() {
        return this.promedio >= 3.0;
    }

    /** Traduce el promedio cuantitativo a la valoracion cualitativa de la guia. */
    public String determinarRendimiento() {
        if (promedio < 3.0) {
            resultado = "Rendimiento insuficiente (R.I)";
        } else if (promedio < 4.0) {
            resultado = "Aprobado (A)";
        } else if (promedio <= 4.5) {
            resultado = "Aprobado con sobresaliente (A.S)";
        } else {
            resultado = "Aprobado con excelente (A.E)";
        }
        return resultado;
    }

    /**
     * Valida que las cuatro notas sean numeros dentro del rango permitido
     * (sin negativos y sin superar 5.0). Devuelve un mensaje de error o
     * null si todo es valido.
     */
    public static String validarNota(double nota, String etiqueta) {
        if (Double.isNaN(nota)) {
            return etiqueta + " debe ser un valor numerico.";
        }
        if (nota < NOTA_MINIMA) {
            return etiqueta + " no puede ser negativa.";
        }
        if (nota > NOTA_MAXIMA) {
            return etiqueta + " no puede ser mayor que 5.0.";
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getNota1() {
        return nota1;
    }

    public void setNota1(double nota1) {
        this.nota1 = nota1;
    }

    public double getNota2() {
        return nota2;
    }

    public void setNota2(double nota2) {
        this.nota2 = nota2;
    }

    public double getNota3() {
        return nota3;
    }

    public void setNota3(double nota3) {
        this.nota3 = nota3;
    }

    public double getNota4() {
        return nota4;
    }

    public void setNota4(double nota4) {
        this.nota4 = nota4;
    }

    public double getPromedio() {
        return promedio;
    }

    public String getResultado() {
        return resultado;
    }
}
