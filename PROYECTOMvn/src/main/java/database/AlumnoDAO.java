package database;

import exception.*;
import java.sql.*;
import java.util.*;
import model.*;
import database.Conexion;

/**
 *
 * @author yerik
 */
public class AlumnoDAO
{

    public List<AlumnoResumen> consultarPorCarrera(int idCarrea) throws AppException
    {
        List<AlumnoResumen> lista = new ArrayList<>();

        String sql = "Consulta";

        try (Connection con = Conexion.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setInt(1, idCarrea);
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    lista.add(new AlumnoResumen(rs.getInt("NO_CONTROL"),
                            rs.getString("NOMBRE"),
                            rs.getString("CORREO"),
                            rs.getString("CARRERA")));
                }

            }

        } catch (SQLException e)
        {
            throw new AppException(MensajesError.traducir(e), e);
        }
        return lista;
    }

    public List<ItemCatalogo> consultarMaterias(int noControl) throws AppException
    {
        List<ItemCatalogo> materias = new ArrayList<>();

        String sql = "pendiente";

        try (Connection con = Conexion.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql))
        {
            // Se asigna el valor del parámetro "?" en el SQL
            // Es setString porque NO_CONTROL es de tipo texto, y va en la posición 1 (el primer ?)
            ps.setInt(1, noControl);

            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    materias.add(new ItemCatalogo(rs.getInt("ID_MATERIA"),
                            rs.getString("NOMBRE")));
                }
            }
        } catch (SQLException e)
        {
            throw new AppException(MensajesError.traducir(e), e);
        }
        return materias;
    }

}
