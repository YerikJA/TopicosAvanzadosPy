package estilos;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;

/**
 * Estilos compartidos de todo el proyecto (basado en el diseño "ALUMNOS").
 * Llamar SIEMPRE despues de initComponents().
 *
 * RESUMEN Ventana ........ estilizarVentana Paneles ........ estilizarPanel,
 * estilizarPanelConSombra, estilizarFondo Textos ......... estilizarTitulo,
 * estilizarSubtitulo, estilizarEtiqueta, estilizarBarraSeccion,
 * estilizarEtiquetaOpcional Botones ........ estilizarBoton,
 * estilizarBotonPrincipal Menu lateral ... estilizarMenu, agregarSubtitulo,
 * seleccionar Pestañas ....... estilizarPestanas, seleccionar Campos .........
 * estilizarCampo, estilizarCombo, estilizarFecha, estilizarSpinner,
 * estilizarScroll, estilizarTabla Iconos ......... icono (escalado nitido)
 *
 * NOTA: Impact no tiene negrita real, por eso SIEMPRE se usa Font.PLAIN.
 */
public class Estilos
{

    // ---------- COLORES ----------
    public static final Color CREMA = new Color(0xF7F6F4);        // fondo general (blanco hueso)
    public static final Color CREMA_CLARO = new Color(0xFFFFFF);  // paneles y campos (blanco)
    public static final Color AZUL = new Color(0x1F7A86);         // titulos grandes, sombra (verde azulado medio)
    public static final Color AZUL_OSCURO = new Color(0x0B3036);  // etiquetas, pestañas (verde oscuro)
    public static final Color NARANJA = new Color(0x31ACBE);      // acento, boton principal (turquesa)
    public static final Color NEGRO = new Color(0x08292D);        // texto y bordes (casi negro verdoso)
    public static final Color GRIS_TEXTO = new Color(0x7D9999);   // placeholders (gris verdoso)
    public static final Color AZUL_SUAVE = new Color(0xCDE7EC);   // boton activo (tono tenue)
    public static final Color HOVER_SUAVE = new Color(0xE8F3F5);  // al pasar el mouse en el menu

    // ---------- FUENTES ----------
    private static final String FAMILIA = elegirFamilia(
            "Bebas Neue", "Barlow Condensed", "Impact", "Arial Narrow", "Arial");

