package validaciones;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class ValidadorAlumno
{//Cuando se importe es para decir donde vive, como es static no necesito instanciar

    private static final Pattern NOMBRE
            = Pattern.compile("^\\p{L}+(\\s\\p{L}+)*$");

    private static final Pattern CORREO
            = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    public static boolean esVacio(String texto)
    {
        return texto == null || texto.trim().isEmpty();
    }

    public static boolean nombreValido(String nombre)
    {
        return nombre != null && NOMBRE.matcher(nombre.trim()).matches();
    }

    public static boolean correoValido(String correo)
    {
        return correo != null && CORREO.matcher(correo.trim()).matches();
    }

    //Este checa que la caja venga con datos
    public static boolean promedioVacio(String promedio)
    {

        return promedio == null || promedio.trim().isEmpty();

    }

    //Este primero ve que entre un dato, cuando entra realiza la conversion, 
    //si el dato introducido es valido, 
    //si no es valido manda la excepcion avisando que el formato en el que entro el dato no es valido
    public static boolean promedioValido(String promedio)
    {
        if (!promedioVacio(promedio))
        {
            try
            {
                double dpromedio = Double.parseDouble(promedio);
                return true;
            } catch (NumberFormatException e)
            {
                return false;
            }
        }
        return false;
    }

    //Este revisa que el dato que metio el usuario este en el rango valido de 0 a 100
    public static boolean promedioValid(double dpromedio)
    {
        return dpromedio >= 0 && dpromedio <= 100;
    }

    public static boolean seleccionCb(int ind)
    {
        return ind <= 0;
    }

    public static boolean materiasVacias(List<String> materias)
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

}
