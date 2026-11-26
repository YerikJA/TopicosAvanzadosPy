package views;

import controller.AlumnoController;
import controller.CatalogoController;
import estilos.Estilos;
import exception.AppException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.Alumno;
import model.ItemCatalogo;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Pdf;

/**
 * Ventana principal de administración de alumnos (diseño "lista + detalle").
 *
 * Todo el CRUD se hace desde una sola pantalla: - Consulta: tabla con filtros
 * (se actualiza mientras escribes) y ficha del alumno a la derecha. - Alta:
 * botón "Nuevo alumno" (Ctrl+N) abre DlgAlumno. - Edición: botón "Editar",
 * doble clic o Enter sobre la tabla abre DlgAlumno con los datos. - Baja: botón
 * "Dar de baja" o tecla Supr, con confirmación.
 *
 * Así el usuario busca al alumno UNA sola vez y actúa sobre él, sin volver a
 * escribir el No. de control en otra pantalla.
 */
public class VtnAdministrador extends javax.swing.JFrame
{

    private static final Logger log = LoggerFactory.getLogger(VtnAdministrador.class);

    private static final String TODAS = "Todas";
    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy");

    // ---------- ESTADO ----------
    private final AlumnoController controllerAdministracion;
    private final CatalogoController catalogo = new CatalogoController();

    private List<ItemCatalogo> carreras = new ArrayList<>();       // catálogo (sin el "Seleccione")
    private List<ItemCatalogo> materiasCatalogo = new ArrayList<>(); // catálogo de materias
    private final List<Alumno> visibles = new ArrayList<>();  // alumnos que muestra la tabla
    private boolean cargando = false;

    // ---------- COMPONENTES ----------
    private final JButton btnNuevo = new JButton("Nuevo alumno");
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnBaja = new JButton("Dar de baja");
    private final JButton btnPdf = new JButton("Reporte PDF");
    private final JButton btnRegresar = new JButton("Regresar");

