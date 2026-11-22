package views.helpers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import model.Alumno;

/**
 * Renderizador de celda para la lista de alumnos en el panel de Consulta.
 * Dibuja un avatar circular con iniciales, nombre, carrera y promedio
 * formateado.
 */
public class AlumnoTarjetaRenderer extends JPanel implements ListCellRenderer<Alumno>
{

    private static final Color COLOR_BORDE = new Color(200, 215, 225);
    private static final Color COLOR_TARJETA = Color.WHITE;
    private static final Color COLOR_TEXTO_SUAVE = new Color(100, 110, 120);
    private static final Color COLOR_SELECCION = new Color(214, 234, 255);

    private static final Font FUENTE_NEGRITA = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FUENTE_ETIQUETA = new Font("Segoe UI", Font.PLAIN, 13);

    private final Circulo avatar = new Circulo(38);
    private final JLabel lblNombre = new JLabel();
    private final JLabel lblSub = new JLabel();
    private final JLabel lblProm = new JLabel();

    public AlumnoTarjetaRenderer()
    {
        setLayout(new BorderLayout(10, 0));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        lblNombre.setFont(FUENTE_NEGRITA);
        lblSub.setFont(FUENTE_ETIQUETA);
        lblSub.setForeground(COLOR_TEXTO_SUAVE);
        lblProm.setFont(FUENTE_NEGRITA);
        lblProm.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        textos.add(lblNombre);
        textos.add(lblSub);

        JPanel izquierda = new JPanel(new GridBagLayout());
        izquierda.setOpaque(false);
        izquierda.add(avatar);

        add(izquierda, BorderLayout.WEST);
        add(textos, BorderLayout.CENTER);
        add(lblProm, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Alumno> list, Alumno alumno, int index, boolean isSelected, boolean cellHasFocus)
    {
        if (alumno == null)
        {
            return this;
        }

        avatar.setTexto(ConsultaFiltroHelper.iniciales(ConsultaFiltroHelper.texto(alumno.getNombre())));
        lblNombre.setText(ConsultaFiltroHelper.texto(alumno.getNombre()));

        String carrera = ConsultaFiltroHelper.texto(alumno.getCarerra());
        String noCtrl = ConsultaFiltroHelper.texto(alumno.getNo_control());
        lblSub.setText(carrera.isEmpty() ? noCtrl : carrera + "  â€¢  " + noCtrl);

        double prom = ConsultaFiltroHelper.promedioDe(alumno);
        lblProm.setText(ConsultaFiltroHelper.formatoPromedio(prom));

        if (isSelected)
        {
            setBackground(COLOR_SELECCION);
        } else
        {
            setBackground(COLOR_TARJETA);
        }

        return this;
    }

    /**
     * Componente visual auxiliar: CÃ­rculo con las iniciales del alumno.
     */
    public static class Circulo extends JLabel
    {

        public Circulo(int tam)
        {
            setPreferredSize(new Dimension(tam, tam));
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, tam / 3));
            setForeground(new Color(60, 70, 80));
            setOpaque(false);
        }

        public void setTexto(String t)
        {
            setText(t);
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(225, 232, 240));
            g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
