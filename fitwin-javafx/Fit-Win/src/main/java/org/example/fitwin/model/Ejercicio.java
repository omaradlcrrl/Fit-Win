package org.example.fitwin.model;

public class Ejercicio {
    private String nombre;
    private int series;
    private int repeticiones;

    public Ejercicio(String nombre, int series, int repeticiones) {
        this.nombre = nombre;
        this.series = series;
        this.repeticiones = repeticiones;
    }


    public String getNombre() { return nombre; }
    public int getSeries() { return series; }
    public int getRepeticiones() { return repeticiones; }
}
