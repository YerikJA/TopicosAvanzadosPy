package views;

import controller.*;
import estilos.Estilos;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;
import model.Alumno;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import validaciones.ValidadorAlumno;

public class VtnAdministrador extends javax.swing.JFrame
{

    private AlumnoController controllerAdministracion;

    private Alumno alumnoOriginal;
    private String pasoActual = "buscar";
    private final javax.swing.ButtonGroup grupoPestanasEd = new javax.swing.ButtonGroup();

    public VtnAdministrador(AlumnoController controller)
    {
        initComponents();
        jP_Encabezado.setPreferredSize(new java.awt.Dimension(0, 48));
        controllerAdministracion = controller;
        //jP_Edicion.setLayout(new java.awt.BorderLayout());
        //jP_Edicion.add(new PanelEdicion(controller), java.awt.BorderLayout.CENTER);

        setTitle("Administracion");
        cargarTabla();

        // ---- FORMATO GENERAL ----
        Estilos.estilizarVentana(this);

        Estilos.estilizarTitulo(jL_TituloGeneral);
        jL_TituloGeneral.setFont(Estilos.fuente(java.awt.Font.PLAIN, 48));
        Estilos.estilizarTitulo(jLabel2);
        jLabel2.setFont(Estilos.fuente(java.awt.Font.PLAIN, 48));   // corregido

        Estilos.estilizarTitulo(jLb_Consulta_buscar);

        // ---- MENÚ Y PESTAÑAS ----
        Estilos.estilizarMenu(jBttn_Alta, jBttn_Consulta, jBttn_Edicion, jBttn_Baja);
        Estilos.estilizarBoton(jBttn_Regresar);
        Estilos.estilizarPestanas(jBttn_DatosPrincipales, jBttn_MateriasProm, jBttn_CuentasExtr);

        // ---- PANELES ----
        Estilos.estilizarFondo(jp_Contenedor); // sin borde: cada pantalla lleva el suyo
        Estilos.estilizarFondo(jP_Bienvenida);
        // Panel de alta
        Estilos.estilizarFondo(jP_Alta);

        // ---- ETIQUETAS ----
        // Alta: datos principales
        Estilos.estilizarEtiqueta(jL_Nombres1);
        Estilos.estilizarEtiqueta(jL_ApellidoPat);
        Estilos.estilizarEtiqueta(jL_ApellidoMat);
        Estilos.estilizarEtiqueta(jL_Fecha);
        // Alta: materias y promedio
        Estilos.estilizarEtiqueta(jL_Materias);
        Estilos.estilizarEtiqueta(jL_Semestre);
        Estilos.estilizarEtiqueta(jL_Promedio);
        // Alta: cuenta y extras
        Estilos.estilizarEtiqueta(jLbl_CuentaExtras);
        jLbl_CuentaExtras.setFont(Estilos.fuente(java.awt.Font.PLAIN, 24));
        Estilos.estilizarEtiqueta(jLbl_Contra);
        jLbl_Contra.setFont(Estilos.fuente(java.awt.Font.PLAIN, 16));
        Estilos.estilizarEtiqueta(jLbl_Telefono);
        jLbl_Telefono.setFont(Estilos.fuente(java.awt.Font.PLAIN, 16));
        Estilos.estilizarEtiqueta(jLbl_Estatus);
        jLbl_Estatus.setFont(Estilos.fuente(java.awt.Font.PLAIN, 16));
        Estilos.estilizarEtiqueta(jLbl_localidad);
        jLbl_localidad.setFont(Estilos.fuente(java.awt.Font.PLAIN, 16));
        // Consulta
        Estilos.estilizarEtiqueta(jLb_CONSULTA_CARR);
        jLb_CONSULTA_CARR.setFont(Estilos.fuente(java.awt.Font.PLAIN, 16));
        Estilos.estilizarEtiqueta(jLb_CONSULTA_ORDENAR);
        jLb_CONSULTA_ORDENAR.setFont(Estilos.fuente(java.awt.Font.PLAIN, 16));
        Estilos.estilizarEtiqueta(jLb_CONSULTA_PROM);
        jLb_CONSULTA_PROM.setFont(Estilos.fuente(java.awt.Font.PLAIN, 16));
        Estilos.estilizarEtiqueta(jLb_Consulta_A);
        jLb_Consulta_A.setFont(Estilos.fuente(java.awt.Font.PLAIN, 16));
        Estilos.estilizarEtiqueta(jLb_Consulta_Sit_Ac);
        jLb_Consulta_Sit_Ac.setFont(Estilos.fuente(java.awt.Font.PLAIN, 16));
        Estilos.estilizarEtiqueta(jLb_Consulta_buscar);
        jLb_Consulta_buscar.setFont(Estilos.fuente(java.awt.Font.PLAIN, 16));
        Estilos.estilizarEtiqueta(jLb_Consulta_materias);
        jLb_Consulta_materias.setFont(Estilos.fuente(java.awt.Font.PLAIN, 16));

        // ---- CAMPOS DE TEXTO ----
        Estilos.estilizarCampo(jTxt_Semestre);
        Estilos.estilizarCampo(jTxt_Promedio);
        Estilos.estilizarCampo(jTxt_ApellidoMat);
        Estilos.estilizarCampo(jTxt_ApellidoPat);
        Estilos.estilizarCampo(jTxt_nombre);
        Estilos.estilizarFecha(jDC_Nacimiento);

        // ---- BOTONES ----
        // Consulta: situación académica
        Estilos.estilizarBoton(jB_Consulta_Todos);
        Estilos.estilizarBoton(jB_Consulta_Sobresal);
        Estilos.estilizarBoton(jB_Cosnulta_Aprob);
        Estilos.estilizarBoton(jB_Consulta_Riesg);
        Estilos.estilizarBoton(jB_Consulta_Rep);
        // Alta: datos principales
        Estilos.estilizarBotonPrincipal(jBttn_Continuar);
        Estilos.estilizarBoton(jBttn_Cancelar);
        // Alta: materias y promedio
        Estilos.estilizarBotonPrincipal(jBttn_Continuar_2);
        Estilos.estilizarBoton(jBttn_Cancelar_2);
        // Alta: cuenta y extras
        Estilos.estilizarBotonPrincipal(jB_Guardar);
        Estilos.estilizarBoton(jB_Cancelar3);
        // Consulta y Baja
        Estilos.estilizarBotonPrincipal(jButton1);   // BUSCAR
        Estilos.estilizarBoton(jButton4);            // LIMPIAR
        Estilos.estilizarBotonPrincipal(jButton2);   // DAR BAJA
        Estilos.estilizarBoton(jButton3);            // CANCELAR

        // ---- COMBOS ----
        Estilos.estilizarCombo(jCb_Estatus);
        Estilos.estilizarCombo(jCb_Localidad);
        Estilos.estilizarCombo(jCb_CONSULTA_CARR);
        Estilos.estilizarCombo(jCb_Consulta_Ordenar);
        Estilos.estilizarCombo(jCb_Consulta_Materias);

        Estilos.estilizarCampo(jTxt_telefono);
        Estilos.estilizarCampo(jTxt_contraseña);
        Estilos.estilizarCampo(jTxt_Consulta_Buscar);
        Estilos.estilizarSpinner(jSpinner1);
        Estilos.estilizarSpinner(jSpinner2);
        Estilos.estilizarScroll(jScrollPane1);

        jP_ConsultaC.setOpaque(true);
        jP_ConsultaC.setBackground(Estilos.CREMA_CLARO);
        jP_CONSULTA_COMP.setOpaque(true);
        jP_CONSULTA_COMP.setBackground(Estilos.CREMA_CLARO);
        jP_CONSULTA_COMP.setBorder(null);

        // ---- TABLAS ----
        Estilos.estilizarTabla(jMaterias);

        // ---- PANELES CON SOMBRA ----
        Estilos.estilizarPanelConSombra(jP_Bienvenida);
        Estilos.estilizarPanelConSombra(jP_Consulta);
        Estilos.estilizarFondo(jP_Edicion);
        Estilos.estilizarPanelConSombra(jP_Baja);

        // ---- ALTA: PESTAÑAS ARRIBA, CAJA DEBAJO ----
        jP_Alta.add(jP_Encabezado, java.awt.BorderLayout.NORTH);
        jP_Alta.add(jP_AltCard, java.awt.BorderLayout.CENTER);

        jP_Encabezado.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));
        jP_Encabezado.setPreferredSize(new java.awt.Dimension(0, 48));
        Estilos.estilizarFondo(jP_Encabezado);

        Estilos.estilizarPanelConSombra(jP_AltCard);

        javax.swing.JPanel[] pantallasAlta =
        {
            jP_DatosPrincipales, jP_MateriasProm, jP_CuentasExtr
        };
        for (javax.swing.JPanel p : pantallasAlta)
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

        // ---- ICONOS VECTORIALES ----
        Estilos.icono(jBttn_Alta, FontAwesomeSolid.USER_PLUS, 22);
        Estilos.icono(jBttn_Consulta, FontAwesomeSolid.SEARCH, 22);
        Estilos.icono(jBttn_Edicion, FontAwesomeSolid.PENCIL_ALT, 22);
        Estilos.icono(jBttn_Baja, FontAwesomeSolid.TRASH, 22);
        Estilos.icono(jBttn_Regresar, FontAwesomeSolid.ARROW_LEFT, 22);

        Estilos.icono(jBttn_DatosPrincipales, FontAwesomeSolid.USER, 18);
        Estilos.icono(jBttn_MateriasProm, FontAwesomeSolid.LIST_ALT, 18);
        Estilos.icono(jBttn_CuentasExtr, FontAwesomeSolid.LOCK, 18);

        Estilos.icono(jBttn_Continuar, FontAwesomeSolid.ARROW_RIGHT, 18);
        Estilos.icono(jBttn_Continuar_2, FontAwesomeSolid.ARROW_RIGHT, 18);
        Estilos.icono(jBttn_Cancelar, FontAwesomeSolid.TIMES, 18);
        Estilos.icono(jBttn_Cancelar_2, FontAwesomeSolid.TIMES, 18);
        Estilos.icono(jB_Cancelar3, FontAwesomeSolid.TIMES, 18);
        Estilos.icono(jB_Guardar, FontAwesomeSolid.SAVE, 18);

        Estilos.icono(jB_Consulta_Todos, FontAwesomeSolid.USERS, 18);
        Estilos.icono(jB_Consulta_Sobresal, FontAwesomeSolid.STAR, 18);
        Estilos.icono(jB_Cosnulta_Aprob, FontAwesomeSolid.CHECK_CIRCLE, 18);
        Estilos.icono(jB_Consulta_Riesg, FontAwesomeSolid.EXCLAMATION_TRIANGLE, 18);
        Estilos.icono(jB_Consulta_Rep, FontAwesomeSolid.TIMES_CIRCLE, 18);

        // etiquetas
        Estilos.icono(jLbl_CuentaExtras, FontAwesomeSolid.COG, 24);
        Estilos.icono(jLbl_Contra, FontAwesomeSolid.KEY, 16);
        Estilos.icono(jLbl_Telefono, FontAwesomeSolid.PHONE, 16);
        Estilos.icono(jLbl_localidad, FontAwesomeSolid.MAP_MARKER_ALT, 16);
        Estilos.icono(jLbl_Estatus, FontAwesomeSolid.USER_CHECK, 16);

        Estilos.icono(jL_Semestre, FontAwesomeSolid.CALENDAR_ALT, 16);
        Estilos.icono(jL_Materias, FontAwesomeSolid.BOOK, 16);
        Estilos.icono(jL_Promedio, FontAwesomeSolid.CHART_LINE, 16);

        Estilos.icono(jLb_Consulta_buscar, FontAwesomeSolid.SEARCH, 32);
        Estilos.icono(jLb_CONSULTA_CARR, FontAwesomeSolid.GRADUATION_CAP, 16);
        Estilos.icono(jLb_CONSULTA_ORDENAR, FontAwesomeSolid.SORT_AMOUNT_DOWN, 16);
        Estilos.icono(jLb_Consulta_materias, FontAwesomeSolid.BOOK, 16);
        Estilos.icono(jLb_CONSULTA_PROM, FontAwesomeSolid.CHART_LINE, 16);

        cargarCatalogos();
    }

    private void cargarCatalogos()
    {
        CatalogoController catalogo = new CatalogoController();
        //creado como un metodo list, necesita un Array
        jCb_Estatus.setModel(new javax.swing.DefaultComboBoxModel<>(catalogo.obtenerEstatus().toArray(new String[0])));
        jCb_Localidad.setModel(new javax.swing.DefaultComboBoxModel<>(catalogo.obtenerLocalidades().toArray(new String[0])));

        // ===== EDICIÓN =====
        grupoPestanasEd.add(jTBuscar);
        grupoPestanasEd.add(jTDatos);
        grupoPestanasEd.add(jTConfirmar);
        java.awt.event.ActionListener soloIndicador = e -> irA(pasoActual);
        jTBuscar.addActionListener(soloIndicador);
        jTDatos.addActionListener(soloIndicador);
        jTConfirmar.addActionListener(soloIndicador);

        CatalogoController catalogoEd = new CatalogoController();
        cmbCarrera.setModel(new javax.swing.DefaultComboBoxModel<>(
                catalogoEd.obtenerCarreras().toArray(new String[0])));
        lstMaterias.setListData(catalogoEd.obtenerMaterias().toArray(new String[0]));

        // Ocultos por ahora (apellidos y botones de materias)
        jLabel16.setVisible(false);
        txtApPat.setVisible(false);
        jLabel17.setVisible(false);
        txtApMaterno.setVisible(false);
        btnAgregarMat.setVisible(false);
        btnQuitarMat.setVisible(false);

        btnAtras2.addActionListener(e -> irA("buscar"));
        btnRestablecer.addActionListener(e -> llenarFormulario(alumnoOriginal));
        btnCancelar2.addActionListener(e -> reiniciar());

        limpiarResultado();
        conScroll(jP_EdBuscar, "buscar");
        conScroll(jP_EdDatos, "datos");
        conScroll(jP_EdConfirmar, "confirmar");
        aplicarEstilos();
        irA("buscar");

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jL_TituloGeneral = new javax.swing.JLabel();
        jBttn_Edicion = new javax.swing.JButton();
        jBttn_Alta = new javax.swing.JButton();
        jBttn_Consulta = new javax.swing.JButton();
        jBttn_Baja = new javax.swing.JButton();
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
        jBttn_Continuar = new javax.swing.JButton();
        jP_MateriasProm = new javax.swing.JPanel();
        jBttn_Continuar_2 = new javax.swing.JButton();
        jBttn_Cancelar_2 = new javax.swing.JButton();
        jL_Semestre = new javax.swing.JLabel();
        jL_Materias = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jMaterias = new javax.swing.JTable();
        jL_Promedio = new javax.swing.JLabel();
        jTxt_Semestre = new javax.swing.JTextField();
        jTxt_Promedio = new javax.swing.JTextField();
        jP_CuentasExtr = new javax.swing.JPanel();
        jLbl_CuentaExtras = new javax.swing.JLabel();
        jLbl_Contra = new javax.swing.JLabel();
        jLbl_Estatus = new javax.swing.JLabel();
        jCb_Estatus = new javax.swing.JComboBox<>();
        jTxt_contraseña = new javax.swing.JTextField();
        jLbl_Telefono = new javax.swing.JLabel();
        jLbl_localidad = new javax.swing.JLabel();
        jTxt_telefono = new javax.swing.JTextField();
        jCb_Localidad = new javax.swing.JComboBox<>();
        jB_Cancelar3 = new javax.swing.JButton();
        jB_Guardar = new javax.swing.JButton();
        jP_Consulta = new javax.swing.JPanel();
        jP_ConsultaC = new javax.swing.JPanel();
        jP_CONSULTA_COMP = new javax.swing.JPanel();
        jLb_Consulta_buscar = new javax.swing.JLabel();
        jTxt_Consulta_Buscar = new javax.swing.JTextField();
        jLb_CONSULTA_CARR = new javax.swing.JLabel();
        jCb_CONSULTA_CARR = new javax.swing.JComboBox<>();
        jLb_CONSULTA_ORDENAR = new javax.swing.JLabel();
        jCb_Consulta_Ordenar = new javax.swing.JComboBox<>();
        jLb_Consulta_materias = new javax.swing.JLabel();
        jCb_Consulta_Materias = new javax.swing.JComboBox<>();
        jLb_CONSULTA_PROM = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jLb_Consulta_A = new javax.swing.JLabel();
        jSpinner2 = new javax.swing.JSpinner();
        jLb_Consulta_Sit_Ac = new javax.swing.JLabel();
        jB_Consulta_Todos = new javax.swing.JButton();
        jB_Consulta_Sobresal = new javax.swing.JButton();
        jB_Cosnulta_Aprob = new javax.swing.JButton();
        jB_Consulta_Riesg = new javax.swing.JButton();
        jB_Consulta_Rep = new javax.swing.JButton();
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
        jBContinuar1 = new javax.swing.JButton();
        jBCancelar1 = new javax.swing.JButton();
        jP_Baja = new javax.swing.JPanel();
        jP_Baja1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jTextField9 = new javax.swing.JTextField();
        jBttn_Regresar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jL_TituloGeneral.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jL_TituloGeneral.setText("ADMINISTRACION");

        jBttn_Edicion.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jBttn_Edicion.setText("EDICIÓN");
        jBttn_Edicion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBttn_EdicionActionPerformed(evt);
            }
        });

        jBttn_Alta.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jBttn_Alta.setText("ALTA");
        jBttn_Alta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBttn_AltaActionPerformed(evt);
            }
        });

        jBttn_Consulta.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jBttn_Consulta.setText("CONSULTA");
        jBttn_Consulta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBttn_ConsultaActionPerformed(evt);
            }
        });

        jBttn_Baja.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jBttn_Baja.setText("BAJA");
        jBttn_Baja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBttn_BajaActionPerformed(evt);
            }
        });

        jp_Contenedor.setLayout(new java.awt.CardLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel2.setText("BIENVENIDO");

        javax.swing.GroupLayout jP_BienvenidaLayout = new javax.swing.GroupLayout(jP_Bienvenida);
        jP_Bienvenida.setLayout(jP_BienvenidaLayout);
        jP_BienvenidaLayout.setHorizontalGroup(
            jP_BienvenidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_BienvenidaLayout.createSequentialGroup()
                .addGap(245, 245, 245)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(270, Short.MAX_VALUE))
        );
        jP_BienvenidaLayout.setVerticalGroup(
            jP_BienvenidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_BienvenidaLayout.createSequentialGroup()
                .addGap(153, 153, 153)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(237, Short.MAX_VALUE))
        );

        jp_Contenedor.add(jP_Bienvenida, "card6");

        jP_Alta.setLayout(new java.awt.BorderLayout());

        jBttn_DatosPrincipales.setText("1. DATOS PRINCIPALES");
        jBttn_DatosPrincipales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBttn_DatosPrincipalesActionPerformed(evt);
            }
        });
        jP_Encabezado.add(jBttn_DatosPrincipales);

        jBttn_MateriasProm.setText("2. MATERIAS Y PROMEDIO");
        jP_Encabezado.add(jBttn_MateriasProm);

        jBttn_CuentasExtr.setText("3. CUENTA Y EXTRAS");
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

        jDC_Nacimiento.setDateFormatString("dd/mm/yyyy");

        jBttn_Cancelar.setText("Cancelar");

        jBttn_Continuar.setText("Continuar");
        jBttn_Continuar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBttn_ContinuarActionPerformed(evt);
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
                    .addComponent(jBttn_Continuar, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addContainerGap(229, Short.MAX_VALUE))
            .addGroup(jP_DatosPrincipalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jP_DatosPrincipalesLayout.createSequentialGroup()
                    .addGap(91, 91, 91)
                    .addComponent(jBttn_Continuar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(450, Short.MAX_VALUE)))
        );

        jP_AltCard.add(jP_DatosPrincipales, "card2");

        jBttn_Continuar_2.setText("Continuar");
        jBttn_Continuar_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBttn_Continuar_2ActionPerformed(evt);
            }
        });

        jBttn_Cancelar_2.setText("Cancelar");

        jL_Semestre.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jL_Semestre.setText("Semestre");

        jL_Materias.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jL_Materias.setText("Materias");

        jMaterias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jMaterias);

        jL_Promedio.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jL_Promedio.setText("Promedio General");

        jTxt_Semestre.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jTxt_Promedio.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

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
                                    .addComponent(jBttn_Continuar_2, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(105, 105, 105))))))
        );
        jP_MateriasPromLayout.setVerticalGroup(
            jP_MateriasPromLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_MateriasPromLayout.createSequentialGroup()
                .addContainerGap(83, Short.MAX_VALUE)
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 140, Short.MAX_VALUE)
                        .addComponent(jBttn_Continuar_2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addComponent(jBttn_Cancelar_2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62))
                    .addGroup(jP_MateriasPromLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jP_AltCard.add(jP_MateriasProm, "card3");

        jLbl_CuentaExtras.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLbl_CuentaExtras.setText("Cuenta y Extras");
        jLbl_CuentaExtras.setToolTipText("");

        jLbl_Contra.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLbl_Contra.setText("Contraseña:");

        jLbl_Estatus.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLbl_Estatus.setText("Estatus:");

        jCb_Estatus.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLbl_Telefono.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLbl_Telefono.setText("Telefono:");

        jLbl_localidad.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLbl_localidad.setText("Localidad:");

        jTxt_telefono.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jCb_Localidad.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jB_Cancelar3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jB_Cancelar3.setText("Cancelar");

        jB_Guardar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jB_Guardar.setText("Guardar");
        jB_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_GuardarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jP_CuentasExtrLayout = new javax.swing.GroupLayout(jP_CuentasExtr);
        jP_CuentasExtr.setLayout(jP_CuentasExtrLayout);
        jP_CuentasExtrLayout.setHorizontalGroup(
            jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_CuentasExtrLayout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLbl_CuentaExtras)
                    .addGroup(jP_CuentasExtrLayout.createSequentialGroup()
                        .addGroup(jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jCb_Localidad, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jP_CuentasExtrLayout.createSequentialGroup()
                                .addComponent(jLbl_Contra)
                                .addGap(237, 237, 237))
                            .addComponent(jLbl_localidad, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTxt_telefono, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLbl_Telefono, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(52, 52, 52)
                        .addGroup(jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jP_CuentasExtrLayout.createSequentialGroup()
                                .addComponent(jLbl_Estatus)
                                .addGap(128, 128, 128)
                                .addGroup(jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jB_Guardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jB_Cancelar3, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)))
                            .addComponent(jCb_Estatus, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jTxt_contraseña, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(127, Short.MAX_VALUE))
        );
        jP_CuentasExtrLayout.setVerticalGroup(
            jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_CuentasExtrLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLbl_CuentaExtras)
                .addGap(18, 18, 18)
                .addComponent(jLbl_Contra)
                .addGap(18, 18, 18)
                .addGroup(jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTxt_contraseña, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jB_Cancelar3, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jB_Guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jP_CuentasExtrLayout.createSequentialGroup()
                        .addComponent(jLbl_Telefono)
                        .addGap(18, 18, 18)
                        .addComponent(jTxt_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLbl_localidad)
                    .addComponent(jLbl_Estatus))
                .addGap(18, 18, 18)
                .addGroup(jP_CuentasExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCb_Localidad, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCb_Estatus, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(231, Short.MAX_VALUE))
        );

        jP_AltCard.add(jP_CuentasExtr, "card4");

        jP_Alta.add(jP_AltCard, java.awt.BorderLayout.PAGE_START);

        jp_Contenedor.add(jP_Alta, "card2");

        jP_Consulta.setLayout(new java.awt.BorderLayout());

        jP_ConsultaC.setLayout(new java.awt.CardLayout());

        jLb_Consulta_buscar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLb_Consulta_buscar.setText("BUSCAR");

        jTxt_Consulta_Buscar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTxt_Consulta_Buscar.setText("Ej. Luis Estrada");

        jLb_CONSULTA_CARR.setText("CARRERA DEL ALUMNO");

        jCb_CONSULTA_CARR.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLb_CONSULTA_ORDENAR.setText("ORDENAR POR:");

        jCb_Consulta_Ordenar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS", "SOBRESALIENTE", "APROBADO", "EN RIESGO", "REPORBADO" }));

        jLb_Consulta_materias.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLb_Consulta_materias.setText("MATERIAS");

        jCb_Consulta_Materias.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jCb_Consulta_Materias.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLb_CONSULTA_PROM.setText("PROMEDIO(0 A 100)");

        jLb_Consulta_A.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLb_Consulta_A.setText("A");

        jLb_Consulta_Sit_Ac.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLb_Consulta_Sit_Ac.setText("SITUACIÓN ACADÉMICA");

        jB_Consulta_Todos.setText("TODOS");

        jB_Consulta_Sobresal.setText("SOBRESALIENTE");

        jB_Cosnulta_Aprob.setText("APROBADO");

        jB_Consulta_Riesg.setText("EN RIESGO");

        jB_Consulta_Rep.setText("REPROBADO");

        javax.swing.GroupLayout jP_CONSULTA_COMPLayout = new javax.swing.GroupLayout(jP_CONSULTA_COMP);
        jP_CONSULTA_COMP.setLayout(jP_CONSULTA_COMPLayout);
        jP_CONSULTA_COMPLayout.setHorizontalGroup(
            jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_CONSULTA_COMPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jP_CONSULTA_COMPLayout.createSequentialGroup()
                        .addGroup(jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jCb_Consulta_Materias, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTxt_Consulta_Buscar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                                .addComponent(jLb_Consulta_buscar, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(jLb_Consulta_materias))
                        .addGap(35, 35, 35)
                        .addGroup(jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLb_CONSULTA_PROM)
                            .addGroup(jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jCb_Consulta_Ordenar, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jP_CONSULTA_COMPLayout.createSequentialGroup()
                                    .addGroup(jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jCb_CONSULTA_CARR, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLb_CONSULTA_CARR)
                                        .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jP_CONSULTA_COMPLayout.createSequentialGroup()
                                            .addGap(18, 18, 18)
                                            .addComponent(jLb_Consulta_A)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jP_CONSULTA_COMPLayout.createSequentialGroup()
                                            .addGap(39, 39, 39)
                                            .addComponent(jLb_CONSULTA_ORDENAR, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                    .addComponent(jLb_Consulta_Sit_Ac)
                    .addGroup(jP_CONSULTA_COMPLayout.createSequentialGroup()
                        .addComponent(jB_Consulta_Todos)
                        .addGap(18, 18, 18)
                        .addComponent(jB_Consulta_Sobresal)
                        .addGap(18, 18, 18)
                        .addComponent(jB_Cosnulta_Aprob)
                        .addGap(18, 18, 18)
                        .addComponent(jB_Consulta_Riesg)
                        .addGap(18, 18, 18)
                        .addComponent(jB_Consulta_Rep)))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jP_CONSULTA_COMPLayout.setVerticalGroup(
            jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_CONSULTA_COMPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLb_Consulta_buscar)
                    .addComponent(jLb_CONSULTA_CARR)
                    .addComponent(jLb_CONSULTA_ORDENAR))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTxt_Consulta_Buscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCb_CONSULTA_CARR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCb_Consulta_Ordenar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLb_Consulta_materias)
                    .addComponent(jLb_CONSULTA_PROM))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCb_Consulta_Materias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLb_Consulta_A)
                    .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLb_Consulta_Sit_Ac)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jP_CONSULTA_COMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jB_Consulta_Todos)
                    .addComponent(jB_Consulta_Sobresal)
                    .addComponent(jB_Cosnulta_Aprob)
                    .addComponent(jB_Consulta_Riesg)
                    .addComponent(jB_Consulta_Rep))
                .addContainerGap(284, Short.MAX_VALUE))
        );

        jP_ConsultaC.add(jP_CONSULTA_COMP, "card2");

        jP_Consulta.add(jP_ConsultaC, java.awt.BorderLayout.CENTER);

        jp_Contenedor.add(jP_Consulta, "card3");

        jP_Edicion.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel1.setText("EDICION");

        jTBuscar.setText("1.Buscar");
        jTBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
                .addContainerGap(244, Short.MAX_VALUE))
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

        txtNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreActionPerformed(evt);
            }
        });
        jP_EdDatos.add(txtNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 110, 280, -1));
        jP_EdDatos.add(txtApPat, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 160, 280, -1));
        jP_EdDatos.add(txtApMaterno, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 210, 280, -1));

        txtCorreo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCorreoActionPerformed(evt);
            }
        });
        jP_EdDatos.add(txtCorreo, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 250, 280, -1));

        cmbCarrera.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jP_EdDatos.add(cmbCarrera, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 300, 280, -1));
        jP_EdDatos.add(txtPromedio, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 350, 280, -1));
        jP_EdDatos.add(dcFechaNac, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 400, 280, -1));

        jLabel22.setText("Materias");
        jP_EdDatos.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 450, -1, -1));

        lstMaterias.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(lstMaterias);

        jP_EdDatos.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 480, 470, 100));

        btnAgregarMat.setText("Agregar Materia");
        jP_EdDatos.add(btnAgregarMat, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 600, -1, -1));

        btnQuitarMat.setText("Quitar Materia");
        jP_EdDatos.add(btnQuitarMat, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 600, -1, -1));

        btnAtras2.setText("Atras");
        jP_EdDatos.add(btnAtras2, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 650, -1, -1));

        btnRestablecer.setText("Restablecer");
        jP_EdDatos.add(btnRestablecer, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 650, -1, -1));

        btnCancelar2.setText("Cancelar");
        jP_EdDatos.add(btnCancelar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 650, -1, -1));

        btnContinuar2.setText("Continuar");
        btnContinuar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContinuar2ActionPerformed(evt);
            }
        });
        jP_EdDatos.add(btnContinuar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 650, -1, -1));

        jP_EdCard.add(jP_EdDatos, "datos");

        jLabel23.setText("Confirmar cambios");

        lblResumen.setText("Resumen del tramite");

        tblCambios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tblCambios);

        chkConfirmo.setText("Revise los cambios y deseo guardarlos");
        chkConfirmo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkConfirmoActionPerformed(evt);
            }
        });

        btnAtras3.setText("Atras");
        btnAtras3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtras3ActionPerformed(evt);
            }
        });

        btnGuardar.setText("Guardar cambios");
        btnGuardar.setEnabled(false);
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 565, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkConfirmo, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jP_EdConfirmarLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnAtras3)
                        .addGap(58, 58, 58)
                        .addComponent(btnGuardar)))
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
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jP_EdConfirmarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jP_EdConfirmarLayout.createSequentialGroup()
                        .addGap(98, 98, 98)
                        .addComponent(chkConfirmo)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jP_EdConfirmarLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jP_EdConfirmarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnGuardar)
                            .addComponent(btnAtras3))
                        .addGap(90, 90, 90))))
        );

        jP_EdCard.add(jP_EdConfirmar, "confirmar");

        jP_EdBuscar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblNoControl.setText("No. de control");
        jP_EdBuscar.add(lblNoControl, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, -1, -1));

        txtNoControl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNoControlActionPerformed(evt);
            }
        });
        jP_EdBuscar.add(txtNoControl, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 110, 240, 20));

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
                    .addGroup(jP_ResultadoLayout.createSequentialGroup()
                        .addComponent(jLbl_Semestre, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(89, 89, 89)
                        .addComponent(jLbl_Promedio, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLbl_Fecha))
                .addGap(17, 17, 17))
        );
        jP_ResultadoLayout.setVerticalGroup(
            jP_ResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_ResultadoLayout.createSequentialGroup()
                .addGap(34, 34, 34)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jP_EdBuscar.add(jP_Resultado, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 540, 130));

        jBContinuar1.setText("Continuar");
        jBContinuar1.setEnabled(false);
        jBContinuar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBContinuar1ActionPerformed(evt);
            }
        });
        jP_EdBuscar.add(jBContinuar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 320, -1, 20));

        jBCancelar1.setText("Cancelar");
        jBCancelar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelar1ActionPerformed(evt);
            }
        });
        jP_EdBuscar.add(jBCancelar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 320, -1, -1));

        jP_EdCard.add(jP_EdBuscar, "buscar");

        jP_Edicion.add(jP_EdCard, java.awt.BorderLayout.CENTER);

        jp_Contenedor.add(jP_Edicion, "card4");

        jP_Baja1.setBackground(new java.awt.Color(239, 232, 217));
        jP_Baja1.setForeground(new java.awt.Color(239, 232, 217));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 3, 96)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(44, 160, 237));
        jLabel13.setText("BAJA");

        jPanel1.setBackground(new java.awt.Color(44, 160, 237));

        jPanel2.setBackground(new java.awt.Color(255, 252, 243));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(27, 21, 17));
        jLabel3.setText("BAJA DE ALUMNO");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(23, 100, 165));
        jLabel4.setText("No. De Control");

        jTextField1.setBackground(new java.awt.Color(255, 252, 243));
        jTextField1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        jButton1.setBackground(new java.awt.Color(255, 100, 24));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setText("BUSCAR");
        jButton1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        jTextField2.setBackground(new java.awt.Color(44, 160, 237));
        jTextField2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField2.setText("ALUMNOENCONTRADO/NOENCONTRADO");
        jTextField2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        jPanel4.setBackground(new java.awt.Color(238, 230, 214));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        jPanel5.setBackground(new java.awt.Color(0, 0, 0));

        jLabel5.setText(".");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTextField3.setBackground(new java.awt.Color(255, 252, 243));
        jTextField3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField3.setText("NOMBRE");
        jTextField3.setBorder(null);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(23, 100, 165));
        jLabel6.setText("PROMEDIO");

        jTextField4.setBackground(new java.awt.Color(255, 252, 243));
        jTextField4.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jTextField4.setForeground(new java.awt.Color(23, 100, 165));
        jTextField4.setBorder(null);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(23, 100, 165));
        jLabel7.setText("CARRERA");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(23, 100, 165));
        jLabel8.setText("SEMESTRE");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(23, 100, 165));
        jLabel9.setText("CORREO");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(23, 100, 165));
        jLabel10.setText("FECHA DE INGRESO");

        jSeparator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jSeparator2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jSeparator4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jSeparator5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextField5.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N

        jTextField6.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N

        jTextField7.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N

        jTextField8.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jSeparator2)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addComponent(jLabel7)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addComponent(jLabel8)
                                                .addGap(105, 105, 105)
                                                .addComponent(jTextField5))
                                            .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addComponent(jLabel9)
                                                .addGap(111, 111, 111)
                                                .addComponent(jTextField7))
                                            .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel10)
                            .addGap(69, 69, 69)
                            .addComponent(jTextField8))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 14, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 14, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 14, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 14, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton2.setBackground(new java.awt.Color(255, 100, 24));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setText("DAR BAJA");
        jButton2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        jButton3.setBackground(new java.awt.Color(239, 232, 217));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton3.setText("CANCELAR");
        jButton3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        jButton4.setBackground(new java.awt.Color(255, 100, 24));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton4.setText("LIMPIAR");
        jButton4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(23, 100, 165));
        jLabel11.setText("Fecha de  Baja");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 8)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(23, 100, 165));
        jLabel12.setText("Motivo de la Baja");

        jComboBox1.setBackground(new java.awt.Color(255, 252, 243));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jTextField9.setBackground(new java.awt.Color(255, 252, 243));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton4)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(57, 57, 57)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 291, Short.MAX_VALUE))
                            .addComponent(jTextField9))))
                .addGap(16, 16, 16))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jP_Baja1Layout = new javax.swing.GroupLayout(jP_Baja1);
        jP_Baja1.setLayout(jP_Baja1Layout);
        jP_Baja1Layout.setHorizontalGroup(
            jP_Baja1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_Baja1Layout.createSequentialGroup()
                .addGroup(jP_Baja1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jP_Baja1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel13))
                    .addGroup(jP_Baja1Layout.createSequentialGroup()
                        .addGap(128, 128, 128)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(228, Short.MAX_VALUE))
        );
        jP_Baja1Layout.setVerticalGroup(
            jP_Baja1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jP_Baja1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jP_BajaLayout = new javax.swing.GroupLayout(jP_Baja);
        jP_Baja.setLayout(jP_BajaLayout);
        jP_BajaLayout.setHorizontalGroup(
            jP_BajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 892, Short.MAX_VALUE)
            .addGroup(jP_BajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jP_BajaLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jP_Baja1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jP_BajaLayout.setVerticalGroup(
            jP_BajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 467, Short.MAX_VALUE)
            .addGroup(jP_BajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jP_BajaLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jP_Baja1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jp_Contenedor.add(jP_Baja, "card5");

        jBttn_Regresar.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jBttn_Regresar.setText("REGRESAR");
        jBttn_Regresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBttn_RegresarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jL_TituloGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jBttn_Consulta, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBttn_Baja, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBttn_Edicion, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBttn_Regresar, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(130, 130, 130)
                        .addComponent(jp_Contenedor, javax.swing.GroupLayout.PREFERRED_SIZE, 892, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(87, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(37, 37, 37)
                    .addComponent(jBttn_Alta, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(1109, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jL_TituloGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(123, 123, 123)
                        .addComponent(jBttn_Consulta, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(jBttn_Edicion, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(jBttn_Baja, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(jBttn_Regresar, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                        .addGap(170, 170, 170))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jp_Contenedor, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(114, 114, 114)
                    .addComponent(jBttn_Alta, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(580, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBttn_RegresarActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jBttn_RegresarActionPerformed
    {//GEN-HEADEREND:event_jBttn_RegresarActionPerformed
        // TODO add your handling code here:
        new VtnHome(controllerAdministracion).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jBttn_RegresarActionPerformed

    private void jBttn_DatosPrincipalesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jBttn_DatosPrincipalesActionPerformed
    {//GEN-HEADEREND:event_jBttn_DatosPrincipalesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jBttn_DatosPrincipalesActionPerformed

    private void jBttn_AltaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jBttn_AltaActionPerformed
    {//GEN-HEADEREND:event_jBttn_AltaActionPerformed
        java.awt.CardLayout cl = (java.awt.CardLayout) jp_Contenedor.getLayout();
        cl.show(jp_Contenedor, "card2");
        Estilos.seleccionar(jBttn_Alta);
    }//GEN-LAST:event_jBttn_AltaActionPerformed

    private void jBttn_ContinuarActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jBttn_ContinuarActionPerformed
    {//GEN-HEADEREND:event_jBttn_ContinuarActionPerformed
        // TODO add your handling code here:
        String nombre = jTxt_nombre.getText().trim();
        String apellidoPat = jTxt_ApellidoPat.getText().trim();
        String apellidoMat = jTxt_ApellidoMat.getText().trim();
        Date fechaNacimiento = jDC_Nacimiento.getDate();
        if (!validaciones.ValidadorAlumno.nombreValido(nombre))
        {
            JOptionPane.showMessageDialog(this, "El formato del nombre no es valido. Ingerese un nombre válido", "Error de campo", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!validaciones.ValidadorAlumno.nombreValido(apellidoPat))
        {
            JOptionPane.showMessageDialog(this, "El formato del apellido paterno no es valido. Ingerese un apellido válido", "Error de campo", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!validaciones.ValidadorAlumno.nombreValido(apellidoMat))
        {
            JOptionPane.showMessageDialog(this, "El formato del apellido materno no es valido. Ingerese un apellido válido", "Error de campo", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (validaciones.ValidadorAlumno.fechaFutura(fechaNacimiento))
        {
            JOptionPane.showMessageDialog(this, "No puede ingresar una fecha futura. Ingerese una fecha válida", "Error de campo", JOptionPane.ERROR_MESSAGE);
            return;

        }
        /*                if(validaciones.ValidadorAlumno.fechaPasada(fechaNacimiento))
        {
            JOptionPane.showMessageDialog(this, "No puede ingresar una fecha anterios a 1950. Ingerese una fecha válida", "Error de campo", JOptionPane.ERROR_MESSAGE);
            return;
            
        }
        String nombreCompleto = nombre + apellidoPat + apellidoMat;

         */
    }//GEN-LAST:event_jBttn_ContinuarActionPerformed

    private void jBttn_EdicionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBttn_EdicionActionPerformed
        // TODO add your handling code here:
        java.awt.CardLayout cl = (java.awt.CardLayout) jp_Contenedor.getLayout();
        cl.show(jp_Contenedor, "card4");
        Estilos.seleccionar(jBttn_Edicion);
    }//GEN-LAST:event_jBttn_EdicionActionPerformed

    private void jBttn_BajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBttn_BajaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jBttn_BajaActionPerformed

    private void jBttn_ConsultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBttn_ConsultaActionPerformed
        // TODO add your handling code here:
        java.awt.CardLayout cl = (java.awt.CardLayout) jp_Contenedor.getLayout();
        cl.show(jp_Contenedor, "card3");
        Estilos.seleccionar(jBttn_Consulta);
    }//GEN-LAST:event_jBttn_ConsultaActionPerformed

    private void jBttn_Continuar_2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBttn_Continuar_2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jBttn_Continuar_2ActionPerformed

    private void jB_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_GuardarActionPerformed
        // TODO add your handling code here:
        String contraseña = jTxt_contraseña.getText().trim();
        String telefono = jTxt_telefono.getText().trim();
        String estatus = jCb_Estatus.getSelectedItem().toString();
        String localidad = jCb_Localidad.getSelectedItem().toString();

        if (validaciones.ValidadorAlumno.esVacio(contraseña))
        {
            JOptionPane.showMessageDialog(this,
                    "La contraseña no puede estar vacio",
                    "Campo requerido",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (validaciones.ValidadorAlumno.esVacio(telefono))
        {
            JOptionPane.showMessageDialog(this,
                    "El telefono no puede estar vacio",
                    "Campo requerido",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (validaciones.ValidadorAlumno.esVacio(estatus))
        {
            JOptionPane.showMessageDialog(this,
                    "Debes elegir un estatus",
                    "Campo requerido",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (validaciones.ValidadorAlumno.esVacio(localidad))
        {
            JOptionPane.showMessageDialog(this,
                    "Debes elegir una localidad",
                    "Campo requerido",
                    JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jB_GuardarActionPerformed

    private void jTBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTBuscarActionPerformed

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtCorreoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCorreoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCorreoActionPerformed

    private void btnContinuar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinuar2ActionPerformed
        // TODO add your handling code here:
        if (!validarFormulario())
        {
            return;
        }
        cargarTablaCambios();
        irA("confirmar");
    }//GEN-LAST:event_btnContinuar2ActionPerformed

    private void chkConfirmoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkConfirmoActionPerformed
        // TODO add your handling code here:
        btnGuardar.setEnabled(chkConfirmo.isSelected() && tblCambios.getRowCount() > 0);
    }//GEN-LAST:event_chkConfirmoActionPerformed

    private void btnAtras3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtras3ActionPerformed
        // TODO add your handling code here:
        irA("datos");
    }//GEN-LAST:event_btnAtras3ActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
        Alumno nuevo = new Alumno(
                alumnoOriginal.getNo_control(),
                txtNombre.getText().trim(),
                txtCorreo.getText().trim(),
                (String) cmbCarrera.getSelectedItem(),
                Double.parseDouble(txtPromedio.getText().trim()),
                dcFechaNac.getDate(),
                lstMaterias.getSelectedValuesList());

        if (controllerAdministracion.actualizarAlumno(nuevo))
        {
            JOptionPane.showMessageDialog(this, "Alumno actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            reiniciar();
        } else
        {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el alumno.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void txtNoControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNoControlActionPerformed
        // TODO add your handling code here:
        btnBuscar.doClick();
    }//GEN-LAST:event_txtNoControlActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        // TODO add your handling code here:
        String nc = txtNoControl.getText().trim();

        // 1. Que no esté vacío
        if (nc.isEmpty())
        {
            lblEstado.setText("Escribe un número de control");
            return;
        }

        // 2. Que solo tenga números (sin espacios, letras ni símbolos)
        if (!nc.matches("\\d+"))
        {
            limpiarResultado();
            alumnoOriginal = null;
            jBContinuar1.setEnabled(false);
            lblEstado.setText("El No. de control solo debe tener números, sin espacios ni símbolos");
            return;
        }

        // 3. Buscar al alumno
        Alumno a = controllerAdministracion.buscarAlumnoPorNoControl(nc);
        if (a == null)
        {
            limpiarResultado();
            alumnoOriginal = null;
            jBContinuar1.setEnabled(false);
            lblEstado.setText("Alumno no encontrado");
            return;
        }

        // 4. Mostrar el resultado
        alumnoOriginal = a;
        lblEstado.setText("✓ Alumno encontrado");
        jLbl_Nombre.setText(a.getNombre());
        jLbl_Carrera.setText(a.getCarerra());
        jLbl_Correo.setText(a.getCorreo());
        jLbl_Promedio.setText(String.valueOf(a.getPromedio()));
        jLbl_Fecha.setText(new SimpleDateFormat("dd/MM/yyyy").format(a.getFechaNaciomiento()));
        jP_Resultado.setVisible(true);
        jBContinuar1.setEnabled(true);
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void jBContinuar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBContinuar1ActionPerformed
        llenarFormulario(alumnoOriginal);
        irA("datos");
    }//GEN-LAST:event_jBContinuar1ActionPerformed

    private void jBCancelar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelar1ActionPerformed
        // TODO add your handling code here:
        reiniciar();
    }//GEN-LAST:event_jBCancelar1ActionPerformed

    // Edicion metodos propios
    private void conScroll(JPanel card, String nombre)
    {
        jP_EdCard.remove(card);
        card.setVisible(true);   // el CardLayout lo había ocultado
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
        pintarPestanas();
    }

    private void pintarPestanas()
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

    private void aplicarEstilos()
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

        // Encabezado: solo las pestañas, a la izquierda
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

        // Etiquetas
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

        // Tarjeta del alumno encontrado
        jLbl_Nombre.setFont(Estilos.fuente(Font.PLAIN, 28));
        for (JLabel l : new JLabel[]
        {
            jLbl_Carrera, jLbl_Semestre, jLbl_Correo, jLbl_Fecha
        })
        {
            l.setFont(Estilos.fuente(Font.BOLD, 16));
        }
        jLbl_Promedio.setFont(Estilos.fuente(Font.PLAIN, 36));
        jLbl_Promedio.setForeground(Estilos.AZUL_OSCURO);

        // Campos
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
        jScrollPane2.setBorder(Estilos.borde(2));   // lista de materias

        // Paso 3
        Estilos.estilizarTabla(tblCambios);
        jScrollPane3.setBorder(Estilos.borde(2));   // tabla de cambios
        chkConfirmo.setOpaque(false);
        chkConfirmo.setFont(Estilos.fuente(Font.BOLD, 16));
        chkConfirmo.setForeground(Estilos.NEGRO);
        chkConfirmo.setText("ENTIENDO QUE ESTA ACCIÓN MODIFICA LOS DATOS DEL ALUMNO");

        // Botones
        btnBuscar.setText("BUSCAR");
        jBCancelar1.setText("CANCELAR");
        jBContinuar1.setText("CONTINUAR");
        btnAtras2.setText("ATRÁS");
        btnRestablecer.setText("RESTABLECER");
        btnCancelar2.setText("CANCELAR");
        btnContinuar2.setText("CONTINUAR");
        btnAtras3.setText("ATRÁS");
        btnGuardar.setText("GUARDAR CAMBIOS");

        JButton[] normales =
        {
            btnBuscar, jBCancelar1, btnAtras2, btnRestablecer, btnCancelar2, btnAtras3
        };
        JButton[] principales =
        {
            jBContinuar1, btnContinuar2, btnGuardar
        };
        for (JButton b : normales)
        {
            Estilos.estilizarBoton(b);
            b.setFont(Estilos.fuente(Font.PLAIN, 18));
        }
        for (JButton b : principales)
        {
            Estilos.estilizarBotonPrincipal(b);
            b.setFont(Estilos.fuente(Font.PLAIN, 18));
        }

        // Posiciones
        // Paso 1
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

        pintarPestanas();
    }

    private void limpiarResultado()
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

    private void reiniciar()
    {
        txtNoControl.setText("");
        alumnoOriginal = null;
        limpiarResultado();
        jBContinuar1.setEnabled(false);
        irA("buscar");
    }

    private void llenarFormulario(Alumno a)
    {
        txtNoControl2.setText(a.getNo_control());
        txtNoControl2.setEditable(false);
        txtNombre.setText(a.getNombre());
        txtCorreo.setText(a.getCorreo());
        cmbCarrera.setSelectedItem(a.getCarerra());
        txtPromedio.setText(String.valueOf(a.getPromedio()));
        dcFechaNac.setDate(a.getFechaNaciomiento());
        seleccionarMaterias(a.getMaterias());
    }

    private void seleccionarMaterias(List<String> materias)
    {
        List<Integer> idx = new ArrayList<>();
        ListModel<String> m = lstMaterias.getModel();
        for (int i = 0; i < m.getSize(); i++)
        {
            if (materias.contains(m.getElementAt(i)))
            {
                idx.add(i);
            }
        }
        lstMaterias.setSelectedIndices(idx.stream().mapToInt(Integer::intValue).toArray());
    }

    private void error(String titulo, String msg)
    {
        JOptionPane.showMessageDialog(this, msg, titulo, JOptionPane.ERROR_MESSAGE);
    }

    private boolean fechaAnterior1950(Date f)
    {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(f);
        return c.get(java.util.Calendar.YEAR) < 1950;
    }

    private boolean validarFormulario()
    {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String promedio = txtPromedio.getText().trim();
        Date fecha = dcFechaNac.getDate();

        if (ValidadorAlumno.esVacio(nombre))
        {
            error("Campo requerido", "El nombre es OBLIGATORIO");
            return false;
        }
        if (!ValidadorAlumno.nombreValido(nombre))
        {
            error("Error de validación", "El nombre solo puede contener letras y espacios.");
            return false;
        }
        if (ValidadorAlumno.esVacio(correo))
        {
            error("Campo requerido", "El correo es OBLIGATORIO");
            return false;
        }
        if (!ValidadorAlumno.correoValido(correo))
        {
            error("Error de validación", "El formato del correo no es válido. Ejemplo: usuario@dominio.com");
            txtCorreo.requestFocus();
            return false;
        }
        if (ValidadorAlumno.seleccionCb(cmbCarrera.getSelectedIndex()))
        {
            error("Campo requerido", "La carrera es OBLIGATORIA");
            return false;
        }
        if (ValidadorAlumno.promedioVacio(promedio))
        {
            error("Campo requerido", "No puede quedar vacío el Promedio");
            return false;
        }
        if (!ValidadorAlumno.promedioValido(promedio))
        {
            error("Error de validación", "El promedio debe ser numérico");
            return false;
        }
        if (!ValidadorAlumno.promedioValid(Double.parseDouble(promedio)))
        {
            error("Error en el rango", "El promedio debe estar en un rango de 0 a 100");
            return false;
        }
        if (ValidadorAlumno.fechaVacia(fecha))
        {
            error("Campo requerido", "Debe ingresar la fecha de nacimiento");
            return false;
        }
        if (ValidadorAlumno.fechaFutura(fecha) || fechaAnterior1950(fecha))
        {
            error("Error en fecha", "La fecha no puede ser futura ni anterior a 1950");
            return false;
        }
        if (ValidadorAlumno.materiasVacias(lstMaterias.getSelectedValuesList()))
        {
            error("Campo requerido", "Debe seleccionar al menos una materia");
            return false;
        }
        return true;
    }

    private void agregarSiCambio(DefaultTableModel m, String campo, String antes, String despues)
    {
        if (!java.util.Objects.equals(antes, despues))
        {
            m.addRow(new Object[]
            {
                campo, antes, despues
            });
        }
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
        Alumno a = alumnoOriginal;
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");

        agregarSiCambio(m, "Nombre", a.getNombre(), txtNombre.getText().trim());
        agregarSiCambio(m, "Correo", a.getCorreo(), txtCorreo.getText().trim());
        agregarSiCambio(m, "Carrera", a.getCarerra(), (String) cmbCarrera.getSelectedItem());
        agregarSiCambio(m, "Promedio", String.valueOf(a.getPromedio()),
                String.valueOf(Double.parseDouble(txtPromedio.getText().trim())));
        agregarSiCambio(m, "Fecha nac.", f.format(a.getFechaNaciomiento()), f.format(dcFechaNac.getDate()));
        agregarSiCambio(m, "Materias", String.join(", ", a.getMaterias()),
                String.join(", ", lstMaterias.getSelectedValuesList()));

        tblCambios.setModel(m);
        lblResumen.setText(m.getRowCount() == 0 ? "Sin cambios" : m.getRowCount() + " campo(s) modificado(s)");
        chkConfirmo.setSelected(false);
        btnGuardar.setEnabled(false);
    }

    public void cargarTabla()
    {
        String[] columnas
                =
                {
                    "Materia",
                    "Horario",
                    "Agregar Mat."
                };
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        //declaro un n de tipo alumno de la carpeta model
        //me la vole UwU

        String[] mat1
                =
                {
                    "Top. Avan. Prog.",
                    "Mar/Jue 7 am - 9 am",
                    "Agregar"
                };
        String[] mat2
                =
                {
                    "Simulacion",
                    "Mar/Jue 9 am - 11 am",
                    "Agregar"
                };
        String[] mat3
                =
                {
                    "Metodos Numericos",
                    "Mie/Vie 1 pm - 3 pm",
                    "Agregar"
                };
        String[] mat4
                =
                {
                    "Prolog",
                    "Mie/Vie 11 am - 1 pm",
                    "Agregar"
                };
        String[] mat5
                =
                {
                    "Arq.Computadoras.",
                    "Lun/Mie/Vie 7 am - 9 am",
                    "Agregar"
                };

        modelo.addRow(mat1);
        modelo.addRow(mat2);
        modelo.addRow(mat3);
        modelo.addRow(mat4);
        modelo.addRow(mat5);

        //Se hace que el JPanel ocupe todo el espacio de la tabla
        /* modelo.addRow(mensaje);
        

               
                modelo.addRow(fila);
         */
        //no mover las columnas de lugar
        jMaterias.getTableHeader().setReorderingAllowed(false);
        //no cambiarles la longitud
        jMaterias.getTableHeader().setResizingAllowed(false);
        jMaterias.setModel(modelo); //muestrala

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarMat;
    private javax.swing.JButton btnAtras2;
    private javax.swing.JButton btnAtras3;
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
    private javax.swing.JButton jB_Consulta_Rep;
    private javax.swing.JButton jB_Consulta_Riesg;
    private javax.swing.JButton jB_Consulta_Sobresal;
    private javax.swing.JButton jB_Consulta_Todos;
    private javax.swing.JButton jB_Cosnulta_Aprob;
    private javax.swing.JButton jB_Guardar;
    private javax.swing.JButton jBttn_Alta;
    private javax.swing.JButton jBttn_Baja;
    private javax.swing.JButton jBttn_Cancelar;
    private javax.swing.JButton jBttn_Cancelar_2;
    private javax.swing.JButton jBttn_Consulta;
    private javax.swing.JButton jBttn_Continuar;
    private javax.swing.JButton jBttn_Continuar_2;
    private javax.swing.JButton jBttn_CuentasExtr;
    private javax.swing.JButton jBttn_DatosPrincipales;
    private javax.swing.JButton jBttn_Edicion;
    private javax.swing.JButton jBttn_MateriasProm;
    private javax.swing.JButton jBttn_Regresar;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jCb_CONSULTA_CARR;
    private javax.swing.JComboBox<String> jCb_Consulta_Materias;
    private javax.swing.JComboBox<String> jCb_Consulta_Ordenar;
    private javax.swing.JComboBox<String> jCb_Estatus;
    private javax.swing.JComboBox<String> jCb_Localidad;
    private javax.swing.JComboBox<String> jComboBox1;
    private com.toedter.calendar.JDateChooser jDC_Nacimiento;
    private javax.swing.JLabel jL_ApellidoMat;
    private javax.swing.JLabel jL_ApellidoPat;
    private javax.swing.JLabel jL_Fecha;
    private javax.swing.JLabel jL_Materias;
    private javax.swing.JLabel jL_Nombres1;
    private javax.swing.JLabel jL_Promedio;
    private javax.swing.JLabel jL_Semestre;
    private javax.swing.JLabel jL_TituloGeneral;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLb_CONSULTA_CARR;
    private javax.swing.JLabel jLb_CONSULTA_ORDENAR;
    private javax.swing.JLabel jLb_CONSULTA_PROM;
    private javax.swing.JLabel jLb_Consulta_A;
    private javax.swing.JLabel jLb_Consulta_Sit_Ac;
    private javax.swing.JLabel jLb_Consulta_buscar;
    private javax.swing.JLabel jLb_Consulta_materias;
    private javax.swing.JLabel jLbl_Carrera;
    private javax.swing.JLabel jLbl_Contra;
    private javax.swing.JLabel jLbl_Correo;
    private javax.swing.JLabel jLbl_CuentaExtras;
    private javax.swing.JLabel jLbl_Estatus;
    private javax.swing.JLabel jLbl_Fecha;
    private javax.swing.JLabel jLbl_Nombre;
    private javax.swing.JLabel jLbl_Promedio;
    private javax.swing.JLabel jLbl_Semestre;
    private javax.swing.JLabel jLbl_Telefono;
    private javax.swing.JLabel jLbl_localidad;
    private javax.swing.JTable jMaterias;
    private javax.swing.JPanel jP_AltCard;
    private javax.swing.JPanel jP_Alta;
    private javax.swing.JPanel jP_Baja;
    private javax.swing.JPanel jP_Baja1;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JToggleButton jTBuscar;
    private javax.swing.JToggleButton jTConfirmar;
    private javax.swing.JToggleButton jTDatos;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JTextField jTxt_ApellidoMat;
    private javax.swing.JTextField jTxt_ApellidoPat;
    private javax.swing.JTextField jTxt_Consulta_Buscar;
    private javax.swing.JTextField jTxt_Promedio;
    private javax.swing.JTextField jTxt_Semestre;
    private javax.swing.JTextField jTxt_contraseña;
    private javax.swing.JTextField jTxt_nombre;
    private javax.swing.JTextField jTxt_telefono;
    private javax.swing.JPanel jp_Contenedor;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblNoControl;
    private javax.swing.JLabel lblResumen;
    private javax.swing.JList<String> lstMaterias;
    private javax.swing.JTable tblCambios;
    private javax.swing.JTextField txtApMaterno;
    private javax.swing.JTextField txtApPat;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextField txtNoControl;
    private javax.swing.JTextField txtNoControl2;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPromedio;
    // End of variables declaration//GEN-END:variables
}
