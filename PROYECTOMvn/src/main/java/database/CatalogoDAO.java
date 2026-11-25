package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import exception.*;
import model.ItemCatalogo;

/**
 *
 * @author PC
 */
public class CatalogoDAO
{

    private static final Logger log = LoggerFactory.getLogger(CatalogoDAO.class);

    public List<ItemCatalogo> obtenerCarreras() throws AppException
    {
        List<ItemCatalogo> carreras = new ArrayList<>();
        String sql = "SELECT ID_CARRERA, CARRERA FROM CAT_CARRERAS " // ---- Actualizar
                + "WHERE ID_ESTATUS = 'AC' ORDER BY CARRERA";
        try (Connection con = Conexion.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                carreras.add(new ItemCatalogo(rs.getInt("ID_CARRERA"),
                        rs.getString("CARRERA")));
            }

        } catch (Exception e)
        {
            throw new AppException(MensajesError.traducir(e), e);
        }

        return carreras;
    }

    public List<ItemCatalogo> obtenerMaterias() throws AppException
    {
        List<ItemCatalogo> materias = new ArrayList<>();
        String sql = "SELECT ID_MATERIA, MATERIA FROM CAT_MATERIAS " // ---- Actualizar
                + "WHERE ID_ESTATUS = 'AC' ORDER BY MATERIA";
        try (Connection con = Conexion.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                materias.add(new ItemCatalogo(rs.getInt("ID_MATERIA"),
                        rs.getString("MATERIA")));
            }

        } catch (Exception e)
        {
            throw new AppException(MensajesError.traducir(e), e);
        }

        return materias;
    }

}
