package validaciones;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class ValidadorAlumno
{//Cuando se importe es para decir donde vive, como es static no necesito instanciar

    private static final Pattern NOMBRE
            = Pattern.compile("^\\p{L}+(\\s\\p{L}+)*$");

    private static final Pattern CORREO
            = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private static final Pattern NUMERICOS
            = Pattern.compile("^\\d+$");

    public static boolean esVacio(String texto)
    {
        return texto == null || texto.trim().isEmpty();
    }

    public static boolean validarNumericos(String numericos)
    {
        return numericos != null && NUMERICOS.matcher(numericos.trim()).matches();

    }

    public static boolean nombreValido(String nombre)
    {
        return nombre != null && NOMBRE.matcher(nombre.trim()).matches();
    }

    public static boolean correoValido(String correo)
    {
        return correo != null && CORREO.matcher(correo.trim()).matches();
    }

    //si el dato introducido es valido, 
    //si no es valido manda la excepcion avisando que el formato en el que entro el dato no es valido
    public static Double promedioValido(String promedio)
    {
        if (!esVacio(promedio))
        {
            try
            {
                double dpromedio = Double.parseDouble(promedio);
                //return true;

                if (dpromedio >= 0 && dpromedio <= 100)
                {
                    return dpromedio; //Sin problemas

                }
                return null; //debe estar dentro del rango

            } catch (NumberFormatException e)
            {
                return null; //no es numerico
            }
        }

        return null; //esta vacio
    }

    public static boolean seleccionCb(int ind)
    {
        return ind <= 0;
    }

    public static boolean materiasVacias(List<?> materias)
    {
        return materias == null || materias.isEmpty();
    }

    //Este revisa que el usuario haya seleccionado una fecha
    public static boolean fechaVacia(Date fechaNacimiento)
    {
        return fechaNacimiento == null;
    }

    //Este revisa que la fecha introducida no sea mayor al dia de hoy
    public static boolean fechaFutura(Date fechaNacimiento)
    {
        if (fechaNacimiento != null)
        {
            return fechaNacimiento.after(new Date());
        }
        return false;
    }

    public static boolean fechaPasada(Date fechaNacimiento)
    {
        if (fechaNacimiento != null)
        {
            Date limite = Date.from(LocalDate.of(1950, 1, 1)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
            return fechaNacimiento.before(limite);
        }
        return false;
    }

}
