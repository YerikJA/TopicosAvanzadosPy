/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import exception.*;
/**
 *
 * @author PC
 */
public class CatalogoDAO
{
    private static final Logger log = LoggerFactory.getLogger(CatalogoDAO.class);

    public List<String> obtenerCarreras()throws AppException
    {
        List<String> carreras = new ArrayList<>();
        String sql = "SELECT NOMBRE_CARRERA FROM CAT_CARRERAS WHERE ESTATUS = 'S'";
        try (Connection con = Conexion.obetenerConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                carreras.add(rs.getString("NOMBRE_CARRERA"));
            }

        } catch (Exception e)
        {
            throw new AppException(MensajesError.traducir(e), e);            
        }

        return carreras;
    }

    public List<String> obtenerMaterias() throws AppException
    {
        List<String> materisa = new ArrayList<>();
        String sql = "SELECT NOMBRE FROM CAT_MATERIAS WHERE ESTATUS = 'S'";
        try (Connection con = Conexion.obetenerConexion(); 
                              PreparedStatement ps = con.prepareStatement(sql); 
                              ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                materisa.add(rs.getString("NOMBRE"));
            }

        } catch (Exception e)
        {
            throw new AppException(MensajesError.traducir(e), e);
        }

        return materisa;
    }
    
}
