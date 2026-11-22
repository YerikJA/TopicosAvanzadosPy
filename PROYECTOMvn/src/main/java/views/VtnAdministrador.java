package views;

import controller.*;
import estilos.*;
import exception.*;
import java.awt.*;
import java.text.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import model.*;
import org.kordamp.ikonli.fontawesome5.*;
import org.slf4j.*;
import utils.*;
import validaciones.*;
import views.helpers.*;

/**
 * Ventana principal de administración para el sistema escolar. Centraliza las
 * cuatro operaciones fundamentales (CRUD): - Alta (Create) - Consulta (Read) -
 * Edición (Update) - Baja (Delete)
 *
 * La lógica compleja de filtrado, renderizado y comparación de datos ha sido
 * delegada a sus respectivos servicios auxiliares en views.helpers.
 */
public class VtnAdministrador extends javax.swing.JFrame
{

    private static final Logger log = LoggerFactory.getLogger(VtnAdministrador.class);

    // ==================================================================
    // ESTADO Y CONTROLADORES
    // ==================================================================
    private AlumnoController controllerAdministracion;

    private String noControl;
    private String nombreAlu;
    private String correoAlu;
    private ItemCatalogo carreraAlu;
    private Double promedioAlu;
    private String semestreAlu;
    private Date fechAlu;
    private String contraseniaAlu;
    private List<ItemCatalogo> materiasAlu;

    //edicion
    private Alumno alumnoOriginal;
    private String pasoActual = "buscar";
    private final javax.swing.ButtonGroup grupoPestanasEd = new javax.swing.ButtonGroup();

    //módulo de Consulta
    private final CatalogoController catalogo = new CatalogoController();
    private List<Alumno> todos = new ArrayList<>();
    private final Map<Alumno, List<String>> materiasDe = new HashMap<>();
    private boolean cargando = false;

    private final DefaultListModel<Alumno> modeloLista = new DefaultListModel<>();
    private final JList<Alumno> listaAlumnos = new JList<>(modeloLista);

    public VtnAdministrador(AlumnoController controller)
    {
        initComponents();
        this.controllerAdministracion = controller;
        setTitle("Administracion");

        inicializarEstilosVisuales();
        cargarTabla(); // Carga tabla estática de materias para el alta
        cargarCatalogos();
        iniciarConsulta();
    }