    private static String elegirFamilia(String... candidatas)
    {
        List<String> instaladas = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getAvailableFontFamilyNames());
        for (String f : candidatas)
        {
            if (instaladas.contains(f))
            {
                return f;
            }
        }
        return Font.SANS_SERIF;
    }

    public static Font fuente(int estilo, int tam)
    {
        return new Font(FAMILIA, estilo, tam);
    }

    // ---------- BORDES ----------
    public static Border borde(int grosor)
    {
        return BorderFactory.createLineBorder(NEGRO, grosor);
    }

    private static Border bordeConPadding(Color color, int grosor, int vertical, int horizontal)
    {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, grosor),
                BorderFactory.createEmptyBorder(vertical, horizontal, vertical, horizontal));
    }

    /**
     * Borde negro con sombra azul desplazada abajo y a la derecha.
     */
    private static class BordeSombra implements Border
    {

        private final int grosor;
        private final int desfase;

        BordeSombra(int grosor, int desfase)
        {
            this.grosor = grosor;
            this.desfase = desfase;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
        {
            // 1) limpiar las franjas donde va la sombra
            g.setColor(CREMA);
            g.fillRect(x + w - desfase, y, desfase, h);
            g.fillRect(x, y + h - desfase, w, desfase);
            // 2) sombra azul
            g.setColor(AZUL);
            g.fillRect(x + w - desfase, y + desfase, desfase, h - desfase);
            g.fillRect(x + desfase, y + h - desfase, w - desfase, desfase);
            // 3) borde negro
            g.setColor(NEGRO);
            int cw = w - desfase;
            int ch = h - desfase;
            g.fillRect(x, y, cw, grosor);
            g.fillRect(x, y + ch - grosor, cw, grosor);
            g.fillRect(x, y, grosor, ch);
            g.fillRect(x + cw - grosor, y, grosor, ch);
        }

        @Override
        public Insets getBorderInsets(Component c)
        {
            return new Insets(grosor, grosor, grosor + desfase, grosor + desfase);
        }

        @Override
        public boolean isBorderOpaque()
        {
            return true;
        }
    }

    // ---------- VENTANA ----------
    /**
     * Fondo crema, centrada en pantalla.
     */
    public static void estilizarVentana(JFrame ventana)
    {
        ventana.getContentPane().setBackground(CREMA);
        ventana.setLocationRelativeTo(null);
    }

    // ---------- PANELES ----------
    /**
     * Panel de contenido: fondo blanco con borde negro.
     */
    public static void estilizarPanel(JPanel p)
    {
        p.setOpaque(true);
        p.setBackground(CREMA_CLARO);
        p.setBorder(borde(3));
    }

    /**
     * Panel de contenido con borde negro y sombra azul (como el diseño). Usar
     * SOLO en el panel de afuera; el de adentro va sin borde.
     */
    public static void estilizarPanelConSombra(JPanel p)
    {
        p.setOpaque(true);
        p.setBackground(CREMA_CLARO);
        p.setBorder(new BordeSombra(3, 10));
    }

    /**
     * Panel sin borde con el fondo crema general (contenedores).
     */
    public static void estilizarFondo(JPanel p)
    {
        p.setOpaque(true);
        p.setBackground(CREMA);
    }

    // ---------- TEXTOS ----------
    /**
     * Titulo grande azul (ej. "ALUMNOS", "BIENVENIDO").
     */
    public static void estilizarTitulo(JLabel l)
    {
        l.setFont(fuente(Font.PLAIN, 64));
        l.setForeground(AZUL);
    }

    /**
     * Subtitulo de pantalla en negro (ej. "CUENTA Y EXTRAS").
     */
    public static void estilizarSubtitulo(JLabel l)
    {
        l.setFont(fuente(Font.PLAIN, 36));
        l.setForeground(NEGRO);
    }

    /**
     * Etiqueta de campo en azul oscuro (ej. "TELEFONO", "ESTATUS").
     */
    public static void estilizarEtiqueta(JLabel l)
    {
        l.setFont(fuente(Font.PLAIN, 16));
        l.setForeground(AZUL_OSCURO);
        l.setText(l.getText().toUpperCase());
    }

    /**
     * Barra negra de seccion (ej. "CUENTA DE LA PLATAFORMA"). Se usa sobre un
     * JLabel.
     */
    public static void estilizarBarraSeccion(JLabel l)
    {
        l.setOpaque(true);
        l.setBackground(NEGRO);
        l.setForeground(Color.WHITE);
        l.setFont(fuente(Font.PLAIN, 20));
        l.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
    }

    /**
     * Etiqueta turquesa pequeña (ej. "OPCIONAL").
     */
    public static void estilizarEtiquetaOpcional(JLabel l)
    {
        l.setOpaque(true);
        l.setBackground(NARANJA);
        l.setForeground(NEGRO);
        l.setFont(fuente(Font.PLAIN, 12));
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        l.setText(l.getText().toUpperCase());
    }

    // ---------- BOTONES ----------
    /**
     * Boton normal: blanco, borde negro, turquesa al pasar el mouse.
     */
    public static void estilizarBoton(JButton b)
    {
        aplicarBoton(b, CREMA_CLARO, NEGRO);
    }

    /**
     * Boton principal (ej. "DAR DE ALTA", "GUARDAR Y CONTINUAR"): turquesa.
     */
    public static void estilizarBotonPrincipal(JButton b)
    {
        aplicarBoton(b, NARANJA, NEGRO);
    }

    private static void aplicarBoton(JButton b, Color fondo, Color texto)
    {
        baseBoton(b, 22);
        b.setBackground(fondo);
        b.setForeground(texto);
        b.setBorder(bordeConPadding(NEGRO, 3, 6, 16));
        b.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                b.setBackground(fondo == NARANJA ? NEGRO : NARANJA);
                b.setForeground(fondo == NARANJA ? Color.WHITE : NEGRO);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                b.setBackground(fondo);
                b.setForeground(texto);
            }
        });
    }

    private static void baseBoton(JButton b, int tam)
    {
        b.setFont(fuente(Font.PLAIN, tam));
        b.setFocusPainted(false);
        // Estas dos lineas quitan el degradado azul de Swing:
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setIconTextGap(10);
    }

    // ---------- GRUPOS DE BOTONES: MENU Y PESTAÑAS ----------
    /**
     * Menu lateral (ALTA, CONSULTA, EDICION, BAJA). Ninguno queda marcado al
     * inicio. Al hacer clic, el boton queda activo: fondo azul tenue, texto
     * azul oscuro y borde turquesa.
     */
    public static void estilizarMenu(JButton... botones)
    {
        estilizarGrupo(botones, 22,
                CREMA_CLARO, NEGRO, NEGRO, // normal: fondo, texto, borde
                HOVER_SUAVE, // hover
                AZUL_SUAVE, AZUL_OSCURO, NARANJA);  // activo: fondo, texto, borde
    }

    /**
     * Pestañas (1. DATOS PRINCIPALES, 2. MATERIAS Y PROMEDIO, 3. CUENTA Y
     * EXTRAS). Normal: azul oscuro con texto blanco. Activa: azul tenue con
     * texto azul oscuro.
     */
    public static void estilizarPestanas(JButton... botones)
    {
        estilizarGrupo(botones, 20,
                AZUL_OSCURO, Color.WHITE, NEGRO,
                AZUL,
                AZUL_SUAVE, AZUL_OSCURO, NARANJA);
    }

    /**
     * Subtitulo en cursiva que aparece debajo del texto SOLO cuando el boton
     * del menu esta activo (ej. "REGISTRAR ALUMNO NUEVO" bajo ALTA).
     */
    public static void agregarSubtitulo(JButton b, String subtitulo)
    {
        b.putClientProperty("subtitulo", subtitulo);
    }

    /**
     * Marca un boton de menu o pestaña como activo desde codigo (y desmarca los
     * demas de su grupo). Util al avanzar con "GUARDAR Y CONTINUAR" o "ATRAS".
     */
    public static void seleccionar(JButton b)
    {
        Object g = b.getClientProperty("grupo");
        if (g instanceof JButton[])
        {
            for (JButton otro : (JButton[]) g)
            {
                pintar(otro, otro == b);
            }
        }
    }

    private static void estilizarGrupo(JButton[] botones, int tam,
            Color bg, Color fg, Color borde, Color hover,
            Color bgAct, Color fgAct, Color bordeAct)
    {
        Color[] tema =
        {
            bg, fg, borde, bgAct, fgAct, bordeAct
        };
        for (JButton b : botones)
        {
            b.putClientProperty("grupo", botones);
            b.putClientProperty("tema", tema);
            b.putClientProperty("texto", b.getText());
            b.putClientProperty("activo", Boolean.FALSE);
            baseBoton(b, tam);
            pintar(b, false);
            b.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseEntered(MouseEvent e)
                {
                    if (!esActivo(b))
                    {
                        b.setBackground(hover);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e)
                {
                    if (!esActivo(b))
                    {
                        b.setBackground(bg);
                    }
                }
            });
            b.addActionListener(e -> seleccionar(b));
        }
    }

    private static boolean esActivo(JButton b)
    {
        return Boolean.TRUE.equals(b.getClientProperty("activo"));
    }

    private static void pintar(JButton b, boolean activo)
    {
        Color[] t = (Color[]) b.getClientProperty("tema");
        b.putClientProperty("activo", activo);
        b.setBackground(activo ? t[3] : t[0]);
        b.setForeground(activo ? t[4] : t[1]);
        b.setBorder(bordeConPadding(activo ? t[5] : t[2], 3, 3, 14));
        String base = (String) b.getClientProperty("texto");
        String sub = (String) b.getClientProperty("subtitulo");
        if (activo && sub != null)
        {
            b.setText("<html><center>" + base
                    + "<br><i><span style='font-size:9px'>" + sub
                    + "</span></i></center></html>");
        } else
        {
            b.setText(base);
        }
    }

    // ---------- CAMPOS ----------
    /**
     * Sirve para JTextField, JPasswordField, JTextArea, etc.
     */
    public static void estilizarCampo(JTextComponent c)
    {
        c.setFont(fuente(Font.PLAIN, 18));
        c.setBackground(CREMA_CLARO);
        c.setForeground(NEGRO);
        c.setCaretColor(NEGRO);
        c.setSelectionColor(NARANJA);
        c.setBorder(bordeConPadding(NEGRO, 2, 6, 8));
    }

    public static void estilizarCombo(JComboBox<?> c)
    {
        c.setFont(fuente(Font.PLAIN, 18));
        c.setBackground(CREMA_CLARO);
        c.setForeground(NEGRO);
        c.setBorder(borde(2));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void estilizarFecha(com.toedter.calendar.JDateChooser dateChooser)
    {
        // Fuente y colores generales del contenedor
        dateChooser.setFont(fuente(Font.PLAIN, 18));
        dateChooser.setBackground(CREMA_CLARO);
        dateChooser.setForeground(NEGRO);
        dateChooser.setBorder(borde(2));

        // Componente de texto interno y boton desplegable del calendario
        for (Component comp : dateChooser.getComponents())
        {
            if (comp instanceof javax.swing.JTextField)
            {
                javax.swing.JTextField txt = (javax.swing.JTextField) comp;
                txt.setFont(fuente(Font.PLAIN, 18));
                txt.setBackground(CREMA_CLARO);
                txt.setForeground(NEGRO);
                txt.setCaretColor(NEGRO);
                txt.setSelectionColor(NARANJA);
                txt.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            } else if (comp instanceof JButton)
            {
                JButton btn = (JButton) comp;
                btn.setBackground(CREMA_CLARO);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createLineBorder(NEGRO, 1));
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        }
    }

    public static void estilizarSpinner(JSpinner s)
    {
        s.setFont(fuente(Font.PLAIN, 18));
        s.setBackground(CREMA_CLARO);
        s.setBorder(borde(2));
        if (s.getEditor() instanceof JSpinner.DefaultEditor)
        {
            javax.swing.JTextField tf = ((JSpinner.DefaultEditor) s.getEditor()).getTextField();
            tf.setFont(fuente(Font.PLAIN, 18));
            tf.setBackground(CREMA_CLARO);
            tf.setForeground(NEGRO);
            tf.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        }
        // flechitas arriba/abajo
        for (Component c : s.getComponents())
        {
            if (c instanceof JButton)
            {
                JButton b = (JButton) c;
                b.setBackground(CREMA_CLARO);
                b.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, NEGRO));
                b.setFocusPainted(false);
                b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        }
    }

    public static void estilizarScroll(JScrollPane sp)
    {
        sp.getViewport().setBackground(CREMA_CLARO);
        sp.setBorder(borde(2));
    }

    // ---------- TABLAS ----------
    /**
     * Encabezado negro, filas separadas, seleccion turquesa.
     */
    public static void estilizarTabla(JTable t)
    {
        t.setFont(fuente(Font.PLAIN, 16));
        t.setForeground(NEGRO);
        t.setBackground(CREMA_CLARO);
        t.setRowHeight(32);
        t.setGridColor(NEGRO);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(NARANJA);
        t.setSelectionForeground(NEGRO);

        JComponent header = t.getTableHeader();
        header.setFont(fuente(Font.PLAIN, 18));
        header.setBackground(NEGRO);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
    }

    // ---------- ICONOS ----------
    /**
     * Carga un icono y lo escala con buena calidad (reduce por pasos para que
     * no se vea borroso). Usar imagenes de 64 px o mas, con fondo transparente.
     *
     * Ejemplo: boton.setIcon(Estilos.icono("/img/alta.png", 24));
     *
     * @param ruta ruta dentro del classpath, empezando con "/"
     * @param tam alto/ancho final en pixeles
     * @return el icono, o null si no se encontro la imagen
     */
    public static ImageIcon icono(String ruta, int tam)
    {
        URL url = Estilos.class.getResource(ruta);
        if (url == null)
        {
            System.err.println("Icono no encontrado: " + ruta);
            return null;
        }
        try
        {
            BufferedImage img = ImageIO.read(url);
            double prop = (double) img.getHeight() / img.getWidth();
            int w = img.getWidth();
            // reducir a la mitad mientras se pueda: queda mucho mas nitido
            while (w / 2 >= tam)
            {
                w /= 2;
                img = escalar(img, w, Math.max(1, (int) (w * prop)));
            }
            img = escalar(img, tam, Math.max(1, (int) (tam * prop)));
            return new ImageIcon(img);
        } catch (IOException e)
        {
            System.err.println("No se pudo leer el icono: " + ruta);
            return null;
        }
    }

    /**
     * Icono vectorial para botones: sigue el color del texto (hover, activo,
     * etc).
     */
    public static void icono(AbstractButton b, Ikon ikon, int tam)
    {
        FontIcon fi = FontIcon.of(ikon, tam, b.getForeground());
        b.setIcon(fi);
        b.addPropertyChangeListener("foreground", e -> fi.setIconColor(b.getForeground()));
    }

    /**
     * Icono vectorial para etiquetas.
     */
    public static void icono(JLabel l, Ikon ikon, int tam)
    {
        l.setIcon(FontIcon.of(ikon, tam, l.getForeground()));
    }

    private static BufferedImage escalar(BufferedImage src, int w, int h)
    {
        BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dst.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return dst;
    }
}
