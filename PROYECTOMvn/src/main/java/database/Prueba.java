
package database;

import java.sql.Connection;


public class Prueba
{

    private static final java.util.logging.Logger logger
            = java.util.logging.Logger.getLogger(Prueba.class.getName());

    public static void main(String[] args)
    {
        try (Connection con = Conexion.obtenerConexion())
        {
            System.out.println("Respuesta de BD: " + con.isValid(2));

        } catch (Exception e)
        {
            System.out.println("Error: " + e.getMessage());
            logger.warning("Error: " + e.getMessage());
        }
    }
}
