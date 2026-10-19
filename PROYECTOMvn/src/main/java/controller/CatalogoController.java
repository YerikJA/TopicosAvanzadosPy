/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Luigi
 */
public class CatalogoController {
    
    //cualquier ventana q use el combo
    public List<String> obtenerCarreras(){
        return Arrays.asList("Seleccione una carrera", "Sistemas", "Logistica", "Electronica");
    }
    
    public List<String> obtenerMaterias(){
        return Arrays.asList("Ingles", "Fisica", "Simulacion", "Telecom");
    }
    
    public List<String> obtenerEstatus(){
        return Arrays.asList("Estudiante", "Profesor");
    }
}
