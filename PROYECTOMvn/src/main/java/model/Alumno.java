/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Luigi
 */
public class Alumno {
    
    private String no_control;
    private String nombre;
    private String correo;
    private String carerra;
    private double promedio;
    private Date fechaNaciomiento;
    private List<String> materias;
    
    public Alumno(String no_control, String nombre, String correo, String carerra, double promedio, Date fechaNacimiento ,List<String> materias)
    {
        this.no_control = no_control;
        this.nombre = nombre;
        this.correo = correo;
        this.carerra = carerra;
        this.promedio = promedio;
        this.fechaNaciomiento= fechaNacimiento;
        this.materias = materias;
    }
    
    public String getNo_control()
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

    public String getCarerra()
    {
        return carerra;
    }

    public double getPromedio()
    {
        return promedio;
    }

    public Date getFechaNaciomiento()
    {
        return fechaNaciomiento;
    }

    
    public List<String> getMaterias()
    {
        return materias;
    }
}