    private final JTextField txtBuscar = campoTexto("Nombre, No. de control o correo", 22);
    private final JComboBox<String> cmbCarrera = new JComboBox<>();
    private final JComboBox<String> cmbMateria = new JComboBox<>();
    private final JSpinner spMin = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 1.0));
    private final JSpinner spMax = new JSpinner(new SpinnerNumberModel(100.0, 0.0, 100.0, 1.0));
    private final JButton btnLimpiar = new JButton("Limpiar filtros");

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]
            {
                "No. control", "Nombre", "Carrera", "Promedio"
            }, 0)
    {
        @Override
        public boolean isCellEditable(int fila, int col)
        {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int col)
        {
            return col == 3 ? Double.class : String.class; // para ordenar bien el promedio
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final JLabel lblContador = new JLabel();

    // área de la tabla: muestra la tabla o un aviso cuando no hay nada que mostrar
    private final JPanel areaTabla = new JPanel(new java.awt.CardLayout());
    private final JLabel lblAvisoIcono = new JLabel();
    private final JLabel lblAvisoTitulo = new JLabel();
    private final JLabel lblAvisoTexto = new JLabel();
    private final JButton btnAviso = new JButton();
    private final JLabel lblAvisoAtajo = new JLabel();
    private boolean avisoSinAlumnos = true; // true: "aún no hay"; false: "sin resultados"

    // ficha: muestra los datos del alumno o un aviso si no hay selección
    private final JPanel areaFicha = new JPanel(new java.awt.CardLayout());

    // ficha (panel derecho)
    private final JLabel lblFNombre = new JLabel();
    private final JLabel lblFControl = new JLabel();
    private final JLabel lblFCarrera = new JLabel();
    private final JLabel lblFCorreo = new JLabel();
    private final JLabel lblFFecha = new JLabel();
    private final JLabel lblFPromedio = new JLabel();
    private final JTextArea txtFMaterias = new JTextArea(4, 20);
    private final JButton btnFEditar = new JButton("Editar");
    private final JButton btnFBaja = new JButton("Dar de baja");

    public VtnAdministrador(AlumnoController controller)
    {
        this.controllerAdministracion = controller;
        setTitle("Administración de alumnos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 620));
        setSize(1280, 760);

        construirInterfaz();
        configurarAtajos();
        cargarCatalogos();
        recargar(null);

        Estilos.estilizarVentana(this); // fondo crema + centrada
        txtBuscar.requestFocusInWindow();
    }

    // ==================================================================
    // CONSTRUCCIÓN DE LA INTERFAZ
    // ==================================================================
    private void construirInterfaz()
    {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Estilos.CREMA);
        setContentPane(raiz);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(crearBarraAcciones(), BorderLayout.NORTH);
        norte.add(crearBarraFiltros(), BorderLayout.SOUTH);
        raiz.add(norte, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(14, 0));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));
        centro.add(crearTabla(), BorderLayout.CENTER);
        centro.add(crearFicha(), BorderLayout.EAST);
        raiz.add(centro, BorderLayout.CENTER);

        lblContador.setFont(TEXTO.deriveFont(13f));
        lblContador.setForeground(Estilos.AZUL_OSCURO);
        lblContador.setBorder(BorderFactory.createEmptyBorder(0, 16, 10, 16));
        raiz.add(lblContador, BorderLayout.SOUTH);
    }

    /**
     * Barra superior: título y acciones principales.
     */
    private JPanel crearBarraAcciones()
    {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilos.AZUL_OSCURO);
        barra.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izq.setOpaque(false);
        JLabel titulo = new JLabel("ALUMNOS");
        titulo.setFont(Estilos.fuente(Font.PLAIN, 40));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 14));
        izq.add(titulo);

        // mismos botones del proyecto: principal turquesa, normales blancos
        Estilos.estilizarBotonPrincipal(btnNuevo);
        Estilos.estilizarBoton(btnEditar);
        Estilos.estilizarBoton(btnBaja);
        Estilos.estilizarBoton(btnPdf);
        Estilos.estilizarBoton(btnRegresar);
        for (JButton b : new JButton[]
        {
            btnNuevo, btnEditar, btnBaja, btnPdf, btnRegresar
        })
        {
            compactar(b, 20);
        }
        Estilos.icono(btnNuevo, FontAwesomeSolid.USER_PLUS, 18);
        Estilos.icono(btnEditar, FontAwesomeSolid.PENCIL_ALT, 18);
        Estilos.icono(btnBaja, FontAwesomeSolid.TRASH, 18);
        Estilos.icono(btnPdf, FontAwesomeSolid.FILE_PDF, 18);
        Estilos.icono(btnRegresar, FontAwesomeSolid.ARROW_LEFT, 18);

        btnNuevo.setToolTipText("Registrar un alumno nuevo (Ctrl+N)");
        btnEditar.setToolTipText("Editar el alumno seleccionado (Enter o doble clic)");
        btnBaja.setToolTipText("Dar de baja al alumno seleccionado (Supr)");
        btnPdf.setToolTipText("Generar PDF con los alumnos que se ven en la tabla");

        btnNuevo.addActionListener(e -> nuevoAlumno());
        btnEditar.addActionListener(e -> editarSeleccionado());
        btnBaja.addActionListener(e -> darBajaSeleccionado());
        btnPdf.addActionListener(e -> generarReportePdf());
        btnRegresar.addActionListener(e -> regresar());

        // grupos: [Nuevo] | [Editar, Dar de baja]  ...  [Reporte PDF, Regresar]
        izq.add(btnNuevo);
        izq.add(separadorVertical());
        izq.add(btnEditar);
        izq.add(btnBaja);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);
        der.add(btnPdf);
        der.add(btnRegresar);

        // centrado vertical para que los dos grupos queden a la misma altura
        JPanel derCentrado = new JPanel(new GridBagLayout());
        derCentrado.setOpaque(false);
        derCentrado.add(der);
        JPanel izqCentrado = new JPanel(new GridBagLayout());
        izqCentrado.setOpaque(false);
        izqCentrado.add(izq);

        barra.add(izqCentrado, BorderLayout.WEST);
        barra.add(derCentrado, BorderLayout.EAST);
        return barra;
    }

    /**
     * Filtros en una sola línea. La tabla se actualiza al momento.
     */
    private JPanel crearBarraFiltros()
    {
        // una sola fila: el buscador se estira y lo demás conserva su tamaño
        JPanel filtros = new JPanel(new GridBagLayout())
        {
            private int col = 0;

            @Override
            public java.awt.Component add(java.awt.Component c)
            {
                GridBagConstraints g = new GridBagConstraints();
                g.gridx = col++;
                g.insets = new Insets(12, 5, 12, 5);
                g.fill = GridBagConstraints.HORIZONTAL;
                g.weightx = (c == txtBuscar) ? 1 : 0;
                super.add(c, g);
                return c;
            }
        };
        filtros.setBackground(Estilos.CREMA_CLARO);
        filtros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Estilos.NEGRO),
                BorderFactory.createEmptyBorder(0, 14, 0, 14)));

        JLabel lupa = etiqueta("Buscar");
        Estilos.icono(lupa, FontAwesomeSolid.SEARCH, 16);
        filtros.add(lupa);
        Estilos.estilizarCampo(txtBuscar);
        txtBuscar.setFont(TEXTO);
        txtBuscar.setMinimumSize(new Dimension(150, txtBuscar.getPreferredSize().height));
        txtBuscar.setPreferredSize(new Dimension(240, txtBuscar.getPreferredSize().height));
        txtBuscar.setToolTipText("Escribe un nombre, un No. de control o un correo");
        filtros.add(txtBuscar);

        // separador y texto "Filtrar por": la búsqueda queda separada de los filtros
        JPanel divisor = new JPanel();
        divisor.setBackground(Estilos.AZUL_SUAVE);
        divisor.setPreferredSize(new Dimension(1, 28));
        divisor.setMinimumSize(new Dimension(1, 28));
        filtros.add(divisor);
        JLabel filtrarPor = new JLabel("Filtrar por");
        filtrarPor.setFont(TEXTO.deriveFont(13f));
        filtrarPor.setForeground(Estilos.GRIS_TEXTO);
        filtros.add(filtrarPor);

        JLabel lCarrera = etiqueta("Carrera");
        Estilos.icono(lCarrera, FontAwesomeSolid.BOOK, 16);
        filtros.add(lCarrera);
        Estilos.estilizarCombo(cmbCarrera);
        cmbCarrera.setPreferredSize(new Dimension(170, cmbCarrera.getPreferredSize().height));
        cmbCarrera.setMinimumSize(new Dimension(110, cmbCarrera.getPreferredSize().height));
        filtros.add(cmbCarrera);

        JLabel lMateria = etiqueta("Materia");
        Estilos.icono(lMateria, FontAwesomeSolid.GRADUATION_CAP, 16);
        filtros.add(lMateria);
        Estilos.estilizarCombo(cmbMateria);
        cmbMateria.setPreferredSize(new Dimension(150, cmbMateria.getPreferredSize().height));
        cmbMateria.setMinimumSize(new Dimension(100, cmbMateria.getPreferredSize().height));
        filtros.add(cmbMateria);

        JLabel lPromedio = etiqueta("Promedio");
        Estilos.icono(lPromedio, FontAwesomeSolid.CHART_LINE, 16);
        filtros.add(lPromedio);
        Estilos.estilizarSpinner(spMin);
        Estilos.estilizarSpinner(spMax);
        spMin.setPreferredSize(new Dimension(72, spMin.getPreferredSize().height));
        spMax.setPreferredSize(new Dimension(72, spMax.getPreferredSize().height));
        filtros.add(spMin);
        filtros.add(etiqueta("a"));
        filtros.add(spMax);

        // "Limpiar filtros" como enlace: es una acción secundaria
        btnLimpiar.setText("Limpiar filtros");
        java.util.Map<java.awt.font.TextAttribute, Object> subrayado = new java.util.HashMap<>();
        subrayado.put(java.awt.font.TextAttribute.UNDERLINE, java.awt.font.TextAttribute.UNDERLINE_ON);
        btnLimpiar.setFont(TEXTO.deriveFont(14f).deriveFont(subrayado));
        btnLimpiar.setMargin(new Insets(0, 0, 0, 0));
        btnLimpiar.setForeground(Estilos.AZUL);
        btnLimpiar.setContentAreaFilled(false);
        btnLimpiar.setBorderPainted(false);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        Estilos.icono(btnLimpiar, FontAwesomeSolid.BROOM, 14);
        btnLimpiar.setMinimumSize(btnLimpiar.getPreferredSize()); // que nunca se corte
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        filtros.add(btnLimpiar);

        txtBuscar.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                aplicarFiltros();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                aplicarFiltros();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                aplicarFiltros();
            }
        });
        cmbCarrera.addActionListener(e -> aplicarFiltros());
        cmbMateria.addActionListener(e -> aplicarFiltros());
        spMin.addChangeListener(e -> aplicarFiltros());
        spMax.addChangeListener(e -> aplicarFiltros());
        return filtros;
    }

    private JPanel crearTabla()
    {
        // tabla con el estilo del proyecto: encabezado negro y selección turquesa
        Estilos.estilizarTabla(tabla);
        tabla.setFont(TEXTO);               // filas en letra normal (más legible)
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tabla.setRowHeight(40);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(0xE3EEEE));
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.getTableHeader().setToolTipText("Clic en un encabezado para ordenar");

        // clic en el encabezado = ordenar (reemplaza el combo "Ordenar por")
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(280);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(180);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(80);

        // celdas con espacio a los lados y renglones alternados (más fácil seguir una fila)
        DefaultTableCellRenderer celda = new CeldaTabla(false);
        for (int i = 0; i < 3; i++)
        {
            tabla.getColumnModel().getColumn(i).setCellRenderer(celda);
        }
        tabla.getColumnModel().getColumn(3).setCellRenderer(new CeldaTabla(true));

        tabla.getSelectionModel().addListSelectionListener(e ->
                {
                    if (!e.getValueIsAdjusting())
                    {
                        mostrarFicha(seleccionado());
                    }
        });
        tabla.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() == 2 && tabla.rowAtPoint(e.getPoint()) >= 0)
                {
                    editarSeleccionado();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(Estilos.CREMA_CLARO);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // tarjeta con borde negro y sombra, como las demás pantallas
        JPanel tarjeta = new JPanel(new BorderLayout());
        Estilos.estilizarPanelConSombra(tarjeta);
        areaTabla.setOpaque(false);
        areaTabla.add(scroll, "tabla");
        areaTabla.add(crearAvisoTabla(), "aviso");
        tarjeta.add(areaTabla, BorderLayout.CENTER);
        return tarjeta;
    }

    /**
     * Aviso que ocupa el lugar de la tabla cuando está vacía: así el inicio
     * no se ve "roto" y el usuario sabe qué hacer.
     */
    private JPanel crearAvisoTabla()
    {
        JPanel aviso = new JPanel(new GridBagLayout());
        aviso.setBackground(Estilos.CREMA_CLARO);

        lblAvisoIcono.setOpaque(false);
        lblAvisoIcono.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvisoIcono.setForeground(Estilos.AZUL);
        lblAvisoIcono.setPreferredSize(new Dimension(92, 92));
        lblAvisoIcono.setBorder(BorderFactory.createEmptyBorder());

        lblAvisoTitulo.setFont(Estilos.fuente(Font.PLAIN, 32));
        lblAvisoTitulo.setForeground(Estilos.NEGRO);
        lblAvisoTexto.setFont(TEXTO);
        lblAvisoTexto.setForeground(Estilos.GRIS_TEXTO);
        lblAvisoTexto.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvisoAtajo.setFont(TEXTO.deriveFont(13f));
        lblAvisoAtajo.setForeground(Estilos.GRIS_TEXTO);

        Estilos.estilizarBotonPrincipal(btnAviso);
        compactar(btnAviso, 22);
        btnAviso.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.NEGRO, 2),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        btnAviso.addActionListener(e ->
        {
            if (avisoSinAlumnos)
            {
                nuevoAlumno();
            } else
            {
                limpiarFiltros();
            }
        });

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.insets = new Insets(0, 0, 14, 0);
        g.gridy = 0;
        aviso.add(circulo(lblAvisoIcono), g);
        g.gridy = 1;
        g.insets = new Insets(0, 0, 8, 0);
        aviso.add(lblAvisoTitulo, g);
        g.gridy = 2;
        g.insets = new Insets(0, 0, 18, 0);
        aviso.add(lblAvisoTexto, g);
        g.gridy = 3;
        g.insets = new Insets(0, 0, 8, 0);
        aviso.add(btnAviso, g);
        g.gridy = 4;
        aviso.add(lblAvisoAtajo, g);
        return aviso;
    }

    /**
     * Pone el ícono dentro de un círculo de color suave.
     */
    private JPanel circulo(JLabel icono)
    {
        JPanel c = new JPanel(new BorderLayout())
        {
            @Override
            protected void paintComponent(java.awt.Graphics g)
            {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Estilos.HOVER_SUAVE);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        c.setOpaque(false);
        c.setPreferredSize(new Dimension(92, 92));
        c.add(icono, BorderLayout.CENTER);
        return c;
    }

    /**
     * Ajusta el aviso: sin alumnos registrados, o sin resultados por filtros.
     */
    private void mostrarAviso(boolean sinAlumnos)
    {
        avisoSinAlumnos = sinAlumnos;
        if (sinAlumnos)
        {
            Estilos.icono(lblAvisoIcono, FontAwesomeSolid.USERS, 44);
            lblAvisoTitulo.setText("AÚN NO HAY ALUMNOS REGISTRADOS");
            lblAvisoTexto.setText("<html><div style='text-align:center;width:340px'>Cuando registres alumnos aparecerán aquí. "
                    + "Podrás buscarlos, filtrarlos y ver su ficha con un clic.</div></html>");
            btnAviso.setText("Registrar el primer alumno");
            Estilos.icono(btnAviso, FontAwesomeSolid.USER_PLUS, 18);
            lblAvisoAtajo.setText("o presiona Ctrl + N");
        } else
        {
            Estilos.icono(lblAvisoIcono, FontAwesomeSolid.SEARCH, 40);
            lblAvisoTitulo.setText("NINGÚN ALUMNO COINCIDE");
            lblAvisoTexto.setText("<html><div style='text-align:center;width:340px'>Prueba con otro nombre o número de control, "
                    + "o quita algunos filtros.</div></html>");
            btnAviso.setText("Limpiar filtros");
            Estilos.icono(btnAviso, FontAwesomeSolid.BROOM, 18);
            lblAvisoAtajo.setText(" ");
        }
        ((java.awt.CardLayout) areaTabla.getLayout()).show(areaTabla, "aviso");
    }

    /**
     * Ficha del alumno seleccionado, con sus acciones al alcance.
     */
    private JPanel crearFicha()
    {
        JPanel tarjeta = new JPanel(new BorderLayout());
        Estilos.estilizarPanelConSombra(tarjeta);
        tarjeta.setPreferredSize(new Dimension(350, 0));
        JPanel ficha = new PanelAjustable(new GridBagLayout());
        ficha.setOpaque(false);
        ficha.setBorder(BorderFactory.createEmptyBorder(18, 22, 4, 22));

        lblFNombre.setFont(Estilos.fuente(Font.PLAIN, 30));
        lblFNombre.setForeground(Estilos.NEGRO);
        lblFControl.setFont(TEXTO.deriveFont(14f));
        lblFControl.setForeground(Estilos.GRIS_TEXTO);
        lblFPromedio.setFont(Estilos.fuente(Font.PLAIN, 44));
        lblFPromedio.setForeground(Estilos.AZUL);

        txtFMaterias.setFont(TEXTO);
        txtFMaterias.setForeground(Estilos.NEGRO);
        txtFMaterias.setEditable(false);
        txtFMaterias.setLineWrap(true);
        txtFMaterias.setWrapStyleWord(true);
        txtFMaterias.setOpaque(false);
        txtFMaterias.setBorder(BorderFactory.createEmptyBorder());

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        g.gridy = 0;
        g.insets = new Insets(0, 0, 2, 0);
        ficha.add(lblFNombre, g);
        g.gridy = 1;
        g.insets = new Insets(0, 0, 10, 0);
        ficha.add(lblFControl, g);

        // promedio destacado entre dos líneas
        JPanel prom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        prom.setOpaque(false);
        prom.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0xE3EEEE)),
                BorderFactory.createEmptyBorder(4, 0, 4, 0)));
        prom.add(lblFPromedio);
        JLabel lProm = new JLabel("PROMEDIO");
        lProm.setFont(Estilos.fuente(Font.PLAIN, 16));
        lProm.setForeground(Estilos.GRIS_TEXTO);
        prom.add(lProm);
        g.gridy = 2;
        g.insets = new Insets(0, 0, 12, 0);
        ficha.add(prom, g);

        // cada dato con su título arriba y el valor abajo (menos amontonado)
        g.insets = new Insets(0, 0, 10, 0);
        g.gridy = 3;
        ficha.add(dato("Carrera", FontAwesomeSolid.BOOK, lblFCarrera), g);
        g.gridy = 4;
        ficha.add(dato("Correo", FontAwesomeSolid.ENVELOPE, lblFCorreo), g);
        g.gridy = 5;
        ficha.add(dato("Nacimiento", FontAwesomeSolid.CALENDAR_ALT, lblFFecha), g);
        g.gridy = 6;
        ficha.add(dato("Materias", FontAwesomeSolid.GRADUATION_CAP, txtFMaterias), g);
        // relleno: deja los datos arriba
        g.gridy = 7;
        g.weighty = 1;
        ficha.add(javax.swing.Box.createVerticalGlue(), g);

        JPanel acciones = new JPanel(new java.awt.GridLayout(1, 2, 10, 0));
        acciones.setOpaque(false);
        Estilos.estilizarBotonPrincipal(btnFEditar);
        Estilos.estilizarBoton(btnFBaja);
        compactar(btnFEditar, 19);
        compactar(btnFBaja, 19);
        Estilos.icono(btnFEditar, FontAwesomeSolid.PENCIL_ALT, 16);
        Estilos.icono(btnFBaja, FontAwesomeSolid.TRASH, 16);
        btnFEditar.addActionListener(e -> editarSeleccionado());
        btnFBaja.addActionListener(e -> darBajaSeleccionado());
        acciones.add(btnFEditar);
        acciones.add(btnFBaja);
        acciones.setBorder(BorderFactory.createEmptyBorder(10, 22, 20, 22));

        // si la ventana es chica, los datos se desplazan y los botones siguen a la vista
        JScrollPane scrollFicha = new JScrollPane(ficha,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollFicha.setBorder(BorderFactory.createEmptyBorder());
        scrollFicha.setOpaque(false);
        scrollFicha.getViewport().setOpaque(false);
        scrollFicha.getVerticalScrollBar().setUnitIncrement(16);
        JPanel conBotones = new JPanel(new BorderLayout());
        conBotones.setOpaque(false);
        conBotones.add(scrollFicha, BorderLayout.CENTER);
        conBotones.add(acciones, BorderLayout.SOUTH);

        // aviso cuando no hay alumno seleccionado
        JPanel vacia = new JPanel(new GridBagLayout());
        vacia.setOpaque(false);
        JLabel icono = new JLabel();
        icono.setForeground(new Color(0x9FB8B8));
        Estilos.icono(icono, FontAwesomeSolid.SEARCH, 38);
        JLabel titulo = new JLabel("FICHA DEL ALUMNO");
        titulo.setFont(Estilos.fuente(Font.PLAIN, 24));
        titulo.setForeground(Estilos.GRIS_TEXTO);
        JLabel texto = new JLabel("<html><div style='text-align:center;width:230px'>"
                + "Aquí verás los datos del alumno que selecciones en la tabla.</div></html>");
        texto.setFont(TEXTO.deriveFont(14f));
        texto.setForeground(Estilos.GRIS_TEXTO);
        GridBagConstraints v = new GridBagConstraints();
        v.gridx = 0;
        v.gridy = 0;
        v.insets = new Insets(0, 0, 12, 0);
        vacia.add(icono, v);
        v.gridy = 1;
        v.insets = new Insets(0, 0, 8, 0);
        vacia.add(titulo, v);
        v.gridy = 2;
        vacia.add(texto, v);

        areaFicha.setOpaque(false);
        areaFicha.add(conBotones, "datos");
        areaFicha.add(vacia, "vacia");
        tarjeta.add(areaFicha, BorderLayout.CENTER);
        return tarjeta;
    }

    /**
     * Un dato de la ficha: título pequeño con ícono arriba y el valor abajo.
     */
    private JPanel dato(String nombre, org.kordamp.ikonli.Ikon icono, JComponent valor)
    {
        JPanel p = new JPanel(new BorderLayout(0, 3));
        p.setOpaque(false);
        JLabel l = new JLabel(nombre.toUpperCase());
        l.setFont(Estilos.fuente(Font.PLAIN, 16));
        l.setForeground(Estilos.GRIS_TEXTO);
        Estilos.icono(l, icono, 13);
        l.setIconTextGap(6);
        p.add(l, BorderLayout.NORTH);
        if (valor instanceof JLabel)
        {
            valor.setFont(TEXTO);
            valor.setForeground(Estilos.NEGRO);
        }
        p.add(valor, BorderLayout.CENTER);
        return p;
    }

    /**
     * Atajos de teclado: Ctrl+N nuevo, Enter editar, Supr dar de baja, Ctrl+F
     * buscar.
     */
    private void configurarAtajos()
    {
        JComponent r = getRootPane();
        atajo(r, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK), "nuevo", this::nuevoAlumno);
        atajo(r, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), "buscar", txtBuscar::requestFocusInWindow);
        // Enter y Supr solo cuando la tabla tiene el foco (para no estorbar al escribir)
        tabla.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "editar");
        tabla.getActionMap().put("editar", accion(this::editarSeleccionado));
        tabla.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "baja");
        tabla.getActionMap().put("baja", accion(this::darBajaSeleccionado));
        // Flecha abajo desde el buscador baja a la tabla
        txtBuscar.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "aTabla");
        txtBuscar.getActionMap().put("aTabla", accion(() ->
                {
                    tabla.requestFocusInWindow();
                    if (tabla.getRowCount() > 0 && tabla.getSelectedRow() < 0)
                    {
                        tabla.setRowSelectionInterval(0, 0);
                    }
        }));
    }

    // ==================================================================
    // DATOS
    // ==================================================================
    private void cargarCatalogos()
    {
        try
        {
            List<ItemCatalogo> lista = catalogo.obtenerCarreras();
            // el primer elemento es el texto "Seleccione una carrera"
            carreras = new ArrayList<>(lista.subList(Math.min(1, lista.size()), lista.size()));
        } catch (AppException e)
        {
            log.error("Error al cargar las carreras", e);
            error("No se pudieron cargar las carreras", e.getMessage());
        }
        try
        {
            for (ItemCatalogo m : catalogo.obtenerMaterias())
            {
                materiasCatalogo.add(m);
            }
        } catch (AppException e)
        {
            log.error("Error al cargar las materias", e);
            error("No se pudieron cargar las materias", e.getMessage());
        }

        cargando = true;
        DefaultComboBoxModel<String> mCarreras = new DefaultComboBoxModel<>();
        mCarreras.addElement(TODAS);
        for (ItemCatalogo c : carreras)
        {
            mCarreras.addElement(c.getNombre());
        }
        cmbCarrera.setModel(mCarreras);
        cargando = false;
    }

    /**
     * Vuelve a leer los alumnos del controlador y deja seleccionado el alumno
     * indicado (o ninguno).
     */
    private void recargar(String noControlSeleccionar)
    {
        cargando = true;
        Object materiaPrevia = cmbMateria.getSelectedItem();
        TreeSet<String> materias = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (ItemCatalogo m : materiasCatalogo)
        {
            materias.add(m.getNombre());
        }
        for (Alumno a : controllerAdministracion.consultarAlumno())
        {
            materias.addAll(nombresMaterias(a.getMaterias()));
        }
        DefaultComboBoxModel<String> mMaterias = new DefaultComboBoxModel<>();
        mMaterias.addElement(TODAS);
        for (String m : materias)
        {
            mMaterias.addElement(m);
        }
        cmbMateria.setModel(mMaterias);
        if (materiaPrevia != null && materias.contains(materiaPrevia.toString()))
        {
            cmbMateria.setSelectedItem(materiaPrevia);
        }
        cargando = false;

        aplicarFiltros();
        if (noControlSeleccionar != null)
        {
            seleccionarPorNoControl(noControlSeleccionar);
        }
    }

    private void aplicarFiltros()
    {
        if (cargando)
        {
            return;
        }
        String noControlPrevio = seleccionado() != null ? seleccionado().getNo_control() : null;

        String texto = txtBuscar.getText().trim().toLowerCase();
        String carrera = String.valueOf(cmbCarrera.getSelectedItem());
        String materia = String.valueOf(cmbMateria.getSelectedItem());
        double min = ((Number) spMin.getValue()).doubleValue();
        double max = ((Number) spMax.getValue()).doubleValue();
        if (min > max)
        {
            double t = min;
            min = max;
            max = t;
        }

        List<Alumno> todos = controllerAdministracion.consultarAlumno();
        visibles.clear();
        modelo.setRowCount(0);
        for (Alumno a : todos)
        {
            boolean coincideTexto = texto.isEmpty()
                    || texto(a.getNombre()).toLowerCase().contains(texto)
                    || texto(a.getNo_control()).toLowerCase().contains(texto)
                    || texto(a.getCorreo()).toLowerCase().contains(texto);
            boolean coincideCarrera = TODAS.equals(carrera)
                    || texto(a.getCarerra()).equalsIgnoreCase(carrera);
            boolean coincideMateria = TODAS.equals(materia)
                    || nombresMaterias(a.getMaterias()).contains(materia);
            boolean enRango = a.getPromedio() >= min && a.getPromedio() <= max;

            if (coincideTexto && coincideCarrera && coincideMateria && enRango)
            {
                visibles.add(a);
                modelo.addRow(new Object[]
                {
                    a.getNo_control(), a.getNombre(), texto(a.getCarerra()), a.getPromedio()
                });
            }
        }

        if (noControlPrevio != null)
        {
            seleccionarPorNoControl(noControlPrevio);
        }
        if (seleccionado() == null)
        {
            mostrarFicha(null);
        }

        // tabla o aviso, según haya algo que mostrar
        if (todos.isEmpty())
        {
            mostrarAviso(true);
        } else if (visibles.isEmpty())
        {
            mostrarAviso(false);
        } else
        {
            ((java.awt.CardLayout) areaTabla.getLayout()).show(areaTabla, "tabla");
        }
        // sin alumnos no tiene caso filtrar ni sacar PDF
        for (JComponent c : new JComponent[]
        {
            txtBuscar, cmbCarrera, cmbMateria, spMin, spMax, btnLimpiar
        })
        {
            c.setEnabled(!todos.isEmpty());
        }
        btnPdf.setEnabled(!visibles.isEmpty());

        lblContador.setText(todos.isEmpty()
                ? "0 alumnos registrados"
                : "Mostrando " + visibles.size() + " de " + todos.size() + " alumnos"
                + "   ·   Doble clic o Enter para editar   ·   Supr para dar de baja");
    }

    private void limpiarFiltros()
    {
        cargando = true;
        txtBuscar.setText("");
        cmbCarrera.setSelectedIndex(0);
        cmbMateria.setSelectedIndex(0);
        spMin.setValue(0.0);
        spMax.setValue(100.0);
        cargando = false;
        aplicarFiltros();
        txtBuscar.requestFocusInWindow();
    }

    /**
     * Alumno de la fila seleccionada (tomando en cuenta el orden de la tabla).
     */
    private Alumno seleccionado()
    {
        int fila = tabla.getSelectedRow();
        if (fila < 0)
        {
            return null;
        }
        int filaModelo = tabla.convertRowIndexToModel(fila);
        return filaModelo < visibles.size() ? visibles.get(filaModelo) : null;
    }

    private void seleccionarPorNoControl(String noControl)
    {
        for (int i = 0; i < visibles.size(); i++)
        {
            if (visibles.get(i).getNo_control().equals(noControl))
            {
                int fila = tabla.convertRowIndexToView(i);
                tabla.setRowSelectionInterval(fila, fila);
                tabla.scrollRectToVisible(tabla.getCellRect(fila, 0, true));
                return;
            }
        }
        tabla.clearSelection();
    }

    private void mostrarFicha(Alumno a)
    {
        boolean hay = a != null;
        btnEditar.setEnabled(hay);
        btnBaja.setEnabled(hay);
        btnFEditar.setEnabled(hay);
        btnFBaja.setEnabled(hay);

        java.awt.CardLayout cl = (java.awt.CardLayout) areaFicha.getLayout();
        if (!hay)
        {
            cl.show(areaFicha, "vacia");
            return;
        }
        cl.show(areaFicha, "datos");
        // <html> permite que un nombre largo baje de renglón en vez de cortarse
        lblFNombre.setText("<html>" + texto(a.getNombre()) + "</html>");
        lblFControl.setText("No. de control " + texto(a.getNo_control()));
        // <html> deja que una carrera larga baje de renglón en vez de cortarse
        lblFCarrera.setText("<html>" + texto(a.getCarerra()) + "</html>");
        lblFCorreo.setText(texto(a.getCorreo()));
        lblFCorreo.setToolTipText(texto(a.getCorreo()));
        lblFFecha.setText(a.getFechaNaciomiento() == null ? "-" : FORMATO_FECHA.format(a.getFechaNaciomiento()));
        lblFPromedio.setText(String.format(Locale.US, "%.1f", a.getPromedio()));
        List<String> ms = nombresMaterias(a.getMaterias());
        txtFMaterias.setText(ms.isEmpty() ? "Sin materias asignadas" : String.join("\n", ms));
        txtFMaterias.setCaretPosition(0);
    }

    // ==================================================================
    // ACCIONES (ALTA, EDICIÓN, BAJA, PDF)
    // ==================================================================
    private void nuevoAlumno()
    {
        DlgAlumno dlg = new DlgAlumno(this, controllerAdministracion, carreras, materiasCatalogo, null);
        dlg.setVisible(true);
        Alumno guardado = dlg.getResultado();
        if (guardado != null)
        {
            limpiarFiltros(); // para que el alumno nuevo se vea seguro
            recargar(guardado.getNo_control());
        }
    }

    private void editarSeleccionado()
    {
        Alumno a = seleccionado();
        if (a == null)
        {
            return;
        }
        DlgAlumno dlg = new DlgAlumno(this, controllerAdministracion, carreras, materiasCatalogo, a);
        dlg.setVisible(true);
        if (dlg.getResultado() != null)
        {
            recargar(a.getNo_control());
        }
    }

    private void darBajaSeleccionado()
    {
        Alumno a = seleccionado();
        if (a == null)
        {
            return;
        }
        Object[] opciones =
        {
            "Dar de baja", "Cancelar"
        };
        int r = JOptionPane.showOptionDialog(this,
                "¿Dar de baja a " + a.getNombre() + " (No. de control " + a.getNo_control() + ")?\n"
                + "Esta acción no se puede deshacer.",
                "Confirmar baja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                null, opciones, opciones[1]); // "Cancelar" queda como opción por defecto
        if (r != 0)
        {
            return;
        }

        int filaAnterior = tabla.getSelectedRow();
        if (controllerAdministracion.darBajaAlumno(a))
        {
            log.info("Alumno con No. Control {} dado de baja", a.getNo_control());
            recargar(null);
            // seleccionar el siguiente alumno para seguir trabajando sin tocar el mouse
            if (tabla.getRowCount() > 0)
            {
                int fila = Math.min(filaAnterior, tabla.getRowCount() - 1);
                tabla.setRowSelectionInterval(fila, fila);
            }
            tabla.requestFocusInWindow();
        } else
        {
            error("No se pudo dar de baja", "El alumno ya no existe en la lista.");
        }
    }

    private void generarReportePdf()
    {
        if (visibles.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "No hay alumnos en la tabla para generar el reporte.",
                    "Reporte PDF", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // el PDF sale en el mismo orden en que se ve la tabla
        List<Alumno> enOrden = new ArrayList<>();
        for (int i = 0; i < tabla.getRowCount(); i++)
        {
            enOrden.add(visibles.get(tabla.convertRowIndexToModel(i)));
        }
        Pdf.generarPdf(enOrden);
    }

    private void regresar()
    {
        new VtnHome(controllerAdministracion).setVisible(true);
        dispose();
    }

    static final Font TEXTO = new Font("Segoe UI", Font.PLAIN, 15);

    static void compactar(JButton b, int tam)
    {
        b.setFont(Estilos.fuente(Font.PLAIN, tam));
        b.setIconTextGap(8);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.NEGRO, 2),
                BorderFactory.createEmptyBorder(5, 14, 5, 14)));
    }

    /**
     * Panel que se ajusta al ancho visible: así los textos largos de la ficha
     * bajan de renglón y solo se desplaza hacia abajo si no cabe.
     */
    private static class PanelAjustable extends JPanel implements javax.swing.Scrollable
    {

        PanelAjustable(java.awt.LayoutManager layout)
        {
            super(layout);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize()
        {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(java.awt.Rectangle r, int orientacion, int dir)
        {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(java.awt.Rectangle r, int orientacion, int dir)
        {
            return r.height;
        }

        @Override
        public boolean getScrollableTracksViewportWidth()
        {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight()
        {
            // si sobra alto, ocupa todo (los botones quedan abajo); si falta, se desplaza
            return getParent() != null && getParent().getHeight() > getPreferredSize().height;
        }
    }

    /**
     * Línea vertical para separar grupos de botones en la barra superior.
     */
    private static JComponent separadorVertical()
    {
        JPanel sep = new JPanel();
        sep.setBackground(new Color(0x2E5A60));
        sep.setPreferredSize(new Dimension(2, 30));
        JPanel envoltura = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        envoltura.setOpaque(false);
        envoltura.add(sep);
        return envoltura;
    }

    /**
     * Celda de la tabla con espacio a los lados y renglones alternados. Si es
     * la columna de promedio, va a la derecha, con un decimal y en negritas.
     */
    private static class CeldaTabla extends DefaultTableCellRenderer
    {

        private static final Color ALTERNO = new Color(0xF4F9FA);
        private final boolean promedio;

        CeldaTabla(boolean promedio)
        {
            this.promedio = promedio;
            setHorizontalAlignment(promedio ? SwingConstants.RIGHT : SwingConstants.LEFT);
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable t, Object valor,
                boolean sel, boolean foco, int fila, int col)
        {
            super.getTableCellRendererComponent(t, valor, sel, false, fila, col);
            setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
            setFont(promedio ? TEXTO.deriveFont(Font.BOLD) : TEXTO);
            if (promedio)
            {
                setText(valor instanceof Double ? String.format(Locale.US, "%.1f", (Double) valor) : "");
            }
            if (!sel)
            {
                setBackground(fila % 2 == 0 ? Color.WHITE : ALTERNO);
                setForeground(t.isEnabled() ? Estilos.NEGRO : Estilos.GRIS_TEXTO);
            }
            return this;
        }
    }

    /**
     * Etiqueta con el estilo del proyecto (azul oscuro, fuente de títulos).
     */
    static JLabel etiqueta(String texto)
    {
        JLabel l = new JLabel(texto);
        Estilos.estilizarEtiqueta(l);
        return l;
    }

    /**
     * Campo de texto que muestra una ayuda gris mientras está vacío.
     */
    static JTextField campoTexto(String ayuda, int columnas)
    {
        return new JTextField(columnas)
        {
            @Override
            protected void paintComponent(java.awt.Graphics g)
            {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner() && isEditable())
                {
                    java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                    g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
                            java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(Estilos.GRIS_TEXTO);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets in = getInsets();
                    int y = (getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
                    g2.drawString(ayuda, in.left, y);
                    g2.dispose();
                }
            }

            
            {
                // repintar al entrar/salir para mostrar u ocultar la ayuda
                addFocusListener(new java.awt.event.FocusAdapter()
                {
                    @Override
                    public void focusGained(java.awt.event.FocusEvent e)
                    {
                        repaint();
                    }

                    @Override
                    public void focusLost(java.awt.event.FocusEvent e)
                    {
                        repaint();
                    }
                });
            }
        };
    }

    static void atajo(JComponent c, KeyStroke tecla, String nombre, Runnable r)
    {
        c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(tecla, nombre);
        c.getActionMap().put(nombre, accion(r));
    }

    static AbstractAction accion(Runnable r)
    {
        return new AbstractAction()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                r.run();
            }
        };
    }

    /**
     * Texto seguro de cualquier objeto (evita "null").
     */
    static String texto(Object o)
    {
        return o == null ? "" : o.toString().trim();
    }

    static List<String> nombresMaterias(List<ItemCatalogo> materias)
    {
        List<String> nombres = new ArrayList<>();
        if (materias != null)
        {
            for (ItemCatalogo m : materias)
            {
                if (m != null && m.getNombre() != null)
                {
                    nombres.add(m.getNombre());
                }
            }
        }
        return nombres;
    }

    private void error(String titulo, String msg)
    {
        JOptionPane.showMessageDialog(this, msg, titulo, JOptionPane.ERROR_MESSAGE);
    }
}
