
package model;

import java.util.Date;
import java.util.List;

public class Alumno {
    
    private String no_control;
    private String nombre;
    private String correo;
    private ItemCatalogo carerra;
    private double promedio;
    private Date fechaNaciomiento;
    private List<ItemCatalogo> materias;
    
    public Alumno(String no_control, String nombre, String correo, ItemCatalogo carerra, double promedio, Date fechaNacimiento ,List<ItemCatalogo> materias)
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

    public ItemCatalogo getCarerra()
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

    
    public List<ItemCatalogo> getMaterias()
    {
        return materias;
    }
}
