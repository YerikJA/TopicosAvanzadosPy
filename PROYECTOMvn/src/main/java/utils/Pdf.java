/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.util.*;
import javax.swing.JOptionPane;
import model.Alumno;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import java.io.*;

/**
 *
 * @author yerik
 */
public class Pdf
{

    Document documento = new Document();

    public static void generarPdf(List<Alumno> lista)
    {
        // 1. Instanciar el documento de OpenPDF
        Document documento = new Document();

        try
        {
            // 2. Crear el "tubo" (FileOutputStream) para guardar el archivo en la PC
            PdfWriter.getInstance(documento, new FileOutputStream("ReporteAlumnos.pdf"));
            documento.open();
            Paragraph titulo = new Paragraph("Reporte General de Alumnos");
            titulo.setAlignment(Paragraph.ALIGN_CENTER);
            documento.add(titulo);

            documento.add(new Paragraph(" "));

            // 5. Crear una Tabla de 4 columnas (Ajusta los nombres según los datos que quieras mostrar)
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);

            // Agregar los títulos de las columnas de la tabla
            tabla.addCell("No. Control");
            tabla.addCell("Nombre");
            tabla.addCell("Carrera");
            tabla.addCell("Promedio");

            // 6. Recorrer tu lista de alumnos con un ciclo for-each y rellenar las celdas
            for (Alumno a : lista)
            {
                // Nota: Asegúrate de usar los métodos get reales de tu clase Alumno (ej. getNoControl, getNombre, etc.)
                tabla.addCell(String.valueOf(a.getNo_control()));
                tabla.addCell(String.valueOf(a.getNombre()));
                tabla.addCell(String.valueOf(a.getCarerra()));
                tabla.addCell(String.valueOf(a.getPromedio()));
            }

            // Agregar la tabla completa al documento PDF
            documento.add(tabla);

            // 7. Cerrar el documento (Paso obligatorio para que se guarde en disco)
            documento.close();

            // Un aviso visual de éxito por si lo quieres mostrar en pantalla
            JOptionPane.showMessageDialog(null, "¡PDF generado con éxito en la raíz del proyecto!", "Reporte Creado", JOptionPane.INFORMATION_MESSAGE);

        } catch (DocumentException | IOException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar el PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
