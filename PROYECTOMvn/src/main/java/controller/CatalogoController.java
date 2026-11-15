/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import database.CatalogoDAO;
import exception.*;
import java.util.ArrayList;
import java.util.*;
import model.ItemCatalogo;

/**
 *
 * @author Luigi
 */
public class CatalogoController {
    
    private CatalogoDAO catalogoDAO= new CatalogoDAO();

    public List<String> obtenerCarreras() throws AppException
    {
        List<String> carreras = new ArrayList<>();
        carreras.add("Seleccone una carrera");
        carreras.addAll(catalogoDAO.obtenerCarreras());
        return carreras;
    }

    public List<ItemCatalogo> obtenerMaterias() throws AppException
    {
        return catalogoDAO.obtenerMaterias();
    }

    
}