    // INICIALIZACIÓN VISUAL Y CATÁLOGOS
    private void inicializarEstilosVisuales()
    {
        jP_Encabezado.setPreferredSize(new java.awt.Dimension(0, 48));

        // Formato de ventana y títulos
        Estilos.estilizarVentana(this);
        Estilos.estilizarTitulo(jL_TituloGeneral);
        jL_TituloGeneral.setFont(Estilos.fuente(java.awt.Font.PLAIN, 48));
        Estilos.estilizarTitulo(jLabel2);
        jLabel2.setFont(Estilos.fuente(java.awt.Font.PLAIN, 48));

        // Menú principal y pestañas
        Estilos.estilizarMenu(jBttn_Alta, jBttn_Consulta, jBttn_Edicion, jBttn_Baja);
        Estilos.estilizarBoton(jBttn_Regresar);
        Estilos.estilizarPestanas(jBttn_DatosPrincipales, jBttn_MateriasProm, jBttn_CuentasExtr);

        // Fondos
        Estilos.estilizarFondo(jp_Contenedor);
        Estilos.estilizarFondo(jP_Bienvenida);
        jPanel2.setBackground(Estilos.AZUL_OSCURO);
        Estilos.estilizarFondo(jP_Alta);

        // Etiquetas de Alta
        Estilos.estilizarEtiqueta(jL_Nombres1);
        Estilos.estilizarEtiqueta(jL_ApellidoPat);
        Estilos.estilizarEtiqueta(jL_ApellidoMat);
        Estilos.estilizarEtiqueta(jL_Fecha);
        Estilos.estilizarEtiqueta(jL_Materias);
        Estilos.estilizarEtiqueta(jL_Semestre);
        Estilos.estilizarEtiqueta(jL_Promedio);
        Estilos.estilizarEtiqueta(jL_NoControl);
        Estilos.estilizarEtiqueta(jL_Carrera);
        Estilos.estilizarEtiqueta(jLbl_CuentaExtras);
        Estilos.estilizarEtiqueta(jLbl_correo);
        jLbl_CuentaExtras.setFont(Estilos.fuente(java.awt.Font.PLAIN, 24));
        Estilos.estilizarEtiqueta(jLbl_Contra);
        jLbl_Contra.setFont(Estilos.fuente(java.awt.Font.PLAIN, 16));

        // Campos de texto de Alta
        Estilos.estilizarCampo(jTxt_Semestre);
        Estilos.estilizarCampo(jTxt_Promedio);
        Estilos.estilizarCampo(jTxt_ApellidoMat);
        Estilos.estilizarCampo(jTxt_ApellidoPat);
        Estilos.estilizarCampo(jTxt_nombre);
        Estilos.estilizarFecha(jDC_Nacimiento);
        Estilos.estilizarCampo(jTxt_Correo);
        Estilos.estilizarCampo(jTxt_NoControl);
        Estilos.estilizarCampo(jPF_contraseña);

        // Botones de Alta
        Estilos.estilizarBotonPrincipal(jBttn_ContinuarDP);
        Estilos.estilizarBoton(jBttn_Cancelar);
        Estilos.estilizarBotonPrincipal(jBttn_ContinuarMP);
        Estilos.estilizarBoton(jBttn_Cancelar_2);
        Estilos.estilizarBotonPrincipal(jB_Guardar);
        Estilos.estilizarBoton(jB_Cancelar3);

        // Botones de Consulta y Baja
        Estilos.estilizarBotonPrincipal(JBtnBuscarBaja);
        Estilos.estilizarBotonPrincipal(jButtonDarBaja);
        Estilos.estilizarBoton(jButtonCancelarBaja);

        // Combos y scrolls
        Estilos.estilizarCombo(jcbCarrera);
        Estilos.estilizarCombo(jComboBox1_C);
        Estilos.estilizarCombo(jcbMateria);
        Estilos.estilizarCombo(jCB_Carrera);
        Estilos.estilizarScroll(jScrollPane1);

        jP_ConsultaC.setOpaque(true);
        jP_ConsultaC.setBackground(Estilos.CREMA_CLARO);
        jP_CONSULTA_COMP.setOpaque(true);
        jP_CONSULTA_COMP.setBackground(Estilos.CREMA_CLARO);
        jP_CONSULTA_COMP.setBorder(null);

        // Tablas y sombras
        Estilos.estilizarTabla(jTb_Materias);
        Estilos.estilizarPanelConSombra(jP_Bienvenida);
        Estilos.estilizarPanelConSombra(jP_Consulta);
        Estilos.estilizarFondo(jP_Edicion);
        Estilos.estilizarPanelConSombra(jP_Baja);
        Estilos.estilizarPanelConSombra(jPanelFiltros);

        // Distribución de pestañas en panel de Alta
        jP_Alta.add(jP_Encabezado, java.awt.BorderLayout.NORTH);
        jP_Alta.add(jP_AltCard, java.awt.BorderLayout.CENTER);
        jP_Encabezado.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));
        jP_Encabezado.setPreferredSize(new java.awt.Dimension(0, 48));
        Estilos.estilizarFondo(jP_Encabezado);
        Estilos.estilizarPanelConSombra(jP_AltCard);

        for (javax.swing.JPanel p : new javax.swing.JPanel[]
        {
            jP_DatosPrincipales, jP_MateriasProm, jP_CuentasExtr
        })
        {
            p.setOpaque(true);
            p.setBackground(Estilos.CREMA_CLARO);
        }

        java.awt.CardLayout tarjetas = (java.awt.CardLayout) jP_AltCard.getLayout();
        jBttn_DatosPrincipales.addActionListener(e -> tarjetas.show(jP_AltCard, "card2"));
        jBttn_MateriasProm.addActionListener(e -> tarjetas.show(jP_AltCard, "card3"));
        jBttn_CuentasExtr.addActionListener(e -> tarjetas.show(jP_AltCard, "card4"));
        tarjetas.show(jP_AltCard, "card2");
        Estilos.seleccionar(jBttn_DatosPrincipales);

        inicializarIconos();
        jTb_Materias.setDefaultEditor(Object.class, null);
    }

    private void inicializarIconos()
    {
        Estilos.icono(jBttn_Alta, FontAwesomeSolid.USER_PLUS, 22);
        Estilos.icono(jBttn_Consulta, FontAwesomeSolid.SEARCH, 22);
        Estilos.icono(jBttn_Edicion, FontAwesomeSolid.PENCIL_ALT, 22);
        Estilos.icono(jBttn_Baja, FontAwesomeSolid.TRASH, 22);
        Estilos.icono(jBttn_Regresar, FontAwesomeSolid.ARROW_LEFT, 22);

        Estilos.icono(jBttn_DatosPrincipales, FontAwesomeSolid.USER, 18);
        Estilos.icono(jBttn_MateriasProm, FontAwesomeSolid.LIST_ALT, 18);
        Estilos.icono(jBttn_CuentasExtr, FontAwesomeSolid.LOCK, 18);

        Estilos.icono(jBttn_ContinuarDP, FontAwesomeSolid.ARROW_RIGHT, 18);
        Estilos.icono(jBttn_ContinuarMP, FontAwesomeSolid.ARROW_RIGHT, 18);
        Estilos.icono(jBttn_Cancelar, FontAwesomeSolid.TIMES, 18);
        Estilos.icono(jBttn_Cancelar_2, FontAwesomeSolid.TIMES, 18);
        Estilos.icono(jB_Cancelar3, FontAwesomeSolid.TIMES, 18);
        Estilos.icono(jB_Guardar, FontAwesomeSolid.SAVE, 18);

        Estilos.icono(jLbl_CuentaExtras, FontAwesomeSolid.COG, 24);
        Estilos.icono(jLbl_Contra, FontAwesomeSolid.KEY, 16);
        Estilos.icono(jL_Semestre, FontAwesomeSolid.CALENDAR_ALT, 16);
        Estilos.icono(jL_Materias, FontAwesomeSolid.BOOK, 16);
        Estilos.icono(jL_Promedio, FontAwesomeSolid.CHART_LINE, 16);

        Estilos.icono(lblTitBuscar, FontAwesomeSolid.SEARCH, 32);
        Estilos.icono(lblTitMateria, FontAwesomeSolid.GRADUATION_CAP, 16);
        Estilos.icono(jLb_CONSULTA_ORDENAR, FontAwesomeSolid.SORT_AMOUNT_DOWN, 16);
        Estilos.icono(lblTitCarrera, FontAwesomeSolid.BOOK, 16);
        Estilos.icono(lblTitRango, FontAwesomeSolid.CHART_LINE, 16);
        Estilos.icono(lblFControl, FontAwesomeSolid.CHART_LINE, 16);

        Estilos.icono(jlblContador, FontAwesomeSolid.SORT_AMOUNT_DOWN, 16);
        Estilos.icono(lblFNombre, FontAwesomeSolid.BOOK, 16);
        Estilos.icono(lblTitCorreo, FontAwesomeSolid.CHART_LINE, 16);
        Estilos.icono(lblFCorreo, FontAwesomeSolid.SORT_AMOUNT_DOWN, 16);
        Estilos.icono(lblTitPromedio, FontAwesomeSolid.BOOK, 16);
        Estilos.icono(lblFProm, FontAwesomeSolid.CHART_LINE, 16);
        Estilos.icono(jLabel24, FontAwesomeSolid.COG, 24);
    }

    private void cargarCatalogos()
    {
        grupoPestanasEd.add(jTBuscar);
        grupoPestanasEd.add(jTDatos);
        grupoPestanasEd.add(jTConfirmar);
        java.awt.event.ActionListener soloIndicador = e -> irA(pasoActual);
        jTBuscar.addActionListener(soloIndicador);
        jTDatos.addActionListener(soloIndicador);
        jTConfirmar.addActionListener(soloIndicador);

        CatalogoController catalogoEd = new CatalogoController();
        try
        {
            lstMaterias.setListData(EdicionHelper.nombres(catalogoEd.obtenerMaterias()).toArray(new String[0]));
            cmbCarrera.setModel(new javax.swing.DefaultComboBoxModel<>(
                    catalogoEd.obtenerCarreras().toArray(new String[0])));
        } catch (AppException e)
        {
            log.error("Error al cargar los catálogos", e);
            error("Error", e.getMessage());
        }

        btnAgregarMat.setVisible(false);
        btnQuitarMat.setVisible(false);

        btnAtras2.addActionListener(e -> irA("buscar"));
        btnRestablecer.addActionListener(e -> llenarFormularioEdicion(alumnoOriginal));
        btnCancelar2.addActionListener(e -> reiniciarEdicion());

        limpiarResultadoEdicion();
        conScroll(jP_EdBuscar, "buscar");
        conScroll(jP_EdDatos, "datos");
        conScroll(jP_EdConfirmar, "confirmar");
        aplicarEstilosEdicion();
        irA("buscar");
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jPanel2 = new javax.swing.JPanel();
        jL_TituloGeneral = new javax.swing.JLabel();
        jBttn_Edicion = new javax.swing.JButton();
        jBttn_Alta = new javax.swing.JButton();
        jBttn_Consulta = new javax.swing.JButton();
        jBttn_Baja = new javax.swing.JButton();
        jBttn_Regresar = new javax.swing.JButton();
        jp_Contenedor = new javax.swing.JPanel();
        jP_Bienvenida = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jP_Alta = new javax.swing.JPanel();
        jP_Encabezado = new javax.swing.JPanel();
        jBttn_DatosPrincipales = new javax.swing.JButton();
        jBttn_MateriasProm = new javax.swing.JButton();
        jBttn_CuentasExtr = new javax.swing.JButton();
        jP_AltCard = new javax.swing.JPanel();
        jP_DatosPrincipales = new javax.swing.JPanel();
        jL_ApellidoMat = new javax.swing.JLabel();
        jTxt_nombre = new javax.swing.JTextField();
        jL_Nombres1 = new javax.swing.JLabel();
        jL_ApellidoPat = new javax.swing.JLabel();
        jTxt_ApellidoMat = new javax.swing.JTextField();
        jTxt_ApellidoPat = new javax.swing.JTextField();
        jL_Fecha = new javax.swing.JLabel();
        jDC_Nacimiento = new com.toedter.calendar.JDateChooser();
        jBttn_Cancelar = new javax.swing.JButton();
        jBttn_ContinuarDP = new javax.swing.JButton();
        jP_MateriasProm = new javax.swing.JPanel();
        jBttn_ContinuarMP = new javax.swing.JButton();
        jBttn_Cancelar_2 = new javax.swing.JButton();
        jL_Semestre = new javax.swing.JLabel();
        jL_Materias = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTb_Materias = new javax.swing.JTable();
        jL_Promedio = new javax.swing.JLabel();
        jTxt_Semestre = new javax.swing.JTextField();
        jTxt_Promedio = new javax.swing.JTextField();
        jP_CuentasExtr = new javax.swing.JPanel();
        jLbl_CuentaExtras = new javax.swing.JLabel();
        jLbl_Contra = new javax.swing.JLabel();
        jB_Cancelar3 = new javax.swing.JButton();
        jB_Guardar = new javax.swing.JButton();
        jLbl_correo = new javax.swing.JLabel();
        jTxt_Correo = new javax.swing.JTextField();
        jPF_contraseña = new javax.swing.JPasswordField();
        jL_NoControl = new javax.swing.JLabel();
        jTxt_NoControl = new javax.swing.JTextField();
        jCB_Carrera = new javax.swing.JComboBox<>();
        jL_Carrera = new javax.swing.JLabel();
        jP_Consulta = new javax.swing.JPanel();
        jP_ConsultaC = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jP_CONSULTA_COMP = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jPanelFiltros = new javax.swing.JPanel();
        lblTitBuscar = new javax.swing.JLabel();
        jtxtBuscar = new javax.swing.JTextField();
        lblTitCarrera = new javax.swing.JLabel();
        jcbCarrera = new javax.swing.JComboBox<>();
        jLb_CONSULTA_ORDENAR = new javax.swing.JLabel();
        jComboBox1_C = new javax.swing.JComboBox<>();
        lblTitMateria = new javax.swing.JLabel();
        jcbMateria = new javax.swing.JComboBox<>();
        lblTitRango = new javax.swing.JLabel();
        jspMin = new javax.swing.JSpinner();
        jLb_Consulta_A = new javax.swing.JLabel();
        jspMax = new javax.swing.JSpinner();
        jlblContador = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jlistaAlumnos = new javax.swing.JList<>();
        jPanel3 = new javax.swing.JPanel();
        lblFNombre = new javax.swing.JLabel();
        lblFControl = new javax.swing.JLabel();
        lblTitCorreo = new javax.swing.JLabel();
        lblFCorreo = new javax.swing.JLabel();
        lblTitCarrera1 = new javax.swing.JLabel();
        lblFCarrera = new javax.swing.JLabel();
        lblTitPromedio = new javax.swing.JLabel();
        lblFProm = new javax.swing.JLabel();
        jScrollPaneMaterias = new javax.swing.JScrollPane();
        txtFMaterias = new javax.swing.JTextArea();
        btnAyuda = new javax.swing.JButton();
        jbtnLimpiar = new javax.swing.JButton();
        jBttn_Pdf = new javax.swing.JButton();
        jP_Edicion = new javax.swing.JPanel();
        jP_EncabezadoEd = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTBuscar = new javax.swing.JToggleButton();
        jTDatos = new javax.swing.JToggleButton();
        jTConfirmar = new javax.swing.JToggleButton();
        jP_EdCard = new javax.swing.JPanel();
        jP_EdDatos = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txtNoControl2 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtApPat = new javax.swing.JTextField();
        txtApMaterno = new javax.swing.JTextField();
        txtCorreo = new javax.swing.JTextField();
        cmbCarrera = new javax.swing.JComboBox<>();
        txtPromedio = new javax.swing.JTextField();
        dcFechaNac = new com.toedter.calendar.JDateChooser();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstMaterias = new javax.swing.JList<>();
        btnAgregarMat = new javax.swing.JButton();
        btnQuitarMat = new javax.swing.JButton();
        btnAtras2 = new javax.swing.JButton();
        btnRestablecer = new javax.swing.JButton();
        btnCancelar2 = new javax.swing.JButton();
        btnContinuar2 = new javax.swing.JButton();
        jP_EdConfirmar = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        lblResumen = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCambios = new javax.swing.JTable();
        chkConfirmo = new javax.swing.JCheckBox();
        btnAtras3 = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        jP_EdBuscar = new javax.swing.JPanel();
        lblNoControl = new javax.swing.JLabel();
        txtNoControl = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        lblEstado = new javax.swing.JLabel();
        jP_Resultado = new javax.swing.JPanel();
        jLbl_Nombre = new javax.swing.JLabel();
        jLbl_Carrera = new javax.swing.JLabel();
        jLbl_Semestre = new javax.swing.JLabel();
        jLbl_Correo = new javax.swing.JLabel();
        jLbl_Fecha = new javax.swing.JLabel();
        jLbl_Promedio = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jBContinuar1 = new javax.swing.JButton();
        jBCancelar1 = new javax.swing.JButton();
        jP_Baja = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTxtBuscarBaja = new javax.swing.JTextField();
        JBtnBuscarBaja = new javax.swing.JButton();
        jTxtEstatusBusqueda = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jTxtFieldNombre = new javax.swing.JTextField();
        jLblPromedioBaja = new javax.swing.JLabel();
        JLblCarreraBaja = new javax.swing.JLabel();
        JLblSemestreBaja = new javax.swing.JLabel();
        JLblCorreoBaja = new javax.swing.JLabel();
        JLblFeNac = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        jTextSemestreBaja = new javax.swing.JTextField();
        jTxtCarreraBaja = new javax.swing.JTextField();
        jTxtCorreoBaja = new javax.swing.JTextField();
        jTxtFechaNacBaja = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTxtPromedioBaja = new javax.swing.JTextField();
        jButtonCancelarBaja = new javax.swing.JButton();
        jButtonDarBaja = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jL_TituloGeneral.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jL_TituloGeneral.setText("ADMINISTRACION");

        jBttn_Edicion.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jBttn_Edicion.setText("EDICIÓN");
        jBttn_Edicion.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBttn_EdicionActionPerformed(evt);
            }
        });

        jBttn_Alta.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jBttn_Alta.setText("ALTA");
        jBttn_Alta.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBttn_AltaActionPerformed(evt);
            }
        });

        jBttn_Consulta.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jBttn_Consulta.setText("CONSULTA");
        jBttn_Consulta.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBttn_ConsultaActionPerformed(evt);
            }
        });

        jBttn_Baja.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jBttn_Baja.setText("BAJA");
        jBttn_Baja.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBttn_BajaActionPerformed(evt);
            }
        });

        jBttn_Regresar.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jBttn_Regresar.setText("REGRESAR");
        jBttn_Regresar.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBttn_RegresarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jL_TituloGeneral))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jBttn_Alta, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBttn_Consulta, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBttn_Edicion, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBttn_Baja, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBttn_Regresar, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jL_TituloGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBttn_Alta, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(jBttn_Consulta, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addComponent(jBttn_Edicion, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(jBttn_Baja, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(jBttn_Regresar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(194, Short.MAX_VALUE))
        );

        jp_Contenedor.setLayout(new java.awt.CardLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jLabel2.setText("BIENVENIDO");

        javax.swing.GroupLayout jP_BienvenidaLayout = new javax.swing.GroupLayout(jP_Bienvenida);
        jP_Bienvenida.setLayout(jP_BienvenidaLayout);
        jP_BienvenidaLayout.setHorizontalGroup(
            jP_BienvenidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_BienvenidaLayout.createSequentialGroup()
                .addGap(295, 295, 295)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(289, Short.MAX_VALUE))
        );
        jP_BienvenidaLayout.setVerticalGroup(
            jP_BienvenidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_BienvenidaLayout.createSequentialGroup()
                .addGap(229, 229, 229)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(310, Short.MAX_VALUE))
        );

        jp_Contenedor.add(jP_Bienvenida, "card6");

        jP_Alta.setLayout(new java.awt.BorderLayout());

        jBttn_DatosPrincipales.setText("1. DATOS PRINCIPALES");
        jBttn_DatosPrincipales.setEnabled(false);
        jBttn_DatosPrincipales.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBttn_DatosPrincipalesActionPerformed(evt);
            }
        });
        jP_Encabezado.add(jBttn_DatosPrincipales);

        jBttn_MateriasProm.setText("2. MATERIAS Y PROMEDIO");
        jBttn_MateriasProm.setEnabled(false);
        jP_Encabezado.add(jBttn_MateriasProm);

        jBttn_CuentasExtr.setText("3. CUENTA Y EXTRAS");
        jBttn_CuentasExtr.setEnabled(false);
        jP_Encabezado.add(jBttn_CuentasExtr);

        jP_Alta.add(jP_Encabezado, java.awt.BorderLayout.NORTH);

        jP_AltCard.setLayout(new java.awt.CardLayout());

        jL_ApellidoMat.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jL_ApellidoMat.setText("Apellido materno");

        jL_Nombres1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jL_Nombres1.setText("Nombre(s)");

        jL_ApellidoPat.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jL_ApellidoPat.setText("Apellido paterno");

        jL_Fecha.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jL_Fecha.setText("Fecha de nacimiento");

        jDC_Nacimiento.setDateFormatString("dd/MM/yyyy");

        jBttn_Cancelar.setText("Cancelar");

        jBttn_ContinuarDP.setText("Continuar");
        jBttn_ContinuarDP.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBttn_ContinuarDPActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jP_DatosPrincipalesLayout = new javax.swing.GroupLayout(jP_DatosPrincipales);
        jP_DatosPrincipales.setLayout(jP_DatosPrincipalesLayout);
        jP_DatosPrincipalesLayout.setHorizontalGroup(
            jP_DatosPrincipalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_DatosPrincipalesLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jP_DatosPrincipalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jP_DatosPrincipalesLayout.createSequentialGroup()
                        .addGroup(jP_DatosPrincipalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTxt_nombre)
                            .addComponent(jL_Nombres1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jL_Fecha, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDC_Nacimiento, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jP_DatosPrincipalesLayout.createSequentialGroup()
                        .addComponent(jL_ApellidoPat, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(113, 113, 113)
                        .addComponent(jL_ApellidoMat, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                        .addGap(379, 379, 379))
                    .addGroup(jP_DatosPrincipalesLayout.createSequentialGroup()
                        .addComponent(jTxt_ApellidoPat, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTxt_ApellidoMat, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(142, 142, 142)
                        .addComponent(jBttn_Cancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(68, 68, 68))))
            .addGroup(jP_DatosPrincipalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jP_DatosPrincipalesLayout.createSequentialGroup()
                    .addContainerGap(659, Short.MAX_VALUE)
                    .addComponent(jBttn_ContinuarDP, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(72, 72, 72)))
        );
        jP_DatosPrincipalesLayout.setVerticalGroup(
            jP_DatosPrincipalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_DatosPrincipalesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jL_Nombres1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTxt_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jP_DatosPrincipalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jL_ApellidoPat, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jL_ApellidoMat, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jP_DatosPrincipalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTxt_ApellidoPat, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxt_ApellidoMat, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBttn_Cancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jL_Fecha, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDC_Nacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(305, Short.MAX_VALUE))
            .addGroup(jP_DatosPrincipalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jP_DatosPrincipalesLayout.createSequentialGroup()
                    .addGap(91, 91, 91)
                    .addComponent(jBttn_ContinuarDP, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(526, Short.MAX_VALUE)))
        );

        jP_AltCard.add(jP_DatosPrincipales, "card2");

        jBttn_ContinuarMP.setText("Continuar");
        jBttn_ContinuarMP.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBttn_ContinuarMPActionPerformed(evt);
            }
        });

        jBttn_Cancelar_2.setText("Volver");
        jBttn_Cancelar_2.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBttn_Cancelar_2ActionPerformed(evt);
            }
        });

        jL_Semestre.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jL_Semestre.setText("Semestre");

        jL_Materias.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jL_Materias.setText("Materias");

        jTb_Materias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String []
            {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTb_Materias);

        jL_Promedio.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jL_Promedio.setText("Promedio General");

        jTxt_Semestre.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jTxt_Semestre.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jTxt_SemestreActionPerformed(evt);
            }
        });

        jTxt_Promedio.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jTxt_Promedio.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jTxt_PromedioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jP_MateriasPromLayout = new javax.swing.GroupLayout(jP_MateriasProm);
        jP_MateriasProm.setLayout(jP_MateriasPromLayout);
        jP_MateriasPromLayout.setHorizontalGroup(
            jP_MateriasPromLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jP_MateriasPromLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jP_MateriasPromLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jP_MateriasPromLayout.createSequentialGroup()
                        .addComponent(jL_Materias)
                        .addContainerGap())
                    .addGroup(jP_MateriasPromLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                        .addGroup(jP_MateriasPromLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jP_MateriasPromLayout.createSequentialGroup()
                                .addGroup(jP_MateriasPromLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jL_Promedio)
                                    .addGroup(jP_MateriasPromLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jTxt_Promedio, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTxt_Semestre, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jL_Semestre))
                                .addGap(50, 50, 50))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jP_MateriasPromLayout.createSequentialGroup()
                                .addGroup(jP_MateriasPromLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jBttn_Cancelar_2, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jBttn_ContinuarMP, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(105, 105, 105))))))
        );
        jP_MateriasPromLayout.setVerticalGroup(
            jP_MateriasPromLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_MateriasPromLayout.createSequentialGroup()
                .addContainerGap(124, Short.MAX_VALUE)
                .addGroup(jP_MateriasPromLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jL_Materias)
                    .addComponent(jL_Semestre))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jP_MateriasPromLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jP_MateriasPromLayout.createSequentialGroup()
                        .addComponent(jTxt_Semestre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jL_Promedio)
                        .addGap(9, 9, 9)
                        .addComponent(jTxt_Promedio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 175, Short.MAX_VALUE)
                        .addComponent(jBttn_ContinuarMP, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addComponent(jBttn_Cancelar_2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62))
                    .addGroup(jP_MateriasPromLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jP_AltCard.add(jP_MateriasProm, "card3");

        jLbl_CuentaExtras.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLbl_CuentaExtras.setText("Datos de la cuenta:");
        jLbl_CuentaExtras.setToolTipText("");

        jLbl_Contra.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLbl_Contra.setText("Contraseña:");

        jB_Cancelar3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jB_Cancelar3.setText("Volver");
        jB_Cancelar3.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jB_Cancelar3ActionPerformed(evt);
            }
        });

        jB_Guardar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jB_Guardar.setText("Guardar");
        jB_Guardar.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jB_GuardarActionPerformed(evt);
            }
        });

        jLbl_correo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLbl_correo.setText("Correo:");

        jTxt_Correo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jPF_contraseña.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jL_NoControl.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jL_NoControl.setText("No.Control");

        jTxt_NoControl.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jCB_Carrera.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jCB_Carrera.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione Carrera", "Ing. Sistemas", "Tic´s", "Quimica", "Mecatronica", "Electromecanica", "Logistica", "Gestion Ambiental" }));

        jL_Carrera.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jL_Carrera.setText("Carrera");

        javax.swing.GroupLayout jP_CuentasExtrLayout = new javax.swing.GroupLayout(jP_CuentasExtr);
        jP_CuentasExtr.setLayout(jP_CuentasExtrLayout);
        jP_CuentasExtrLayout.setHorizontalGroup(
            jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_CuentasExtrLayout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLbl_CuentaExtras)
                    .addGroup(jP_CuentasExtrLayout.createSequentialGroup()
                        .addGroup(jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jTxt_NoControl, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jL_NoControl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jCB_Carrera, javax.swing.GroupLayout.Alignment.LEADING, 0, 330, Short.MAX_VALUE))
                            .addComponent(jL_Carrera))
                        .addGap(93, 93, 93)
                        .addGroup(jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTxt_Correo, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLbl_correo)
                            .addComponent(jPF_contraseña, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLbl_Contra)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jP_CuentasExtrLayout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(jB_Cancelar3, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jB_Guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(86, Short.MAX_VALUE))
        );
        jP_CuentasExtrLayout.setVerticalGroup(
            jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_CuentasExtrLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLbl_CuentaExtras)
                .addGap(18, 18, 18)
                .addGroup(jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLbl_correo)
                    .addComponent(jL_NoControl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTxt_NoControl, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxt_Correo, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addGroup(jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jL_Carrera)
                    .addComponent(jLbl_Contra))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCB_Carrera, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPF_contraseña, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE))
                .addGap(30, 30, 30)
                .addGroup(jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jB_Guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jB_Cancelar3, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(334, Short.MAX_VALUE))
        );

        jP_AltCard.add(jP_CuentasExtr, "card4");

        jP_Alta.add(jP_AltCard, java.awt.BorderLayout.PAGE_START);

        jp_Contenedor.add(jP_Alta, "card2");

        jP_Consulta.setLayout(new java.awt.BorderLayout());

        jP_ConsultaC.setLayout(new java.awt.CardLayout());

        jP_CONSULTA_COMP.setPreferredSize(new java.awt.Dimension(892, 604));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel24.setText("Consulta");

        lblTitBuscar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTitBuscar.setText("Buscar");

        jtxtBuscar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtBuscar.setText("Ej. Luis Estrada");

        lblTitCarrera.setText("Carrera del alumno");

        jcbCarrera.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLb_CONSULTA_ORDENAR.setText("Ordenar por:");

        jComboBox1_C.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jComboBox1_C.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblTitMateria.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTitMateria.setText("Materia");

        jcbMateria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS", "SOBRESALIENTE", "APROBADO", "EN RIESGO", "REPORBADO" }));

        lblTitRango.setText("Promedio (0 a 100)");

        jLb_Consulta_A.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLb_Consulta_A.setText("A");

        javax.swing.GroupLayout jPanelFiltrosLayout = new javax.swing.GroupLayout(jPanelFiltros);
        jPanelFiltros.setLayout(jPanelFiltrosLayout);
        jPanelFiltrosLayout.setHorizontalGroup(
            jPanelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelFiltrosLayout.createSequentialGroup()
                        .addGroup(jPanelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTitBuscar)
                            .addComponent(lblTitMateria))
                        .addGap(23, 23, 23)
                        .addGroup(jPanelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanelFiltrosLayout.createSequentialGroup()
                                .addGroup(jPanelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTitRango)
                                    .addComponent(lblTitCarrera))
                                .addGap(192, 192, 192))
                            .addGroup(jPanelFiltrosLayout.createSequentialGroup()
                                .addComponent(jcbCarrera, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(29, 29, 29)))
                        .addGroup(jPanelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLb_CONSULTA_ORDENAR, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1_C, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelFiltrosLayout.createSequentialGroup()
                        .addComponent(jcbMateria, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(jspMin, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLb_Consulta_A)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jspMax, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(280, 280, 280)))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanelFiltrosLayout.setVerticalGroup(
            jPanelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelFiltrosLayout.createSequentialGroup()
                        .addGroup(jPanelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTitBuscar)
                            .addComponent(lblTitCarrera)
                            .addComponent(jLb_CONSULTA_ORDENAR))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1_C, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTitMateria))
                    .addGroup(jPanelFiltrosLayout.createSequentialGroup()
                        .addComponent(jcbCarrera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblTitRango)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcbMateria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jspMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLb_Consulta_A)
                    .addComponent(jspMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jlblContador.setText("Mostrando 0 de 0 alumnos");

        jlistaAlumnos.setModel(new javax.swing.AbstractListModel<String>()
        {
            String[] strings = { " " };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(jlistaAlumnos);

        lblFNombre.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblFNombre.setText("Nombre del Alumno");

        lblFControl.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblFControl.setForeground(new java.awt.Color(100, 110, 120));
        lblFControl.setText("No. de control: 00000000");

        lblTitCorreo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblTitCorreo.setForeground(new java.awt.Color(100, 110, 120));
        lblTitCorreo.setText("Correo:");

        lblFCorreo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblFCorreo.setText("correo@ejemplo.com");

        lblTitCarrera1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblTitCarrera1.setForeground(new java.awt.Color(100, 110, 120));
        lblTitCarrera1.setText("Carrera:");

        lblFCarrera.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblFCarrera.setText("Sistemas");

        lblTitPromedio.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblTitPromedio.setForeground(new java.awt.Color(100, 110, 120));
        lblTitPromedio.setText("Promedio:");

        lblFProm.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblFProm.setText("100");

        jScrollPaneMaterias.setBorder(javax.swing.BorderFactory.createTitledBorder("Materias que cursa"));

        txtFMaterias.setEditable(false);
        txtFMaterias.setColumns(20);
        txtFMaterias.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtFMaterias.setLineWrap(true);
        txtFMaterias.setRows(5);
        txtFMaterias.setWrapStyleWord(true);
        jScrollPaneMaterias.setViewportView(txtFMaterias);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneMaterias)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(lblTitCorreo)
                                .addGap(18, 18, 18)
                                .addComponent(lblFCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTitPromedio)
                                .addGap(18, 18, 18)
                                .addComponent(lblFProm, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblFNombre)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(lblFControl)
                                .addGap(107, 107, 107)
                                .addComponent(lblTitCarrera1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblFCarrera, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 29, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFNombre)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFControl)
                    .addComponent(lblTitCarrera1)
                    .addComponent(lblFCarrera))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitCorreo)
                    .addComponent(lblFCorreo)
                    .addComponent(lblTitPromedio)
                    .addComponent(lblFProm))
                .addGap(18, 18, 18)
                .addComponent(jScrollPaneMaterias, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnAyuda.setText("¿Cómo busco?");
        btnAyuda.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnAyudaActionPerformed(evt);
            }
        });

        jbtnLimpiar.setText("Limpiar filtros");

        jBttn_Pdf.setText("PDF");
        jBttn_Pdf.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBttn_PdfActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jP_CONSULTA_COMPLayout = new javax.swing.GroupLayout(jP_CONSULTA_COMP);
        jP_CONSULTA_COMP.setLayout(jP_CONSULTA_COMPLayout);
        jP_CONSULTA_COMPLayout.setHorizontalGroup(
            jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_CONSULTA_COMPLayout.createSequentialGroup()
                .addGroup(jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jP_CONSULTA_COMPLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblContador, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(32, 32, 32)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 10, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jP_CONSULTA_COMPLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanelFiltros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jP_CONSULTA_COMPLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBttn_Pdf, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAyuda)
                        .addGap(18, 18, 18)
                        .addComponent(jbtnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(235, 235, 235)))
                .addContainerGap())
        );
        jP_CONSULTA_COMPLayout.setVerticalGroup(
            jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_CONSULTA_COMPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jP_CONSULTA_COMPLayout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addGap(8, 8, 8))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jP_CONSULTA_COMPLayout.createSequentialGroup()
                        .addGroup(jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAyuda)
                            .addComponent(jbtnLimpiar)
                            .addComponent(jBttn_Pdf))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jPanelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jP_CONSULTA_COMPLayout.createSequentialGroup()
                        .addComponent(jlblContador)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jP_CONSULTA_COMP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jP_CONSULTA_COMP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jP_ConsultaC.add(jPanel1, "card2");

        jP_Consulta.add(jP_ConsultaC, java.awt.BorderLayout.CENTER);

        jp_Contenedor.add(jP_Consulta, "card3");

        jP_Edicion.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel1.setText("EDICION");

        jTBuscar.setText("1.Buscar");
        jTBuscar.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jTBuscarActionPerformed(evt);
            }
        });

        jTDatos.setText("2.Datos del alumno");

        jTConfirmar.setText("3.Confirmar");

        javax.swing.GroupLayout jP_EncabezadoEdLayout = new javax.swing.GroupLayout(jP_EncabezadoEd);
        jP_EncabezadoEd.setLayout(jP_EncabezadoEdLayout);
        jP_EncabezadoEdLayout.setHorizontalGroup(
            jP_EncabezadoEdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_EncabezadoEdLayout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jTBuscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTDatos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTConfirmar)
                .addContainerGap(243, Short.MAX_VALUE))
        );
        jP_EncabezadoEdLayout.setVerticalGroup(
            jP_EncabezadoEdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_EncabezadoEdLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel1)
                .addContainerGap(13, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jP_EncabezadoEdLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jP_EncabezadoEdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTBuscar)
                    .addComponent(jTDatos)
                    .addComponent(jTConfirmar))
                .addContainerGap())
        );

        jP_Edicion.add(jP_EncabezadoEd, java.awt.BorderLayout.PAGE_START);

        jP_EdCard.setLayout(new java.awt.CardLayout());

        jP_EdDatos.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setText("Num.Ctrl");
        jP_EdDatos.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, -1, -1));

        txtNoControl2.setEditable(false);
        txtNoControl2.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                txtNoControl2ActionPerformed(evt);
            }
        });
        jP_EdDatos.add(txtNoControl2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 60, 280, -1));

        jLabel15.setText("Nombre");
        jP_EdDatos.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 110, -1, -1));

        jLabel16.setText("Apellido paterno");
        jP_EdDatos.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 160, -1, -1));

        jLabel17.setText("Apellido materno");
        jP_EdDatos.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 210, -1, -1));

        jLabel18.setText("Correo");
        jP_EdDatos.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 250, -1, -1));

        jLabel19.setText("Carrera");
        jP_EdDatos.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 300, -1, -1));

        jLabel20.setText("Promedio");
        jP_EdDatos.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 350, -1, -1));

        jLabel21.setText("Fecha nac.");
        jP_EdDatos.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 400, -1, -1));

        txtNombre.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                txtNombreActionPerformed(evt);
            }
        });
        jP_EdDatos.add(txtNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 110, 280, -1));
        jP_EdDatos.add(txtApPat, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 160, 280, -1));
        jP_EdDatos.add(txtApMaterno, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 210, 280, -1));

        txtCorreo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                txtCorreoActionPerformed(evt);
            }
        });
        jP_EdDatos.add(txtCorreo, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 250, 280, -1));

        cmbCarrera.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione Carrera", "Ing. Sistemas", "Tic´s", "Quimica", "Mecatronica", "Electromecanica", "Logistica", "Gestion Ambiental" }));
        jP_EdDatos.add(cmbCarrera, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 300, 280, -1));
        jP_EdDatos.add(txtPromedio, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 350, 280, -1));
        jP_EdDatos.add(dcFechaNac, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 400, 280, -1));

        jLabel22.setText("Materias");
        jP_EdDatos.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 450, -1, -1));

        lstMaterias.setModel(new javax.swing.AbstractListModel<String>()
        {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(lstMaterias);

        jP_EdDatos.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 480, 470, 100));

        btnAgregarMat.setText("Agregar Materia");
        jP_EdDatos.add(btnAgregarMat, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 450, -1, -1));

        btnQuitarMat.setText("Quitar Materia");
        jP_EdDatos.add(btnQuitarMat, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 450, -1, -1));

        btnAtras2.setText("Atras");
        jP_EdDatos.add(btnAtras2, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 590, -1, -1));

        btnRestablecer.setText("Restablecer");
        jP_EdDatos.add(btnRestablecer, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 590, -1, -1));

        btnCancelar2.setText("Cancelar");
        jP_EdDatos.add(btnCancelar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 590, -1, -1));

        btnContinuar2.setText("Continuar");
        btnContinuar2.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnContinuar2ActionPerformed(evt);
            }
        });
        jP_EdDatos.add(btnContinuar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 590, -1, -1));

        jP_EdCard.add(jP_EdDatos, "datos");

        jLabel23.setText("Confirmar cambios");

        lblResumen.setText("Resumen del tramite");

        tblCambios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String []
            {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tblCambios);

        chkConfirmo.setText("Revise los cambios y deseo guardarlos");
        chkConfirmo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                chkConfirmoActionPerformed(evt);
            }
        });

        btnAtras3.setText("Atras");
        btnAtras3.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnAtras3ActionPerformed(evt);
            }
        });

        btnGuardar.setText("Guardar cambios");
        btnGuardar.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnGuardarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jP_EdConfirmarLayout = new javax.swing.GroupLayout(jP_EdConfirmar);
        jP_EdConfirmar.setLayout(jP_EdConfirmarLayout);
        jP_EdConfirmarLayout.setHorizontalGroup(
            jP_EdConfirmarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_EdConfirmarLayout.createSequentialGroup()
                .addGroup(jP_EdConfirmarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jP_EdConfirmarLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addGroup(jP_EdConfirmarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jP_EdConfirmarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblResumen, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                                .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 565, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jP_EdConfirmarLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jP_EdConfirmarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jP_EdConfirmarLayout.createSequentialGroup()
                                .addComponent(btnAtras3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnGuardar))
                            .addComponent(chkConfirmo, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap(282, Short.MAX_VALUE))
        );
        jP_EdConfirmarLayout.setVerticalGroup(
            jP_EdConfirmarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_EdConfirmarLayout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblResumen)
                .addGap(43, 43, 43)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chkConfirmo)
                .addGap(18, 18, 18)
                .addGroup(jP_EdConfirmarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGuardar)
                    .addComponent(btnAtras3))
                .addGap(90, 90, 90))
        );

        jP_EdCard.add(jP_EdConfirmar, "confirmar");

        jP_EdBuscar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblNoControl.setText("No. de control");
        jP_EdBuscar.add(lblNoControl, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, -1, -1));

        txtNoControl.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                txtNoControlActionPerformed(evt);
            }
        });
        jP_EdBuscar.add(txtNoControl, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 110, 240, 20));

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnBuscarActionPerformed(evt);
            }
        });
        jP_EdBuscar.add(btnBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 110, -1, -1));

        lblEstado.setText("Resultado de la busqueda");
        jP_EdBuscar.add(lblEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 140, 150, -1));

        jLbl_Nombre.setText("jLabel2");

        jLbl_Carrera.setText("jLabel3");

        jLbl_Semestre.setText("jLabel4");

        jLbl_Correo.setText("jLabel5");

        jLbl_Fecha.setText("jLabel6");

        jLbl_Promedio.setText("jLabel7");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Promedio");

        javax.swing.GroupLayout jP_ResultadoLayout = new javax.swing.GroupLayout(jP_Resultado);
        jP_Resultado.setLayout(jP_ResultadoLayout);
        jP_ResultadoLayout.setHorizontalGroup(
            jP_ResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_ResultadoLayout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(jP_ResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLbl_Nombre, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                    .addComponent(jLbl_Correo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLbl_Carrera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jP_ResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLbl_Fecha)
                    .addGroup(jP_ResultadoLayout.createSequentialGroup()
                        .addComponent(jLbl_Semestre, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(89, 89, 89)
                        .addComponent(jLbl_Promedio, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(17, 17, 17))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jP_ResultadoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(43, 43, 43))
        );
        jP_ResultadoLayout.setVerticalGroup(
            jP_ResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_ResultadoLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel5)
                .addGap(3, 3, 3)
                .addComponent(jLbl_Nombre)
                .addGap(18, 18, 18)
                .addGroup(jP_ResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLbl_Carrera)
                    .addComponent(jLbl_Semestre)
                    .addComponent(jLbl_Promedio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jP_ResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLbl_Correo)
                    .addComponent(jLbl_Fecha))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jP_EdBuscar.add(jP_Resultado, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 540, 130));

        jBContinuar1.setText("Continuar");
        jBContinuar1.setEnabled(false);
        jBContinuar1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBContinuar1ActionPerformed(evt);
            }
        });
        jP_EdBuscar.add(jBContinuar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 320, -1, 20));

        jBCancelar1.setText("Cancelar");
        jBCancelar1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBCancelar1ActionPerformed(evt);
            }
        });
        jP_EdBuscar.add(jBCancelar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 320, -1, -1));

        jP_EdCard.add(jP_EdBuscar, "buscar");

        jP_Edicion.add(jP_EdCard, java.awt.BorderLayout.CENTER);

        jp_Contenedor.add(jP_Edicion, "card4");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(27, 21, 17));
        jLabel3.setText("BAJA DE ALUMNO");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(23, 100, 165));
        jLabel4.setText("No. De Control");

        jTxtBuscarBaja.setBackground(new java.awt.Color(255, 252, 243));
        jTxtBuscarBaja.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jTxtBuscarBaja.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        JBtnBuscarBaja.setBackground(new java.awt.Color(255, 100, 24));
        JBtnBuscarBaja.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        JBtnBuscarBaja.setText("BUSCAR");
        JBtnBuscarBaja.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));
        JBtnBuscarBaja.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                JBtnBuscarBajaActionPerformed(evt);
            }
        });

        jTxtEstatusBusqueda.setBackground(new java.awt.Color(44, 160, 237));
        jTxtEstatusBusqueda.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jTxtEstatusBusqueda.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        jTxtFieldNombre.setBackground(new java.awt.Color(255, 252, 243));
        jTxtFieldNombre.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTxtFieldNombre.setBorder(null);
        jTxtFieldNombre.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jTxtFieldNombreActionPerformed(evt);
            }
        });

        jLblPromedioBaja.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLblPromedioBaja.setForeground(new java.awt.Color(23, 100, 165));
        jLblPromedioBaja.setText("PROMEDIO");

        JLblCarreraBaja.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        JLblCarreraBaja.setForeground(new java.awt.Color(23, 100, 165));
        JLblCarreraBaja.setText("CARRERA");

        JLblSemestreBaja.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        JLblSemestreBaja.setForeground(new java.awt.Color(23, 100, 165));
        JLblSemestreBaja.setText("SEMESTRE");

        JLblCorreoBaja.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        JLblCorreoBaja.setForeground(new java.awt.Color(23, 100, 165));
        JLblCorreoBaja.setText("CORREO");

        JLblFeNac.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        JLblFeNac.setForeground(new java.awt.Color(23, 100, 165));
        JLblFeNac.setText("FECHA DE NACIMIENTO");

        jSeparator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jSeparator2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jSeparator4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jSeparator5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextSemestreBaja.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jTxtCarreraBaja.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N

        jTxtCorreoBaja.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jTxtFechaNacBaja.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(23, 100, 165));
        jLabel11.setText("NOMBRE");

        jTxtPromedioBaja.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N

        jButtonCancelarBaja.setBackground(new java.awt.Color(239, 232, 217));
        jButtonCancelarBaja.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonCancelarBaja.setText("CANCELAR");
        jButtonCancelarBaja.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));
        jButtonCancelarBaja.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonCancelarBajaActionPerformed(evt);
            }
        });

        jButtonDarBaja.setBackground(new java.awt.Color(255, 100, 24));
        jButtonDarBaja.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonDarBaja.setText("DAR BAJA");
        jButtonDarBaja.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));
        jButtonDarBaja.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonDarBajaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator5)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTxtFieldNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(JLblFeNac)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                        .addComponent(jTxtFechaNacBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addComponent(JLblSemestreBaja)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextSemestreBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addComponent(JLblCorreoBaja)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTxtCorreoBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addComponent(JLblCarreraBaja)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTxtCarreraBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLblPromedioBaja)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jTxtPromedioBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(29, 29, 29))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonCancelarBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonDarBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jLblPromedioBaja)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTxtPromedioBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTxtFieldNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(JLblCarreraBaja)
                            .addComponent(jTxtCarreraBaja))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(JLblSemestreBaja)
                            .addComponent(jTextSemestreBaja))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(JLblCorreoBaja)
                            .addComponent(jTxtCorreoBaja))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JLblFeNac)
                    .addComponent(jTxtFechaNacBaja))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancelarBaja)
                    .addComponent(jButtonDarBaja))
                .addGap(22, 22, 22))
        );

        javax.swing.GroupLayout jP_BajaLayout = new javax.swing.GroupLayout(jP_Baja);
        jP_Baja.setLayout(jP_BajaLayout);
        jP_BajaLayout.setHorizontalGroup(
            jP_BajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_BajaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jP_BajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jP_BajaLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jP_BajaLayout.createSequentialGroup()
                        .addGroup(jP_BajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jP_BajaLayout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTxtBuscarBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(JBtnBuscarBaja)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTxtEstatusBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 73, Short.MAX_VALUE))))
        );
        jP_BajaLayout.setVerticalGroup(
            jP_BajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_BajaLayout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jP_BajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTxtBuscarBaja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(JBtnBuscarBaja)
                    .addComponent(jTxtEstatusBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(187, Short.MAX_VALUE))
        );

        jp_Contenedor.add(jP_Baja, "card5");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64)
                .addComponent(jp_Contenedor, javax.swing.GroupLayout.PREFERRED_SIZE, 892, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(75, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addComponent(jp_Contenedor, javax.swing.GroupLayout.PREFERRED_SIZE, 616, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // MANEJADORES DE EVENTOS DE NAVEGACIÓN Y MENÚ

    private void jBttn_RegresarActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jBttn_RegresarActionPerformed
    {//GEN-HEADEREND:event_jBttn_RegresarActionPerformed
        new VtnHome(controllerAdministracion).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jBttn_RegresarActionPerformed

    private void jBttn_DatosPrincipalesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jBttn_DatosPrincipalesActionPerformed
    {//GEN-HEADEREND:event_jBttn_DatosPrincipalesActionPerformed
    }//GEN-LAST:event_jBttn_DatosPrincipalesActionPerformed

    private void jBttn_AltaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jBttn_AltaActionPerformed
    {//GEN-HEADEREND:event_jBttn_AltaActionPerformed
        java.awt.CardLayout cl = (java.awt.CardLayout) jp_Contenedor.getLayout();
        cl.show(jp_Contenedor, "card2");
        Estilos.seleccionar(jBttn_Alta);
    }//GEN-LAST:event_jBttn_AltaActionPerformed

    private void jBttn_ConsultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBttn_ConsultaActionPerformed
        iniciarConsulta();
        java.awt.CardLayout cl = (java.awt.CardLayout) jp_Contenedor.getLayout();
        cl.show(jp_Contenedor, "card3");
        Estilos.seleccionar(jBttn_Consulta);
    }//GEN-LAST:event_jBttn_ConsultaActionPerformed

    private void jBttn_EdicionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBttn_EdicionActionPerformed
        java.awt.CardLayout cl = (java.awt.CardLayout) jp_Contenedor.getLayout();
        cl.show(jp_Contenedor, "card4");
        Estilos.seleccionar(jBttn_Edicion);
    }//GEN-LAST:event_jBttn_EdicionActionPerformed

    private void jBttn_BajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBttn_BajaActionPerformed
        java.awt.CardLayout cl = (java.awt.CardLayout) jp_Contenedor.getLayout();
        cl.show(jp_Contenedor, "card5");
        Estilos.seleccionar(jBttn_Baja);
    }//GEN-LAST:event_jBttn_BajaActionPerformed

    // MANEJADORES DE EVENTOS: ALTA (CREATE)
    private void jBttn_ContinuarDPActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jBttn_ContinuarDPActionPerformed
    {//GEN-HEADEREND:event_jBttn_ContinuarDPActionPerformed
        avanzarPasoDatosPrincipales();
    }//GEN-LAST:event_jBttn_ContinuarDPActionPerformed

    private void jBttn_ContinuarMPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBttn_ContinuarMPActionPerformed
        avanzarPasoMaterias();
    }//GEN-LAST:event_jBttn_ContinuarMPActionPerformed

    private void jB_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_GuardarActionPerformed
        guardarNuevoAlumno();
    }//GEN-LAST:event_jB_GuardarActionPerformed

    private void jBttn_Cancelar_2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jBttn_Cancelar_2ActionPerformed
    {//GEN-HEADEREND:event_jBttn_Cancelar_2ActionPerformed
        java.awt.CardLayout cl = (java.awt.CardLayout) jP_AltCard.getLayout();
        cl.show(jP_AltCard, "card2");
        Estilos.seleccionar(jBttn_DatosPrincipales);
    }//GEN-LAST:event_jBttn_Cancelar_2ActionPerformed

    private void jB_Cancelar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_Cancelar3ActionPerformed
        java.awt.CardLayout cExtra = (java.awt.CardLayout) jP_AltCard.getLayout();
        cExtra.previous(jP_AltCard);
    }//GEN-LAST:event_jB_Cancelar3ActionPerformed

    private void jTxt_SemestreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTxt_SemestreActionPerformed
    }//GEN-LAST:event_jTxt_SemestreActionPerformed

    private void jTxt_PromedioActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jTxt_PromedioActionPerformed
    {//GEN-HEADEREND:event_jTxt_PromedioActionPerformed
    }//GEN-LAST:event_jTxt_PromedioActionPerformed

    // MANEJADORES DE EVENTOS: BAJA (DELETE)
    private void JBtnBuscarBajaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_JBtnBuscarBajaActionPerformed
    {//GEN-HEADEREND:event_JBtnBuscarBajaActionPerformed
        buscarAlumnoParaBaja();
    }//GEN-LAST:event_JBtnBuscarBajaActionPerformed

    private void jButtonDarBajaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonDarBajaActionPerformed
    {//GEN-HEADEREND:event_jButtonDarBajaActionPerformed
        confirmarBajaAlumno();
    }//GEN-LAST:event_jButtonDarBajaActionPerformed

    private void jButtonCancelarBajaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonCancelarBajaActionPerformed
    {//GEN-HEADEREND:event_jButtonCancelarBajaActionPerformed
        limpiarBaja();
    }//GEN-LAST:event_jButtonCancelarBajaActionPerformed

    private void jTxtFieldNombreActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jTxtFieldNombreActionPerformed
    {//GEN-HEADEREND:event_jTxtFieldNombreActionPerformed
    }//GEN-LAST:event_jTxtFieldNombreActionPerformed

    // MANEJADORES DE EVENTOS: EDICIÓN (UPDATE)
    private void jTBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTBuscarActionPerformed
    }//GEN-LAST:event_jTBuscarActionPerformed

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtCorreoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCorreoActionPerformed
    }//GEN-LAST:event_txtCorreoActionPerformed

    private void txtNoControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNoControlActionPerformed
        btnBuscar.doClick();
    }//GEN-LAST:event_txtNoControlActionPerformed

    private void txtNoControl2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNoControl2ActionPerformed
    }//GEN-LAST:event_txtNoControl2ActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        buscarAlumnoParaEdicion();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void jBContinuar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBContinuar1ActionPerformed
        llenarFormularioEdicion(alumnoOriginal);
        irA("datos");
    }//GEN-LAST:event_jBContinuar1ActionPerformed

    private void jBCancelar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelar1ActionPerformed
        reiniciarEdicion();
    }//GEN-LAST:event_jBCancelar1ActionPerformed

    private void btnContinuar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinuar2ActionPerformed
        avanzarPasoEdicionConfirmacion();
    }//GEN-LAST:event_btnContinuar2ActionPerformed

    private void btnAtras3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtras3ActionPerformed
        irA("datos");
    }//GEN-LAST:event_btnAtras3ActionPerformed

    private void chkConfirmoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkConfirmoActionPerformed
        btnGuardar.setEnabled(chkConfirmo.isSelected() && tblCambios.getRowCount() > 0);
    }//GEN-LAST:event_chkConfirmoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardarCambiosEdicion();
    }//GEN-LAST:event_btnGuardarActionPerformed

    // MANEJADORES DE EVENTOS: CONSULTA Y REPORTES (READ)
    private void btnAyudaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnAyudaActionPerformed
    {//GEN-HEADEREND:event_btnAyudaActionPerformed
        mostrarDialogoAyuda();
    }//GEN-LAST:event_btnAyudaActionPerformed

    private void jBttn_PdfActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jBttn_PdfActionPerformed
    {//GEN-HEADEREND:event_jBttn_PdfActionPerformed
        generarReportePdf();
    }//GEN-LAST:event_jBttn_PdfActionPerformed

    // LÓGICA DE ALTA (CREATE)
    private void avanzarPasoDatosPrincipales()
    {
        String nombre = jTxt_nombre.getText().trim();
        String apellidoPat = jTxt_ApellidoPat.getText().trim();
        String apellidoMat = jTxt_ApellidoMat.getText().trim();
        Date fechaNacimiento = jDC_Nacimiento.getDate();

        if (ValidadorAlumno.esVacio(nombre) || !ValidadorAlumno.nombreValido(nombre))
        {
            error("Error de campo", "Ingrese un nombre válido (solo letras y espacios).");
            return;
        }
        if (ValidadorAlumno.esVacio(apellidoPat) || !ValidadorAlumno.nombreValido(apellidoPat))
        {
            error("Error de campo", "El apellido paterno es obligatorio y debe ser válido.");
            return;
        }
        if (ValidadorAlumno.esVacio(apellidoMat) || !ValidadorAlumno.nombreValido(apellidoMat))
        {
            error("Error de campo", "El apellido materno es obligatorio y debe ser válido.");
            return;
        }
        if (ValidadorAlumno.fechaVacia(fechaNacimiento))
        {
            error("Error de campo", "Debe seleccionar una fecha de nacimiento.");
            return;
        }
        if (ValidadorAlumno.fechaFutura(fechaNacimiento) || ValidadorAlumno.fechaPasada(fechaNacimiento))
        {
            error("Error de campo", "La fecha de nacimiento no puede ser futura ni anterior a 1950.");
            return;
        }

        nombreAlu = EdicionHelper.armarNombreCompleto(nombre, apellidoPat, apellidoMat);
        fechAlu = fechaNacimiento;

        java.awt.CardLayout cl = (java.awt.CardLayout) jP_AltCard.getLayout();
        cl.show(jP_AltCard, "card3");
        Estilos.seleccionar(jBttn_MateriasProm);
    }

    private void avanzarPasoMaterias()
    {
        int[] indice = jTb_Materias.getSelectedRows();
        String semestre = jTxt_Semestre.getText().trim();
        String promedio = jTxt_Promedio.getText().trim();

        if (ValidadorAlumno.esVacio(semestre) || !ValidadorAlumno.validarNumericos(semestre))
        {
            error("Error de campo", "El semestre es obligatorio y debe ser numérico.");
            return;
        }
        if (ValidadorAlumno.esVacio(promedio))
        {
            error("Error de campo", "El promedio no puede estar vacío.");
            return;
        }
        Double promedioValido = ValidadorAlumno.promedioValido(promedio);
        if (promedioValido == null)
        {
            error("Error de campo", "Promedio fuera de rango. Ingrese un número entre 0 y 100.");
            return;
        }

        List<ItemCatalogo> materias = new ArrayList<>();
        for (int i = 0; i < indice.length; i++)
        {
            materias.add(new ItemCatalogo(0, String.valueOf(jTb_Materias.getValueAt(indice[i], 0))));
        }

        materiasAlu = materias;
        semestreAlu = semestre;
        promedioAlu = promedioValido;

        java.awt.CardLayout cl = (java.awt.CardLayout) jP_AltCard.getLayout();
        cl.show(jP_AltCard, "card4");
        Estilos.seleccionar(jBttn_CuentasExtr);
    }

    private void guardarNuevoAlumno()
    {
        String numeroControl = jTxt_NoControl.getText().trim();
        String correo = jTxt_Correo.getText().trim();
        String contrasenia = new String(jPF_contraseña.getPassword());
        String carrera = jCB_Carrera.getSelectedItem() != null ? jCB_Carrera.getSelectedItem().toString() : "";

        if (ValidadorAlumno.esVacio(numeroControl) || !ValidadorAlumno.validarNumericos(numeroControl))
        {
            error("Error en el campo", "El No. de Control es obligatorio y solo puede contener números.");
            return;
        }
        if (ValidadorAlumno.seleccionCb(jCB_Carrera.getSelectedIndex()))
        {
            error("Campo requerido", "Seleccione una carrera.");
            return;
        }
        if (ValidadorAlumno.esVacio(correo) || !ValidadorAlumno.correoValido(correo))
        {
            error("Error de validación", "El formato del correo electrónico no es válido. Ejemplo: usuario@dominio.com");
            jTxt_Correo.requestFocus();
            return;
        }
        if (ValidadorAlumno.esVacio(contrasenia))
        {
            error("Campo requerido", "La contraseña no puede estar vacía.");
            return;
        }

        noControl = numeroControl;
        contraseniaAlu = contrasenia;
        correoAlu = correo;
        carreraAlu = new ItemCatalogo(0, carrera);

        Alumno nuevoAlumno = new Alumno(noControl, nombreAlu, correo, carreraAlu, promedioAlu, fechAlu, materiasAlu);
        boolean altaEstudiante = controllerAdministracion.agergarAlumno(nuevoAlumno);

        if (!altaEstudiante)
        {
            JOptionPane.showMessageDialog(this, "El No. de Control ya está registrado.", "Duplicado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Alumno guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        log.info("Alumno {} registrado con No. Control {}", nombreAlu, numeroControl);

        limpiarRegistroAlta();
        java.awt.CardLayout cl = (java.awt.CardLayout) jP_AltCard.getLayout();
        cl.show(jP_AltCard, "card2");
        Estilos.seleccionar(jBttn_DatosPrincipales);
    }

    private void limpiarRegistroAlta()
    {
        jTxt_nombre.setText("");
        jTxt_ApellidoPat.setText("");
        jTxt_ApellidoMat.setText("");
        jDC_Nacimiento.setDate(null);
        jTxt_Semestre.setText("");
        jTxt_Promedio.setText("");
        jTxt_Correo.setText("");
        jPF_contraseña.setText("");
        jTb_Materias.clearSelection();
        jTxt_NoControl.setText("");
        jCB_Carrera.setSelectedIndex(0);

        nombreAlu = "";
        noControl = "";
        correoAlu = "";
        carreraAlu = null;
        promedioAlu = null;
        fechAlu = null;
        materiasAlu = null;
    }

    public void cargarTabla()
    {
        String[] columnas =
        {
            "Materia", "Horario", "Agregar Mat."
        };
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        modelo.addRow(new String[]
        {
            "Top. Avan. Prog.", "Mar/Jue 7 am - 9 am", "Agregar"
        });
        modelo.addRow(new String[]
        {
            "Simulacion", "Mar/Jue 9 am - 11 am", "Agregar"
        });
        modelo.addRow(new String[]
        {
            "Metodos Numericos", "Mie/Vie 1 pm - 3 pm", "Agregar"
        });
        modelo.addRow(new String[]
        {
            "Prolog", "Mie/Vie 11 am - 1 pm", "Agregar"
        });
        modelo.addRow(new String[]
        {
            "Arq.Computadoras.", "Lun/Mie/Vie 7 am - 9 am", "Agregar"
        });

        jTb_Materias.getTableHeader().setReorderingAllowed(false);
        jTb_Materias.getTableHeader().setResizingAllowed(false);
        jTb_Materias.setModel(modelo);
    }

    // LÓGICA DE BAJA (DELETE)
    private void buscarAlumnoParaBaja()
    {
        String nc = jTxtBuscarBaja.getText().trim();
        if (nc.isEmpty())
        {
            limpiarBaja();
            jTxtEstatusBusqueda.setText("Escribe un número de control");
            return;
        }
        if (!nc.matches("\\d+"))
        {
            limpiarBaja();
            alumnoOriginal = null;
            jTxtEstatusBusqueda.setText("X Alumno no encontrado");
            return;
        }

        Alumno a = controllerAdministracion.buscarAlumnoPorNoControl(nc);
        if (a == null)
        {
            limpiarBaja();
            alumnoOriginal = null;
            jTxtEstatusBusqueda.setText("X Alumno no encontrado");
            return;
        }

        alumnoOriginal = a;
        jTxtEstatusBusqueda.setText("✓ Alumno encontrado");
        jTxtFieldNombre.setText(a.getNombre());
        jTxtCarreraBaja.setText(ConsultaFiltroHelper.texto(a.getCarerra()));
        jTxtCorreoBaja.setText(a.getCorreo());
        jTxtPromedioBaja.setText(String.valueOf(a.getPromedio()));
        jTextSemestreBaja.setText(semestreAlu != null ? semestreAlu : "");
        jTxtFechaNacBaja.setText(a.getFechaNaciomiento() != null
                ? new SimpleDateFormat("dd/MM/yyyy").format(a.getFechaNaciomiento()) : "");
    }

    private void confirmarBajaAlumno()
    {
        if (alumnoOriginal == null)
        {
            error("Aviso", "Primero busque un alumno para dar de baja.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que deseas dar de baja al alumno?",
                "Confirmar baja de alumno",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION)
        {
            controllerAdministracion.darBajaAlumno(alumnoOriginal);
            log.info("Alumno con número de control {} fue dado de baja correctamente", alumnoOriginal.getNo_control());
            limpiarBaja();
            alumnoOriginal = null;
            JOptionPane.showMessageDialog(this, "Alumno dado de baja exitosamente.", "Baja exitosa", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void limpiarBaja()
    {
        jTxtCarreraBaja.setEditable(false);
        jTextSemestreBaja.setEditable(false);
        jTxtCorreoBaja.setEditable(false);
        jTxtFechaNacBaja.setEditable(false);
        jTxtPromedioBaja.setEditable(false);
        jTxtFieldNombre.setEditable(false);

        jTxtCarreraBaja.setText("");
        jTextSemestreBaja.setText("");
        jTxtCorreoBaja.setText("");
        jTxtFechaNacBaja.setText("");
        jTxtPromedioBaja.setText("");
        jTxtFieldNombre.setText("");
        jTxtBuscarBaja.setText("");
        jTxtEstatusBusqueda.setText("");
    }

    // LÓGICA DE EDICIÓN (UPDATE)
    private void buscarAlumnoParaEdicion()
    {
        String nc = txtNoControl.getText().trim();
        if (nc.isEmpty())
        {
            lblEstado.setText("Escribe un número de control");
            return;
        }
        if (!nc.matches("\\d+"))
        {
            limpiarResultadoEdicion();
            alumnoOriginal = null;
            jBContinuar1.setEnabled(false);
            lblEstado.setText("El No. de control solo debe tener números, sin espacios ni símbolos");
            return;
        }

        Alumno a = controllerAdministracion.buscarAlumnoPorNoControl(nc);
        if (a == null)
        {
            limpiarResultadoEdicion();
            alumnoOriginal = null;
            jBContinuar1.setEnabled(false);
            lblEstado.setText("Alumno no encontrado");
            return;
        }

        alumnoOriginal = a;
        lblEstado.setText("✓ Alumno encontrado");
        jLbl_Nombre.setText(a.getNombre());
        jLbl_Carrera.setText(ConsultaFiltroHelper.texto(a.getCarerra()));
        jLbl_Correo.setText(a.getCorreo());
        jLbl_Promedio.setText(String.valueOf(a.getPromedio()));
        jLbl_Fecha.setText(a.getFechaNaciomiento() != null
                ? new SimpleDateFormat("dd/MM/yyyy").format(a.getFechaNaciomiento()) : "");
        jP_Resultado.setVisible(true);
        jBContinuar1.setEnabled(true);
    }

    private void avanzarPasoEdicionConfirmacion()
    {
        if (!validarFormularioEdicion())
        {
            return;
        }
        cargarTablaCambios();
        irA("confirmar");
    }

    private void guardarCambiosEdicion()
    {
        if (alumnoOriginal == null)
        {
            error("Aviso", "No hay alumno seleccionado para editar.");
            return;
        }

        String nombreCompleto = EdicionHelper.armarNombreCompleto(
                txtNombre.getText().trim(),
                txtApPat.getText().trim(),
                txtApMaterno.getText().trim());

        Alumno nuevo = new Alumno(
                alumnoOriginal.getNo_control(),
                nombreCompleto,
                txtCorreo.getText().trim(),
                new ItemCatalogo(0, (String) cmbCarrera.getSelectedItem()),
                Double.parseDouble(txtPromedio.getText().trim()),
                dcFechaNac.getDate(),
                EdicionHelper.aItems(lstMaterias.getSelectedValuesList()));

        if (controllerAdministracion.actualizarAlumno(nuevo))
        {
            JOptionPane.showMessageDialog(this, "Alumno actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            log.info("Alumno con número de control {} actualizado", alumnoOriginal.getNo_control());
            reiniciarEdicion();
        } else
        {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el alumno.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void llenarFormularioEdicion(Alumno a)
    {
        if (a == null)
        {
            return;
        }
        txtNoControl2.setText(a.getNo_control());
        txtNoControl2.setEditable(false);

        String[] partes = EdicionHelper.descomponerNombre(a.getNombre());
        txtNombre.setText(partes[0]);
        txtApPat.setText(partes[1]);
        txtApMaterno.setText(partes[2]);

        txtCorreo.setText(a.getCorreo());

        String carreraGuardada = ConsultaFiltroHelper.texto(a.getCarerra()).trim();
        boolean encontrada = false;
        for (int i = 0; i < cmbCarrera.getItemCount(); i++)
        {
            if (cmbCarrera.getItemAt(i).trim().equalsIgnoreCase(carreraGuardada))
            {
                cmbCarrera.setSelectedIndex(i);
                encontrada = true;
                break;
            }
        }
        if (!encontrada && !carreraGuardada.isEmpty())
        {
            cmbCarrera.addItem(carreraGuardada);
            cmbCarrera.setSelectedItem(carreraGuardada);
        }

        txtPromedio.setText(String.valueOf(a.getPromedio()));
        dcFechaNac.setDate(a.getFechaNaciomiento());
        seleccionarMateriasEdicion(a.getMaterias());
    }

    private void seleccionarMateriasEdicion(List<ItemCatalogo> materias)
    {
        List<Integer> idx = new ArrayList<>();
        List<String> nombresMat = EdicionHelper.nombres(materias);
        ListModel<String> m = lstMaterias.getModel();
        for (int i = 0; i < m.getSize(); i++)
        {
            if (nombresMat.contains(m.getElementAt(i)))
            {
                idx.add(i);
            }
        }
        lstMaterias.setSelectedIndices(idx.stream().mapToInt(Integer::intValue).toArray());
    }

    private boolean validarFormularioEdicion()
    {
        String nombre = txtNombre.getText().trim();
        String apPat = txtApPat.getText().trim();
        String apMat = txtApMaterno.getText().trim();
        String correo = txtCorreo.getText().trim();
        String promedio = txtPromedio.getText().trim();
        Date fecha = dcFechaNac.getDate();

        if (ValidadorAlumno.esVacio(nombre) || !ValidadorAlumno.nombreValido(nombre))
        {
            error("Error de validación", "El nombre es obligatorio y solo puede contener letras y espacios.");
            return false;
        }
        if (ValidadorAlumno.esVacio(apPat) || !ValidadorAlumno.nombreValido(apPat))
        {
            error("Error de validación", "El apellido paterno es obligatorio y debe ser válido.");
            return false;
        }
        if (ValidadorAlumno.esVacio(apMat) || !ValidadorAlumno.nombreValido(apMat))
        {
            error("Error de validación", "El apellido materno es obligatorio y debe ser válido.");
            return false;
        }
        if (ValidadorAlumno.esVacio(correo) || !ValidadorAlumno.correoValido(correo))
        {
            error("Error de validación", "El formato del correo no es válido. Ejemplo: usuario@dominio.com");
            txtCorreo.requestFocus();
            return false;
        }
        if (ValidadorAlumno.seleccionCb(cmbCarrera.getSelectedIndex()))
        {
            error("Error de validación", "La carrera es obligatoria.");
            return false;
        }
        if (ValidadorAlumno.esVacio(promedio) || ValidadorAlumno.promedioValido(promedio) == null)
        {
            error("Error de validación", "El promedio debe ser numérico y estar en el rango de 0 a 100.");
            return false;
        }
        if (ValidadorAlumno.fechaVacia(fecha) || ValidadorAlumno.fechaFutura(fecha) || ValidadorAlumno.fechaPasada(fecha))
        {
            error("Error de validación", "La fecha de nacimiento no puede ser futura ni anterior a 1950.");
            return false;
        }
        if (ValidadorAlumno.materiasVacias(lstMaterias.getSelectedValuesList()))
        {
            error("Error de validación", "Debe seleccionar al menos una materia.");
            return false;
        }
        return true;
    }

    private void cargarTablaCambios()
    {
        DefaultTableModel m = new DefaultTableModel(new String[]
        {
            "Campo", "Antes", "Después"
        }, 0)
        {
            @Override
            public boolean isCellEditable(int r, int c)
            {
                return false;
            }
        };

        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        String fechaTexto = dcFechaNac.getDate() != null ? f.format(dcFechaNac.getDate()) : "";

        String nombreCompleto = EdicionHelper.armarNombreCompleto(
                txtNombre.getText().trim(),
                txtApPat.getText().trim(),
                txtApMaterno.getText().trim());

        List<Object[]> cambios = EdicionHelper.calcularCambios(
                alumnoOriginal,
                nombreCompleto,
                txtCorreo.getText().trim(),
                (String) cmbCarrera.getSelectedItem(),
                txtPromedio.getText().trim(),
                fechaTexto,
                lstMaterias.getSelectedValuesList());

        for (Object[] fila : cambios)
        {
            m.addRow(fila);
        }

        tblCambios.setModel(m);
        lblResumen.setText(m.getRowCount() == 0 ? "Sin cambios" : m.getRowCount() + " campo(s) modificado(s)");
        chkConfirmo.setSelected(false);
        btnGuardar.setEnabled(false);
    }

    private void conScroll(JPanel card, String nombre)
    {
        jP_EdCard.remove(card);
        card.setVisible(true);
        javax.swing.JScrollPane sp = new javax.swing.JScrollPane(card);
        sp.setBorder(null);
        sp.getViewport().setBackground(Estilos.CREMA_CLARO);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        jP_EdCard.add(sp, nombre);
    }

    private void irA(String paso)
    {
        pasoActual = paso;
        ((java.awt.CardLayout) jP_EdCard.getLayout()).show(jP_EdCard, paso);
        switch (paso)
        {
            case "buscar":
                jTBuscar.setSelected(true);
                break;
            case "datos":
                jTDatos.setSelected(true);
                break;
            case "confirmar":
                jTConfirmar.setSelected(true);
                break;
        }
        pintarPestanasEdicion();
    }

    private void pintarPestanasEdicion()
    {
        for (AbstractButton t : new AbstractButton[]
        {
            jTBuscar, jTDatos, jTConfirmar
        })
        {
            boolean activa = t.isSelected();
            t.setBackground(activa ? Estilos.NEGRO : Estilos.AZUL_OSCURO);
            t.setForeground(activa ? Estilos.NARANJA : java.awt.Color.WHITE);
        }
    }

    private void poner(JPanel panel, java.awt.Component c, int x, int y, int w, int h)
    {
        panel.add(c, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
    }

    private void aplicarEstilosEdicion()
    {
        Estilos.estilizarFondo(jP_EncabezadoEd);
        Estilos.estilizarPanelConSombra(jP_EdCard);
        for (JPanel p : new JPanel[]
        {
            jP_EdBuscar, jP_EdDatos, jP_EdConfirmar, jP_Resultado
        })
        {
            p.setOpaque(true);
            p.setBackground(Estilos.CREMA_CLARO);
        }
        jP_Resultado.setBorder(Estilos.borde(2));

        jLabel1.setVisible(false);
        jP_EncabezadoEd.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 8));
        jP_EncabezadoEd.setPreferredSize(new java.awt.Dimension(0, 56));
        jTBuscar.setText("1. BUSCAR ALUMNO");
        jTDatos.setText("2. DATOS DEL ALUMNO");
        jTConfirmar.setText("3. CONFIRMAR");

        for (AbstractButton t : new AbstractButton[]
        {
            jTBuscar, jTDatos, jTConfirmar
        })
        {
            t.setFont(Estilos.fuente(Font.PLAIN, 20));
            t.setFocusPainted(false);
            t.setContentAreaFilled(false);
            t.setOpaque(true);
            t.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            t.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Estilos.NEGRO, 3),
                    BorderFactory.createEmptyBorder(3, 14, 3, 14)));
        }

        for (JLabel l : new JLabel[]
        {
            lblNoControl, jLabel14, jLabel15, jLabel16, jLabel17,
            jLabel18, jLabel19, jLabel20, jLabel21, jLabel22, lblResumen
        })
        {
            Estilos.estilizarEtiqueta(l);
        }
        Estilos.estilizarSubtitulo(jLabel23);
        jLabel23.setText("CONFIRMAR CAMBIOS");
        lblEstado.setFont(Estilos.fuente(Font.BOLD, 16));
        lblEstado.setForeground(Estilos.AZUL_OSCURO);

        // Tarjeta de alumno en resultado
        jP_Resultado.removeAll();
        jP_Resultado.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints g = new java.awt.GridBagConstraints();
        g.insets = new java.awt.Insets(8, 20, 8, 20);
        g.anchor = java.awt.GridBagConstraints.WEST;
        g.fill = java.awt.GridBagConstraints.HORIZONTAL;

        jLbl_Nombre.setFont(Estilos.fuente(Font.PLAIN, 26));
        jLbl_Carrera.setFont(Estilos.fuente(Font.BOLD, 16));
        jLbl_Correo.setFont(Estilos.fuente(Font.BOLD, 16));
        jLbl_Fecha.setFont(Estilos.fuente(Font.BOLD, 16));
        jLbl_Promedio.setFont(Estilos.fuente(Font.PLAIN, 30));
        jLbl_Promedio.setForeground(Estilos.AZUL_OSCURO);
        jLbl_Promedio.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        g.gridy = 0;
        g.gridx = 0;
        g.gridwidth = 2;
        g.weightx = 1;
        jP_Resultado.add(jLbl_Nombre, g);

        g.gridwidth = 1;
        g.gridy = 1;
        g.gridx = 0;
        g.weightx = 1;
        jP_Resultado.add(jLbl_Carrera, g);
        g.gridx = 1;
        g.weightx = 0;
        jP_Resultado.add(jLbl_Promedio, g);

        g.gridy = 2;
        g.gridx = 0;
        g.weightx = 1;
        jP_Resultado.add(jLbl_Correo, g);
        g.gridx = 1;
        g.weightx = 0;
        jP_Resultado.add(jLbl_Fecha, g);

        jLbl_Semestre.setVisible(false);

        Estilos.estilizarCampo(txtNoControl);
        Estilos.estilizarCampo(txtNoControl2);
        txtNoControl2.setBackground(Estilos.CREMA);
        Estilos.estilizarCampo(txtNombre);
        Estilos.estilizarCampo(txtApPat);
        Estilos.estilizarCampo(txtApMaterno);
        Estilos.estilizarCampo(txtCorreo);
        Estilos.estilizarCampo(txtPromedio);
        Estilos.estilizarCombo(cmbCarrera);
        Estilos.estilizarFecha(dcFechaNac);

        lstMaterias.setFont(Estilos.fuente(Font.PLAIN, 18));
        lstMaterias.setBackground(Estilos.CREMA_CLARO);
        lstMaterias.setSelectionBackground(Estilos.NARANJA);
        lstMaterias.setSelectionForeground(Estilos.NEGRO);
        jScrollPane2.setBorder(Estilos.borde(2));

        Estilos.estilizarTabla(tblCambios);
        jScrollPane3.setBorder(Estilos.borde(2));
        chkConfirmo.setOpaque(false);
        chkConfirmo.setFont(Estilos.fuente(Font.BOLD, 16));
        chkConfirmo.setForeground(Estilos.NEGRO);
        chkConfirmo.setText("ENTIENDO QUE ESTA ACCIÓN MODIFICA LOS DATOS DEL ALUMNO");

        btnBuscar.setText("BUSCAR");
        jBCancelar1.setText("CANCELAR");
        jBContinuar1.setText("CONTINUAR");
        btnAtras2.setText("ATRÁS");
        btnRestablecer.setText("RESTABLECER");
        btnCancelar2.setText("CANCELAR");
        btnContinuar2.setText("CONTINUAR");
        btnAtras3.setText("ATRÁS");
        btnGuardar.setText("GUARDAR CAMBIOS");

        for (JButton b : new JButton[]
        {
            btnBuscar, jBCancelar1, btnAtras2, btnRestablecer, btnCancelar2, btnAtras3
        })
        {
            Estilos.estilizarBoton(b);
            b.setFont(Estilos.fuente(Font.PLAIN, 18));
        }
        for (JButton b : new JButton[]
        {
            jBContinuar1, btnContinuar2, btnGuardar
        })
        {
            Estilos.estilizarBotonPrincipal(b);
            b.setFont(Estilos.fuente(Font.PLAIN, 18));
        }

        poner(jP_EdBuscar, lblNoControl, 60, 20, -1, -1);
        poner(jP_EdBuscar, txtNoControl, 60, 45, 300, 40);
        poner(jP_EdBuscar, btnBuscar, 380, 45, -1, 40);
        poner(jP_EdBuscar, lblEstado, 60, 95, 600, -1);
        poner(jP_EdBuscar, jP_Resultado, 60, 130, 540, 130);
        poner(jP_EdBuscar, jBCancelar1, 60, 280, -1, -1);
        poner(jP_EdBuscar, jBContinuar1, 220, 280, -1, -1);

        poner(jP_EdDatos, btnAtras2, 60, 600, -1, -1);
        poner(jP_EdDatos, btnRestablecer, 200, 600, -1, -1);
        poner(jP_EdDatos, btnCancelar2, 360, 600, -1, -1);
        poner(jP_EdDatos, btnContinuar2, 500, 600, -1, -1);

        pintarPestanasEdicion();
    }

    private void limpiarResultadoEdicion()
    {
        jLbl_Nombre.setText("");
        jLbl_Carrera.setText("");
        jLbl_Semestre.setText("");
        jLbl_Correo.setText("");
        jLbl_Fecha.setText("");
        jLbl_Promedio.setText("");
        lblEstado.setText("");
        jP_Resultado.setVisible(false);
    }

    private void reiniciarEdicion()
    {
        txtNoControl.setText("");
        alumnoOriginal = null;
        limpiarResultadoEdicion();
        jBContinuar1.setEnabled(false);
        irA("buscar");
    }

    // LÓGICA DE CONSULTA (READ)
    private void iniciarConsulta()
    {
        jScrollPane4.setViewportView(listaAlumnos);
        listaAlumnos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaAlumnos.setCellRenderer(new AlumnoTarjetaRenderer());

        jtxtBuscar.setText("");
        jComboBox1_C.setModel(new DefaultComboBoxModel<>(new String[]
        {
            "Nombre (A-Z)", "Nombre (Z-A)", "Promedio (mayor)",
            "Promedio (menor)", "No. de control"
        }));
        jspMin.setModel(new javax.swing.SpinnerNumberModel(0.0, 0.0, 100.0, 1.0));
        jspMax.setModel(new javax.swing.SpinnerNumberModel(100.0, 0.0, 100.0, 1.0));
        jbtnLimpiar.addActionListener(e -> limpiarFiltros());

        cargarCatalogoCarreras();
        recargarDatos();
        configurarEventos();
        aplicarFiltros();
    }

    private void limpiarFiltros()
    {
        cargando = true;
        jtxtBuscar.setText("");
        jcbCarrera.setSelectedIndex(0);
        jcbMateria.setSelectedIndex(0);
        jComboBox1_C.setSelectedIndex(0);
        jspMin.setValue(0.0);
        jspMax.setValue(100.0);
        cargando = false;
        aplicarFiltros();
    }

    private void configurarEventos()
    {
        jtxtBuscar.getDocument().addDocumentListener(new DocumentListener()
        {
            public void insertUpdate(DocumentEvent e)
            {
                aplicarFiltros();
            }

            public void removeUpdate(DocumentEvent e)
            {
                aplicarFiltros();
            }

            public void changedUpdate(DocumentEvent e)
            {
                aplicarFiltros();
            }
        });

        jcbCarrera.addActionListener(e -> aplicarFiltros());
        jcbMateria.addActionListener(e -> aplicarFiltros());
        jComboBox1_C.addActionListener(e -> aplicarFiltros());

        jspMin.addChangeListener(e -> alCambiarRango());
        jspMax.addChangeListener(e -> alCambiarRango());

        listaAlumnos.addListSelectionListener(e ->
        {
            if (!e.getValueIsAdjusting())
            {
                mostrarFicha(listaAlumnos.getSelectedValue());
            }
        });
    }

    public void cargarTabla(String filtroCarrera)
    {
        recargarDatos();
        if (filtroCarrera != null)
        {
            cargando = true;
            jcbCarrera.setSelectedItem(filtroCarrera);
            if (jcbCarrera.getSelectedItem() == null)
            {
                jcbCarrera.setSelectedIndex(0);
            }
            cargando = false;
        }
        aplicarFiltros();
    }

    private void cargarCatalogoCarreras()
    {
        cargando = true;
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();
        modelo.addElement(ConsultaFiltroHelper.TODAS_CARRERAS);
        try
        {
            List<String> carreras = catalogo.obtenerCarreras();
            if (carreras != null)
            {
                for (String c : carreras)
                {
                    if (c != null && !c.toLowerCase().startsWith("seleccione"))
                    {
                        modelo.addElement(c);
                    }
                }
            }
        } catch (AppException e)
        {
            log.error("Error al cargar las carreras", e);
            error("Error", e.getMessage());
        }
        jcbCarrera.setModel(modelo);
        cargando = false;
    }

    private void recargarDatos()
    {
        cargando = true;
        String materiaPrevia = seleccionado(jcbMateria);

        List<Alumno> lista = controllerAdministracion.consultarAlumno();
        todos = (lista == null) ? new ArrayList<>() : new ArrayList<>(lista);
        materiasDe.clear();

        TreeSet<String> materias = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Alumno a : todos)
        {
            List<String> ms = new ArrayList<>();
            int pos = controllerAdministracion.obtenerPosicionAlumno(ConsultaFiltroHelper.texto(a.getNo_control()));
            if (pos != -1)
            {
                List<ItemCatalogo> deAlumno = controllerAdministracion.obtenerMaterias(pos);
                if (deAlumno != null)
                {
                    ms.addAll(EdicionHelper.nombres(deAlumno));
                }
            }
            materiasDe.put(a, ms);
            materias.addAll(ms);
        }

        DefaultComboBoxModel<String> modMaterias = new DefaultComboBoxModel<>();
        modMaterias.addElement(ConsultaFiltroHelper.TODAS_MATERIAS);
        for (String m : materias)
        {
            modMaterias.addElement(m);
        }
        jcbMateria.setModel(modMaterias);
        if (materias.contains(materiaPrevia))
        {
            jcbMateria.setSelectedItem(materiaPrevia);
        }
        cargando = false;
    }

    private void aplicarFiltros()
    {
        if (cargando)
        {
            return;
        }

        String buscado = jtxtBuscar.getText();
        String carrera = seleccionado(jcbCarrera);
        String materia = seleccionado(jcbMateria);
        double min = valor(jspMin);
        double max = valor(jspMax);
        int criterioOrden = jComboBox1_C.getSelectedIndex();

        List<Alumno> resultado = ConsultaFiltroHelper.filtrar(
                todos, buscado, carrera, materia, min, max, criterioOrden, materiasDe);

        Alumno anterior = listaAlumnos.getSelectedValue();
        modeloLista.clear();
        for (Alumno a : resultado)
        {
            modeloLista.addElement(a);
        }

        if (anterior != null && resultado.contains(anterior))
        {
            listaAlumnos.setSelectedValue(anterior, true);
        } else
        {
            listaAlumnos.clearSelection();
            mostrarFicha(null);
        }

        jlblContador.setText("Mostrando " + resultado.size() + " de " + todos.size() + " alumnos");
    }

    private void alCambiarRango()
    {
        if (cargando)
        {
            return;
        }
        if (valor(jspMin) > valor(jspMax))
        {
            cargando = true;
            double a = valor(jspMin);
            jspMin.setValue(valor(jspMax));
            jspMax.setValue(a);
            cargando = false;
        }
        aplicarFiltros();
    }

    private void mostrarFicha(Alumno a)
    {
        if (a == null)
        {
            lblFNombre.setText("Selecciona un alumno");
            lblFControl.setText("No. de control: -");
            lblFCorreo.setText("-");
            lblFCarrera.setText("-");
            lblFProm.setText("-");
            txtFMaterias.setText("");
            return;
        }

        double prom = ConsultaFiltroHelper.promedioDe(a);

        lblFNombre.setText(ConsultaFiltroHelper.texto(a.getNombre()));
        lblFControl.setText("No. de control: " + ConsultaFiltroHelper.texto(a.getNo_control()));
        lblFCorreo.setText(ConsultaFiltroHelper.texto(a.getCorreo()));
        lblFCarrera.setText(ConsultaFiltroHelper.texto(a.getCarerra()));
        lblFProm.setText(ConsultaFiltroHelper.formatoPromedio(prom));

        List<String> ms = materiasDe.get(a);
        StringBuilder sb = new StringBuilder();
        if (ms == null || ms.isEmpty())
        {
            sb.append("Sin materias asignadas");
        } else
        {
            for (String m : ms)
            {
                sb.append("• ").append(m).append("\n");
            }
        }
        txtFMaterias.setText(sb.toString());
        txtFMaterias.setCaretPosition(0);
    }

    // REPORTES Y UTILIDADES
    private void generarReportePdf()
    {
        List<Alumno> lista = new ArrayList<>();
        for (int i = 0; i < modeloLista.size(); i++)
        {
            lista.add(modeloLista.get(i));
        }
        if (lista.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "No hay información suficiente para crear el documento.", "Error de creación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Pdf.generarPdf(lista);
    }

    private void mostrarDialogoAyuda()
    {
        JOptionPane.showMessageDialog(this,
                "• Escribe en «Buscar» un nombre, un número de control o un correo.\n"
                + "• Elige carrera, materia o ajusta el rango de promedio: la lista se actualiza sola.\n"
                + "• Puedes usar un solo filtro o varios a la vez; se combinan.\n"
                + "• Haz clic en un alumno de la lista para ver su información a la derecha.\n"
                + "• Pulsa «Limpiar filtros» para reiniciar la búsqueda.",
                "¿Cómo busco?", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String titulo, String msg)
    {
        JOptionPane.showMessageDialog(this, msg, titulo, JOptionPane.ERROR_MESSAGE);
    }

    private static String seleccionado(javax.swing.JComboBox<String> cb)
    {
        Object o = cb.getSelectedItem();
        return o == null ? "" : o.toString();
    }

    private static double valor(JSpinner sp)
    {
        return ((Number) sp.getValue()).doubleValue();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton JBtnBuscarBaja;
    private javax.swing.JLabel JLblCarreraBaja;
    private javax.swing.JLabel JLblCorreoBaja;
    private javax.swing.JLabel JLblFeNac;
    private javax.swing.JLabel JLblSemestreBaja;
    private javax.swing.JButton btnAgregarMat;
    private javax.swing.JButton btnAtras2;
    private javax.swing.JButton btnAtras3;
    private javax.swing.JButton btnAyuda;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar2;
    private javax.swing.JButton btnContinuar2;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnQuitarMat;
    private javax.swing.JButton btnRestablecer;
    private javax.swing.JCheckBox chkConfirmo;
    private javax.swing.JComboBox<String> cmbCarrera;
    private com.toedter.calendar.JDateChooser dcFechaNac;
    private javax.swing.JButton jBCancelar1;
    private javax.swing.JButton jBContinuar1;
    private javax.swing.JButton jB_Cancelar3;
    private javax.swing.JButton jB_Guardar;
    private javax.swing.JButton jBttn_Alta;
    private javax.swing.JButton jBttn_Baja;
    private javax.swing.JButton jBttn_Cancelar;
    private javax.swing.JButton jBttn_Cancelar_2;
    private javax.swing.JButton jBttn_Consulta;
    private javax.swing.JButton jBttn_ContinuarDP;
    private javax.swing.JButton jBttn_ContinuarMP;
    private javax.swing.JButton jBttn_CuentasExtr;
    private javax.swing.JButton jBttn_DatosPrincipales;
    private javax.swing.JButton jBttn_Edicion;
    private javax.swing.JButton jBttn_MateriasProm;
    private javax.swing.JButton jBttn_Pdf;
    private javax.swing.JButton jBttn_Regresar;
    private javax.swing.JButton jButtonCancelarBaja;
    private javax.swing.JButton jButtonDarBaja;
    private javax.swing.JComboBox<String> jCB_Carrera;
    private javax.swing.JComboBox<String> jComboBox1_C;
    private com.toedter.calendar.JDateChooser jDC_Nacimiento;
    private javax.swing.JLabel jL_ApellidoMat;
    private javax.swing.JLabel jL_ApellidoPat;
    private javax.swing.JLabel jL_Carrera;
    private javax.swing.JLabel jL_Fecha;
    private javax.swing.JLabel jL_Materias;
    private javax.swing.JLabel jL_NoControl;
    private javax.swing.JLabel jL_Nombres1;
    private javax.swing.JLabel jL_Promedio;
    private javax.swing.JLabel jL_Semestre;
    private javax.swing.JLabel jL_TituloGeneral;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLb_CONSULTA_ORDENAR;
    private javax.swing.JLabel jLb_Consulta_A;
    private javax.swing.JLabel jLblPromedioBaja;
    private javax.swing.JLabel jLbl_Carrera;
    private javax.swing.JLabel jLbl_Contra;
    private javax.swing.JLabel jLbl_Correo;
    private javax.swing.JLabel jLbl_CuentaExtras;
    private javax.swing.JLabel jLbl_Fecha;
    private javax.swing.JLabel jLbl_Nombre;
    private javax.swing.JLabel jLbl_Promedio;
    private javax.swing.JLabel jLbl_Semestre;
    private javax.swing.JLabel jLbl_correo;
    private javax.swing.JPasswordField jPF_contraseña;
    private javax.swing.JPanel jP_AltCard;
    private javax.swing.JPanel jP_Alta;
    private javax.swing.JPanel jP_Baja;
    private javax.swing.JPanel jP_Bienvenida;
    private javax.swing.JPanel jP_CONSULTA_COMP;
    private javax.swing.JPanel jP_Consulta;
    private javax.swing.JPanel jP_ConsultaC;
    private javax.swing.JPanel jP_CuentasExtr;
    private javax.swing.JPanel jP_DatosPrincipales;
    private javax.swing.JPanel jP_EdBuscar;
    private javax.swing.JPanel jP_EdCard;
    private javax.swing.JPanel jP_EdConfirmar;
    private javax.swing.JPanel jP_EdDatos;
    private javax.swing.JPanel jP_Edicion;
    private javax.swing.JPanel jP_Encabezado;
    private javax.swing.JPanel jP_EncabezadoEd;
    private javax.swing.JPanel jP_MateriasProm;
    private javax.swing.JPanel jP_Resultado;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelFiltros;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPaneMaterias;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JToggleButton jTBuscar;
    private javax.swing.JToggleButton jTConfirmar;
    private javax.swing.JToggleButton jTDatos;
    private javax.swing.JTable jTb_Materias;
    private javax.swing.JTextField jTextSemestreBaja;
    private javax.swing.JTextField jTxtBuscarBaja;
    private javax.swing.JTextField jTxtCarreraBaja;
    private javax.swing.JTextField jTxtCorreoBaja;
    private javax.swing.JTextField jTxtEstatusBusqueda;
    private javax.swing.JTextField jTxtFechaNacBaja;
    private javax.swing.JTextField jTxtFieldNombre;
    private javax.swing.JTextField jTxtPromedioBaja;
    private javax.swing.JTextField jTxt_ApellidoMat;
    private javax.swing.JTextField jTxt_ApellidoPat;
    private javax.swing.JTextField jTxt_Correo;
    private javax.swing.JTextField jTxt_NoControl;
    private javax.swing.JTextField jTxt_Promedio;
    private javax.swing.JTextField jTxt_Semestre;
    private javax.swing.JTextField jTxt_nombre;
    private javax.swing.JButton jbtnLimpiar;
    private javax.swing.JComboBox<String> jcbCarrera;
    private javax.swing.JComboBox<String> jcbMateria;
    private javax.swing.JLabel jlblContador;
    private javax.swing.JList<String> jlistaAlumnos;
    private javax.swing.JPanel jp_Contenedor;
    private javax.swing.JSpinner jspMax;
    private javax.swing.JSpinner jspMin;
    private javax.swing.JTextField jtxtBuscar;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblFCarrera;
    private javax.swing.JLabel lblFControl;
    private javax.swing.JLabel lblFCorreo;
    private javax.swing.JLabel lblFNombre;
    private javax.swing.JLabel lblFProm;
    private javax.swing.JLabel lblNoControl;
    private javax.swing.JLabel lblResumen;
    private javax.swing.JLabel lblTitBuscar;
    private javax.swing.JLabel lblTitCarrera;
    private javax.swing.JLabel lblTitCarrera1;
    private javax.swing.JLabel lblTitCorreo;
    private javax.swing.JLabel lblTitMateria;
    private javax.swing.JLabel lblTitPromedio;
    private javax.swing.JLabel lblTitRango;
    private javax.swing.JList<String> lstMaterias;
    private javax.swing.JTable tblCambios;
    private javax.swing.JTextField txtApMaterno;
    private javax.swing.JTextField txtApPat;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextArea txtFMaterias;
    private javax.swing.JTextField txtNoControl;
    private javax.swing.JTextField txtNoControl2;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPromedio;
    // End of variables declaration//GEN-END:variables
}
