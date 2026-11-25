/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author yerik
 */
public class AlumnoResumen
{

    private final int no_control;
    private final String nombre;
    private final String correo;
    private final String carrera;

    public AlumnoResumen(int no_control, String nombre, String correo, String carrera)
    {
        this.no_control = no_control;
        this.nombre = nombre;
        this.correo = correo;
        this.carrera = carrera;
    }

    public int getNo_control()
    {
        return no_control;
    }

    public String getNombre()
    {
        return nombre;
    }

    public String getCorreo()
    {
        return correo;
    }

    public String getCarrera()
    {
        return carrera;
    }
}
