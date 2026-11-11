/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion
{

    private static final String URL = "aun no";
    private static final String USUARIO = "aun no";
    private static final String CLAVE = "aun no";

    public static Connection obetenerConexion() throws SQLException
    {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }

}
