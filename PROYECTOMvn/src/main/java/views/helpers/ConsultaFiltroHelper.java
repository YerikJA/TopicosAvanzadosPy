package views.helpers;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import model.Alumno;

/**
 * Servicio auxiliar para la lÃ³gica de bÃºsqueda, filtrado, normalizaciÃ³n de
 * texto y ordenaciÃ³n de alumnos en el panel de Consulta.
 */
public class ConsultaFiltroHelper
{

    public static final String TODAS_CARRERAS = "Todas las carreras";
    public static final String TODAS_MATERIAS = "Todas las materias";

    /**
     * Filtra y ordena la lista de alumnos segÃºn los criterios de la vista.
     */
    public static List<Alumno> filtrar(
            List<Alumno> alumnos,
            String textoBusqueda,
            String carreraSeleccionada,
            String materiaSeleccionada,
            double minPromedio,
            double maxPromedio,
            int criterioOrden,
            Map<Alumno, List<String>> materiasDe)
    {
        if (alumnos == null)
        {
            return new ArrayList<>();
        }

        String buscado = normalizar(textoBusqueda);
        String[] palabras = buscado.isEmpty() ? new String[0] : buscado.split(" ");

        List<Alumno> resultado = new ArrayList<>();
        for (Alumno a : alumnos)
        {
            if (cumple(a, palabras, carreraSeleccionada, materiaSeleccionada, minPromedio, maxPromedio, materiasDe))
            {
                resultado.add(a);
            }
        }

        resultado.sort(obtenerComparador(criterioOrden));
        return resultado;
    }

    /**
     * Verifica si un alumno cumple con los filtros activos.
     */
    public static boolean cumple(
            Alumno a,
            String[] palabras,
            String carrera,
            String materia,
            double min,
            double max,
            Map<Alumno, List<String>> materiasDe)
    {
        if (a == null)
        {
            return false;
        }

        if (palabras != null && palabras.length > 0)
        {
            String pajar = normalizar(texto(a.getNombre()) + " " + texto(a.getNo_control()) + " " + texto(a.getCorreo()));
            for (String p : palabras)
            {
                if (!pajar.contains(p))
                {
                    return false;
                }
            }
        }

        if (carrera != null && !TODAS_CARRERAS.equals(carrera) && !carrera.isEmpty() && !carrera.equals(texto(a.getCarerra())))
        {
            return false;
        }

        if (materia != null && !TODAS_MATERIAS.equals(materia) && !materia.isEmpty())
        {
            List<String> ms = materiasDe != null ? materiasDe.get(a) : null;
            if (ms == null || !ms.contains(materia))
            {
                return false;
            }
        }

        double prom = promedioDe(a);
        return !(prom < min || prom > max);
    }

    /**
     * Retorna el comparador adecuado segÃºn el Ã­ndice del ComboBox de orden.
     */
    public static Comparator<Alumno> obtenerComparador(int indiceCombo)
    {
        switch (indiceCombo)
        {
            case 1: // Nombre (Z-A)
                return (x, y) -> normalizar(texto(y.getNombre())).compareTo(normalizar(texto(x.getNombre())));
            case 2: // Promedio (mayor a menor)
                return (x, y) -> Double.compare(promedioDe(y), promedioDe(x));
            case 3: // Promedio (menor a mayor)
                return (x, y) -> Double.compare(promedioDe(x), promedioDe(y));
            case 4: // No. de control
                return (x, y) -> texto(x.getNo_control()).compareTo(texto(y.getNo_control()));
            case 0: // Nombre (A-Z)
            default:
                return (x, y) -> normalizar(texto(x.getNombre())).compareTo(normalizar(texto(y.getNombre())));
        }
    }

    public static String texto(Object o)
    {
        return o == null ? "" : o.toString();
    }

    public static String normalizar(String s)
    {
        if (s == null)
        {
            return "";
        }
        String n = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return n.toLowerCase().trim().replaceAll("\\s+", " ");
    }

    public static double promedioDe(Alumno a)
    {
        if (a == null)
        {
            return 0;
        }
        String crudo = String.valueOf(a.getPromedio()).trim();
        try
        {
            return Double.parseDouble(crudo.replace(',', '.'));
        } catch (NumberFormatException e)
        {
            return 0;
        }
    }

    public static String formatoPromedio(double d)
    {
        return (d == Math.floor(d)) ? String.valueOf((long) d) : String.format("%.1f", d);
    }

    public static String iniciales(String nombre)
    {
        if (nombre == null)
        {
            return "";
        }
        String[] partes = nombre.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < partes.length && sb.length() < 2; i++)
        {
            if (!partes[i].isEmpty())
            {
                sb.append(Character.toUpperCase(partes[i].charAt(0)));
            }
        }
        return sb.toString();
    }
}
