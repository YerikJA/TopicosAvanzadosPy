package database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Conexion
{

    private static final Properties CONFIG = new Properties();

    static
    {
        try (java.io.InputStream in = Conexion.class.getClassLoader()
                .getResourceAsStream("config.properties"))
        {
            if (in == null)
            {
                throw new RuntimeException("No se encontró config.properties en src/main/resources");
            }
            CONFIG.load(in);
        } catch (IOException e)
        {
            throw new RuntimeException("Error al leer config.properties", e);
        }
    }

    public static Connection obtenerConexion() throws SQLException
    {
        return DriverManager.getConnection(
                CONFIG.getProperty("db.url"),
                CONFIG.getProperty("db.usuario"),
                CONFIG.getProperty("db.clave")
        );
    }
}
