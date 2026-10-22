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
        return Arrays.asList("Seleccione una carrera","Ing. Sistemas","Tic´s","Quimica","Mecatronica","Electromecanica","Logistica","Gestion Ambiental" );
    }
    
    public List<String> obtenerMaterias(){
        return Arrays.asList("Top. Avan. Prog.","Simulacion","Metodos Numericos","Prolog","Arq.Computadoras.");
    }

    
}
