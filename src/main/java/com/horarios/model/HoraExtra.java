package com.horarios.model;

import java.time.LocalDate;

public class HoraExtra {
    private int idHoraExtra;
    private int idDocente;
    private LocalDate fecha;
    private double horas;

    public int getIdHoraExtra() { return idHoraExtra; }
    public void setIdHoraExtra(int idHoraExtra) { this.idHoraExtra = idHoraExtra; }

    public int getIdDocente() { return idDocente; }
    public void setIdDocente(int idDocente) { this.idDocente = idDocente; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public double getHoras() { return horas; }
    public void setHoras(double horas) { this.horas = horas; }
}