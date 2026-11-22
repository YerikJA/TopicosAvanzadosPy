package views.helpers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.Alumno;
import model.ItemCatalogo;

/**
 * Servicio auxiliar para la lÃ³gica de transformaciÃ³n y comparaciÃ³n de datos
 * en el panel de EdiciÃ³n de alumnos.
 */
public class EdicionHelper
{

    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy");

    public static List<String> nombres(List<ItemCatalogo> items)
    {
        List<String> resultado = new ArrayList<>();
        if (items != null)
        {
            for (ItemCatalogo item : items)
            {
                if (item != null)
                {
                    resultado.add(item.getNombre());
                }
            }
        }
        return resultado;
    }

    public static List<ItemCatalogo> aItems(List<String> nombres)
    {
        List<ItemCatalogo> resultado = new ArrayList<>();
        if (nombres != null)
        {
            for (String nombre : nombres)
            {
                resultado.add(new ItemCatalogo(0, nombre));
            }
        }
        return resultado;
    }

    /**
     * Descompone un nombre completo en [nombres, apellidoPaterno,
     * apellidoMaterno].
     */
    public static String[] descomponerNombre(String nombreCompleto)
    {
        if (nombreCompleto == null)
        {
            return new String[]
            {
                "", "", ""
            };
        }
        String[] partes = nombreCompleto.trim().split("\\s+");
        if (partes.length >= 3)
        {
            String apMat = partes[partes.length - 1];
            String apPat = partes[partes.length - 2];
            String nombre = String.join(" ", java.util.Arrays.copyOf(partes, partes.length - 2));
            return new String[]
            {
                nombre, apPat, apMat
            };
        } else if (partes.length == 2)
        {
            return new String[]
            {
                partes[0], partes[1], ""
            };
        } else
        {
            return new String[]
            {
                nombreCompleto.trim(), "", ""
            };
        }
    }

    public static String armarNombreCompleto(String nombre, String apPat, String apMat)
    {
        return (ConsultaFiltroHelper.texto(nombre).trim() + " "
                + ConsultaFiltroHelper.texto(apPat).trim() + " "
                + ConsultaFiltroHelper.texto(apMat).trim()).trim().replaceAll("\\s+", " ");
    }

    /**
     * Calcula la lista de cambios entre el alumno original y los valores nuevos
     * ingresados. Retorna una lista de arreglos {Campo, Antes, DespuÃ©s}.
     */
    public static List<Object[]> calcularCambios(
            Alumno alumnoOriginal,
            String nuevoNombre,
            String nuevoCorreo,
            String nuevaCarrera,
            String nuevoPromedioTexto,
            String nuevaFechaTexto,
            List<String> nuevasMaterias)
    {
        List<Object[]> cambios = new ArrayList<>();
        if (alumnoOriginal == null)
        {
            return cambios;
        }

        String antesNombre = alumnoOriginal.getNombre();
        if (!Objects.equals(antesNombre, nuevoNombre))
        {
            cambios.add(new Object[]
            {
                "Nombre", antesNombre, nuevoNombre
            });
        }

        String antesCorreo = alumnoOriginal.getCorreo();
        if (!Objects.equals(antesCorreo, nuevoCorreo))
        {
            cambios.add(new Object[]
            {
                "Correo", antesCorreo, nuevoCorreo
            });
        }

        String antesCarrera = ConsultaFiltroHelper.texto(alumnoOriginal.getCarerra());
        if (!Objects.equals(antesCarrera, nuevaCarrera))
        {
            cambios.add(new Object[]
            {
                "Carrera", antesCarrera, nuevaCarrera
            });
        }

        String antesPromedio = String.valueOf(alumnoOriginal.getPromedio());
        if (!Objects.equals(antesPromedio, nuevoPromedioTexto))
        {
            cambios.add(new Object[]
            {
                "Promedio", antesPromedio, nuevoPromedioTexto
            });
        }

        String antesFecha = alumnoOriginal.getFechaNaciomiento() != null
                ? FORMATO_FECHA.format(alumnoOriginal.getFechaNaciomiento()) : "";
        if (!Objects.equals(antesFecha, nuevaFechaTexto))
        {
            cambios.add(new Object[]
            {
                "Fecha nac.", antesFecha, nuevaFechaTexto
            });
        }

        String antesMaterias = String.join(", ", nombres(alumnoOriginal.getMaterias()));
        String despuesMaterias = nuevasMaterias != null ? String.join(", ", nuevasMaterias) : "";
        if (!Objects.equals(antesMaterias, despuesMaterias))
        {
            cambios.add(new Object[]
            {
                "Materias", antesMaterias, despuesMaterias
            });
        }

        return cambios;
    }
}
