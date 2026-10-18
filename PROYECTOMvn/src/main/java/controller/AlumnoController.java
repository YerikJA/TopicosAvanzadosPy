/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.util.ArrayList;
import java.util.List;
import model.Alumno;

/**
 *
 * @author Luigi
 */
public class AlumnoController
{

    private List<Alumno> alumnos = new ArrayList();

    public boolean agergarAlumno(Alumno alumno)
    {
        for (Alumno ob_alumno : alumnos)
        {   // de ese numero de lista checa el anterior si es q ya existe es no control
            if (ob_alumno.getNo_control().equals(alumno.getNo_control()))
            {
                return false;
            }
        }
        alumnos.add(alumno);
        return true;
    }

    public List<Alumno> consultarAlumno()
    {
        return alumnos;
    }

    public int obtenerPosicionAlumno(String no_control)
    {

        for (int i = 0; i < alumnos.size(); i++)
        {

            if (alumnos.get(i).getNo_control()
                    .equals(no_control))
            {

                return i;
            }
        }

        return -1;
    }

    public List<String> obtenerMaterias(int posicion)
    {

        return alumnos.get(posicion).getMaterias();
    }

    public Alumno buscarAlumnoPorNoControl(String no_control)
    {
        for (Alumno alumno : alumnos)
        {
            if (alumno.getNo_control().equals(no_control))
            {
                return alumno;
            }
        }

        return null;
    }

    public Alumno consultaPorNoControl(String noControl)
    {

        for (Alumno alumno : alumnos)
        {//Si eslo que estoy buscado que me mande el alumno que estoy buscado
            if (alumno.getNo_control().equals(noControl))
            {
                return alumno;
            }
        }
        return null;

    }

    public boolean darBajaAlumno(Alumno alumnoBaja)
    {
        for (int i = 0; i < alumnos.size(); i++)
        {
            if (alumnos.get(i).getNo_control().equals(alumnoBaja.getNo_control()))
            {
                alumnos.remove(i);
                return true;
            }
        }
        return false;

    }

    public boolean actualizarAlumno(Alumno alumnoActualizado)
    {

        for (int i = 0; i < alumnos.size(); i++)
        {
            if (alumnos.get(i).getNo_control().equals(alumnoActualizado.getNo_control()))
            {
                alumnos.set(i, alumnoActualizado);
                return true;
            }
        }

        return false;
    }

}
