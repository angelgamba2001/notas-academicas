package com.horarios.model;

public class Asignatura {
    private int idAsignatura;
    private String nombre;
    private int intensidadHoraria;

    public int getIdAsignatura() { return idAsignatura; }
    public void setIdAsignatura(int idAsignatura) { this.idAsignatura = idAsignatura; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getIntensidadHoraria() { return intensidadHoraria; }
    public void setIntensidadHoraria(int intensidadHoraria) { this.intensidadHoraria = intensidadHoraria; }
}