package com.horarios.model;

public class Curso {
    private int idCurso;
    private String grado;
    private String nombreCurso;
    private String jornada;
    private int numEstudiantes;

    public int getIdCurso() { return idCurso; }
    public void setIdCurso(int idCurso) { this.idCurso = idCurso; }

    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }

    public String getNombreCurso() { return nombreCurso; }
    public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }

    public String getJornada() { return jornada; }
    public void setJornada(String jornada) { this.jornada = jornada; }

    public int getNumEstudiantes() { return numEstudiantes; }
    public void setNumEstudiantes(int numEstudiantes) { this.numEstudiantes = numEstudiantes; }
}