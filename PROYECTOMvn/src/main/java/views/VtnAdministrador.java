package views;

import controller.AlumnoController;
import controller.CatalogoController;
import estilos.Estilos;
import exception.AppException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
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
 * Todo el CRUD se hace desde una sola pantalla, sin ventanas emergentes: -
 * Consulta: tabla con filtros (se actualiza mientras escribes) y ficha del
 * alumno en el panel derecho. - Alta: "Nuevo alumno" (Ctrl+N) cambia el panel
 * derecho al formulario (PnlAlumno). - Edición: "Editar", doble clic o Enter
 * muestra el formulario con los datos. - Baja: "Dar de baja" o tecla Supr,
 * con confirmación.
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

    private List<String> carreras = new ArrayList<>();       // catálogo (sin el "Seleccione")
    private List<String> materiasCatalogo = new ArrayList<>(); // catálogo de materias
    private final List<Alumno> visibles = new ArrayList<>();  // alumnos que muestra la tabla
    private boolean cargando = false;
    private boolean editando = false; // true mientras el formulario está abierto

    // panel derecho: muestra la ficha (consulta) o el formulario (alta / edición)
    private static final int ANCHO_FICHA = 360;
    private static final int ANCHO_FORMULARIO = 520;
    private final JPanel panelDerecho = new JPanel(new java.awt.CardLayout());
    private final JPanel tarjetaFormulario = new JPanel(new BorderLayout());

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
        panelDerecho.setOpaque(false);
        panelDerecho.add(crearFicha(), "ficha");
        Estilos.estilizarPanelConSombra(tarjetaFormulario);
        panelDerecho.add(tarjetaFormulario, "formulario");
        panelDerecho.setPreferredSize(new Dimension(ANCHO_FICHA, 0));
        centro.add(panelDerecho, BorderLayout.EAST);
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
        barra.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

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

        izq.add(btnNuevo);
        izq.add(btnEditar);
        izq.add(btnBaja);
        izq.add(btnPdf);

        barra.add(izq, BorderLayout.WEST);
        barra.add(btnRegresar, BorderLayout.EAST);
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
                g.insets = new Insets(8, 4, 8, 4);
                g.fill = GridBagConstraints.HORIZONTAL;
                g.weightx = (c == txtBuscar) ? 1 : 0;
                super.add(c, g);
                return c;
            }
        };
        filtros.setBackground(Estilos.CREMA_CLARO);
        filtros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Estilos.NEGRO),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)));

        JLabel lupa = etiqueta("Buscar");
        Estilos.icono(lupa, FontAwesomeSolid.SEARCH, 16);
        filtros.add(lupa);
        Estilos.estilizarCampo(txtBuscar);
        txtBuscar.setFont(TEXTO);
        txtBuscar.setMinimumSize(new Dimension(180, txtBuscar.getPreferredSize().height));
        txtBuscar.setPreferredSize(new Dimension(260, txtBuscar.getPreferredSize().height));
        txtBuscar.setToolTipText("Escribe un nombre, un No. de control o un correo");
        filtros.add(txtBuscar);

        JLabel lCarrera = etiqueta("Carrera");
        Estilos.icono(lCarrera, FontAwesomeSolid.BOOK, 16);
        filtros.add(lCarrera);
        Estilos.estilizarCombo(cmbCarrera);
        cmbCarrera.setPreferredSize(new Dimension(190, cmbCarrera.getPreferredSize().height));
        cmbCarrera.setMinimumSize(new Dimension(130, cmbCarrera.getPreferredSize().height));
        filtros.add(cmbCarrera);

        JLabel lMateria = etiqueta("Materia");
        Estilos.icono(lMateria, FontAwesomeSolid.GRADUATION_CAP, 16);
        filtros.add(lMateria);
        Estilos.estilizarCombo(cmbMateria);
        cmbMateria.setPreferredSize(new Dimension(170, cmbMateria.getPreferredSize().height));
        cmbMateria.setMinimumSize(new Dimension(120, cmbMateria.getPreferredSize().height));
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

        Estilos.estilizarBoton(btnLimpiar);
        compactar(btnLimpiar, 18);
        Estilos.icono(btnLimpiar, FontAwesomeSolid.BROOM, 16);
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
        tabla.setGridColor(Estilos.AZUL_SUAVE);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 36));
        tabla.getTableHeader().setToolTipText("Clic en un encabezado para ordenar");

        // clic en el encabezado = ordenar (reemplaza el combo "Ordenar por")
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(280);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(180);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(80);

        DefaultTableCellRenderer derecha = new DefaultTableCellRenderer()
        {
            @Override
            protected void setValue(Object valor)
            {
                setText(valor instanceof Double ? String.format(Locale.US, "%.1f", (Double) valor) : "");
            }
        };
        derecha.setHorizontalAlignment(SwingConstants.RIGHT);
        tabla.getColumnModel().getColumn(3).setCellRenderer(derecha);

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
        tarjeta.add(scroll, BorderLayout.CENTER);
        return tarjeta;
    }

    /**
     * Ficha del alumno seleccionado, con sus acciones al alcance.
     */
    private JPanel crearFicha()
    {
        JPanel tarjeta = new JPanel(new BorderLayout());
        Estilos.estilizarPanelConSombra(tarjeta);
        tarjeta.setPreferredSize(new Dimension(360, 0));
        JPanel ficha = new JPanel(new GridBagLayout());
        ficha.setOpaque(false);
        ficha.setBorder(BorderFactory.createEmptyBorder(16, 18, 22, 22));
        tarjeta.add(ficha, BorderLayout.CENTER);

        lblFNombre.setFont(Estilos.fuente(Font.PLAIN, 26));
        lblFNombre.setForeground(Estilos.NEGRO);
        lblFControl.setFont(TEXTO);
        lblFControl.setForeground(Estilos.AZUL_OSCURO);
        lblFPromedio.setFont(Estilos.fuente(Font.PLAIN, 40));
        lblFPromedio.setForeground(Estilos.AZUL);

        txtFMaterias.setFont(TEXTO);
        txtFMaterias.setForeground(Estilos.NEGRO);
        txtFMaterias.setEditable(false);
        txtFMaterias.setLineWrap(true);
        txtFMaterias.setWrapStyleWord(true);
        txtFMaterias.setBackground(Estilos.CREMA);
        txtFMaterias.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridwidth = 2;
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.insets = new Insets(0, 0, 2, 0);
        g.gridy = 0;
        ficha.add(lblFNombre, g);
        g.gridy = 1;
        g.insets = new Insets(0, 0, 14, 0);
        ficha.add(lblFControl, g);

        g.gridwidth = 1;
        g.insets = new Insets(0, 0, 8, 10);
        agregarDato(ficha, g, 2, "Carrera", FontAwesomeSolid.BOOK, lblFCarrera);
        agregarDato(ficha, g, 3, "Correo", FontAwesomeSolid.ENVELOPE, lblFCorreo);
        agregarDato(ficha, g, 4, "Nacimiento", FontAwesomeSolid.CALENDAR_ALT, lblFFecha);
        agregarDato(ficha, g, 5, "Promedio", FontAwesomeSolid.CHART_LINE, lblFPromedio);

        g.gridx = 0;
        g.gridwidth = 2;
        g.gridy = 6;
        g.insets = new Insets(8, 0, 4, 0);
        JLabel lMaterias = etiqueta("Materias");
        Estilos.icono(lMaterias, FontAwesomeSolid.GRADUATION_CAP, 16);
        ficha.add(lMaterias, g);
        g.gridy = 7;
        g.weighty = 1;
        g.fill = GridBagConstraints.BOTH;
        g.insets = new Insets(0, 0, 12, 0);
        JScrollPane spMaterias = new JScrollPane(txtFMaterias);
        spMaterias.setBorder(BorderFactory.createEmptyBorder());
        ficha.add(spMaterias, g);

        JPanel acciones = new JPanel(new java.awt.GridLayout(2, 1, 0, 8));
        acciones.setOpaque(false);
        Estilos.estilizarBotonPrincipal(btnFEditar);
        Estilos.estilizarBoton(btnFBaja);
        compactar(btnFEditar, 18);
        compactar(btnFBaja, 18);
        Estilos.icono(btnFEditar, FontAwesomeSolid.PENCIL_ALT, 16);
        Estilos.icono(btnFBaja, FontAwesomeSolid.TRASH, 16);
        btnFEditar.addActionListener(e -> editarSeleccionado());
        btnFBaja.addActionListener(e -> darBajaSeleccionado());
        acciones.add(btnFEditar);
        acciones.add(btnFBaja);
        g.gridy = 8;
        g.weighty = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 0, 0);
        ficha.add(acciones, g);
        return tarjeta;
    }

    private void agregarDato(JPanel p, GridBagConstraints g, int fila, String nombre,
            org.kordamp.ikonli.Ikon icono, JLabel valor)
    {
        g.gridy = fila;
        g.gridx = 0;
        g.weightx = 0;
        JLabel l = etiqueta(nombre);
        Estilos.icono(l, icono, 14);
        p.add(l, g);
        g.gridx = 1;
        g.weightx = 1;
        if (valor != lblFPromedio)
        {
            valor.setFont(TEXTO);
        }
        valor.setForeground(Estilos.NEGRO);
        p.add(valor, g);
    }

    /**
     * Atajos de teclado: Ctrl+N nuevo, Enter editar, Supr dar de baja, Ctrl+F
     * buscar.
     */
    private void configurarAtajos()
    {
        JComponent r = getRootPane();
        atajo(r, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK), "nuevo", () ->
        {
            if (!editando)
            {
                nuevoAlumno();
            }
        });
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
            List<String> lista = catalogo.obtenerCarreras();
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
                materiasCatalogo.add(m.getNombre());
            }
        } catch (AppException e)
        {
            log.error("Error al cargar las materias", e);
            error("No se pudieron cargar las materias", e.getMessage());
        }

        cargando = true;
        DefaultComboBoxModel<String> mCarreras = new DefaultComboBoxModel<>();
        mCarreras.addElement(TODAS);
        for (String c : carreras)
        {
            mCarreras.addElement(c);
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

        lblContador.setText(todos.isEmpty()
                ? "Aún no hay alumnos. Pulsa «Nuevo alumno» (Ctrl+N) para registrar el primero."
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

        if (!hay)
        {
            lblFNombre.setText("Selecciona un alumno");
            lblFControl.setText("Elige una fila de la tabla para ver sus datos");
            lblFCarrera.setText("-");
            lblFCorreo.setText("-");
            lblFFecha.setText("-");
            lblFPromedio.setText("-");
            txtFMaterias.setText("");
            return;
        }
        // <html> permite que un nombre largo baje de renglón en vez de cortarse
        lblFNombre.setText("<html>" + texto(a.getNombre()) + "</html>");
        lblFControl.setText("No. de control " + texto(a.getNo_control()));
        lblFCarrera.setText(texto(a.getCarerra()));
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
        abrirFormulario(null);
    }

    private void editarSeleccionado()
    {
        Alumno a = seleccionado();
        if (a != null && !editando)
        {
            abrirFormulario(a);
        }
    }

    /**
     * Cambia el panel derecho de la ficha al formulario (alta si original es
     * null, edición si trae un alumno). Mientras está abierto, la tabla, los
     * filtros y los botones de arriba se bloquean.
     */
    private void abrirFormulario(Alumno original)
    {
        PnlAlumno formulario = new PnlAlumno(controllerAdministracion, carreras, materiasCatalogo,
                original, guardado -> cerrarFormulario(original, guardado));
        tarjetaFormulario.removeAll();
        tarjetaFormulario.add(formulario, BorderLayout.CENTER);

        bloquear(true);
        panelDerecho.setPreferredSize(new Dimension(ANCHO_FORMULARIO, 0));
        ((java.awt.CardLayout) panelDerecho.getLayout()).show(panelDerecho, "formulario");
        panelDerecho.revalidate();
        panelDerecho.repaint();
        formulario.enfocar();
    }

    /**
     * Regresa el panel derecho a la ficha. guardado = alumno guardado, o null
     * si se canceló.
     */
    private void cerrarFormulario(Alumno original, Alumno guardado)
    {
        bloquear(false);
        panelDerecho.setPreferredSize(new Dimension(ANCHO_FICHA, 0));
        ((java.awt.CardLayout) panelDerecho.getLayout()).show(panelDerecho, "ficha");
        tarjetaFormulario.removeAll();
        panelDerecho.revalidate();
        panelDerecho.repaint();

        if (guardado != null)
        {
            if (original == null)
            {
                limpiarFiltros(); // para que el alumno nuevo se vea seguro
            }
            recargar(guardado.getNo_control());
        } else
        {
            mostrarFicha(seleccionado());
        }
        tabla.requestFocusInWindow();
    }

    /**
     * Bloquea (o desbloquea) todo lo que no es el formulario.
     */
    private void bloquear(boolean b)
    {
        editando = b;
        for (JComponent c : new JComponent[]
        {
            btnNuevo, btnEditar, btnBaja, btnPdf, btnRegresar,
            txtBuscar, cmbCarrera, cmbMateria, spMin, spMax, btnLimpiar, tabla
        })
        {
            c.setEnabled(!b);
        }
        tabla.setForeground(b ? Estilos.GRIS_TEXTO : Estilos.NEGRO);
        tabla.getTableHeader().setEnabled(!b); // tampoco se puede reordenar
        if (!b)
        {
            Alumno a = seleccionado();
            btnEditar.setEnabled(a != null);
            btnBaja.setEnabled(a != null);
        }
    }

    private void darBajaSeleccionado()
    {
        Alumno a = seleccionado();
        if (a == null || editando)
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

    // ==================================================================
    // UTILIDADES (también las usa PnlAlumno)
    // ==================================================================
    /**
     * Letra normal para datos que el usuario lee o escribe (nombres, correos,
     * tablas). Los títulos, etiquetas y botones usan la fuente de Estilos.
     */
    static final Font TEXTO = new Font("Segoe UI", Font.PLAIN, 15);

    /**
     * Botón de Estilos en tamaño compacto (misma fuente, colores y efecto al
     * pasar el mouse; borde un poco más delgado). Llamar DESPUÉS de
     * Estilos.estilizarBoton / estilizarBotonPrincipal.
     */
    static void compactar(JButton b, int tam)
    {
        b.setFont(Estilos.fuente(Font.PLAIN, tam));
        b.setIconTextGap(8);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.NEGRO, 2),
                BorderFactory.createEmptyBorder(5, 14, 5, 14)));
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
