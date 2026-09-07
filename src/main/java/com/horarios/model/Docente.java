package com.horarios.model;

public class Docente {
    private int idDocente;
    private String nombre;
    private String apellido;
    private String disponibilidad;
    private int horasMaximasSemanales;

    public int getIdDocente() { return idDocente; }
    public void setIdDocente(int idDocente) { this.idDocente = idDocente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(String disponibilidad) { this.disponibilidad = disponibilidad; }

    public int getHorasMaximasSemanales() { return horasMaximasSemanales; }
    public void setHorasMaximasSemanales(int horasMaximasSemanales) { this.horasMaximasSemanales = horasMaximasSemanales; }
}