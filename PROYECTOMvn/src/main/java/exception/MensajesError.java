/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exception;

import java.sql.*;

public class MensajesError
{

    public static String traducir(Exception e)
    {
        if (e instanceof SQLException)
        {
            SQLException sqlEx = (SQLException) e;

            switch (sqlEx.getErrorCode())
            {

                case 1017:  // ORA-01017
                    return "El usuario o la contraseña son incorrectos.";

                case 28000: // ORA-28000
                    return "La cuenta de usuario se encuentra bloqueada. "
                            + "Comuníquese con el administrador del sistema.";

                case 1:
                {   // ORA-00001
                    String detalle = sqlEx.getMessage();
                    if (detalle.contains("PK_ESTUDIANTES"))
                    {
                        return "El número de control ya se encuentra registrado.";
                    }
                    if (detalle.contains("UQ_ESTUDIANTES_CORREO"))
                    {
                        return "El correo electrónico ya se encuentra registrado.";
                    }
                    return "Ya existe un registro con los datos ingresados.";
                }

                case 2290:  // ORA-02290
                    return "Uno de los datos ingresados no cumple con los "
                            + "valores permitidos.";

                case 1400:  // ORA-01400
                    return "Falta un dato obligatorio.";
            }
        }
        return "Ocurrió un error inesperado. Intente de nuevo.";
    }
}
