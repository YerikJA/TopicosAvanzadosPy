/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package views;

import controller.AlumnoController;
import controller.CatalogoController;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;
import model.Alumno;
import validaciones.ValidadorAlumno;
import java.util.Date;

import java.awt.Font;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author israe
 */
public class PanelEdicion extends javax.swing.JPanel {

    /**
     * Creates new form PanelEdicion
     */
    // ===== 1. CAMPOS =====
    private final AlumnoController controller;
    private Alumno alumnoOriginal;
    private String pasoActual = "buscar";

    public PanelEdicion(AlumnoController controller) {
        this.controller = controller;
        initComponents();

        buttonGroup1.add(jTBuscar);
        buttonGroup1.add(jTDatos);
        buttonGroup1.add(jTConfirmar);
        java.awt.event.ActionListener soloIndicador = e -> irA(pasoActual);
        jTBuscar.addActionListener(soloIndicador);
        jTDatos.addActionListener(soloIndicador);
        jTConfirmar.addActionListener(soloIndicador);
        CatalogoController catalogo = new CatalogoController();
        cmbCarrera.setModel(new javax.swing.DefaultComboBoxModel<>(
                catalogo.obtenerCarreras().toArray(new String[0])));
        lstMaterias.setListData(catalogo.obtenerMaterias().toArray(new String[0]));
        jLabel4.setVisible(false);
        txtApPat.setVisible(false);
        jLabel5.setVisible(false);
        txtApMaterno.setVisible(false);
        btnAgregarMat.setVisible(false);
        btnQuitarMat.setVisible(false);
        // Botones del paso 2 (sin necesidad de doble clic)
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
    // ===== 3. MÉTODOS PROPIOS =====

     private void conScroll(JPanel card, String nombre) {
        jP_EdCard.remove(card);
        card.setVisible(true);   // el CardLayout lo había ocultado
        javax.swing.JScrollPane sp = new javax.swing.JScrollPane(card);
        sp.setBorder(null);
        sp.getViewport().setBackground(Estilos.CREMA_CLARO);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        jP_EdCard.add(sp, nombre);
    }
    private void irA(String paso) {
        pasoActual = paso;
        ((java.awt.CardLayout) jP_EdCard.getLayout()).show(jP_EdCard, paso);
        switch (paso) {
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

    // Colorea las pestañas según cuál está activa
    private void pintarPestanas() {
        for (AbstractButton t : new AbstractButton[]{jTBuscar, jTDatos, jTConfirmar}) {
            boolean activa = t.isSelected();
            t.setBackground(activa ? Estilos.NEGRO : Estilos.AZUL_OSCURO);
            t.setForeground(activa ? Estilos.NARANJA : java.awt.Color.WHITE);
        }
    }

    // Reposiciona un componente en un panel de layout absoluto
    private void poner(JPanel panel, java.awt.Component c, int x, int y, int w, int h) {
        panel.add(c, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
    }

    private void aplicarEstilos() {
        // ----- Fondos y contenedores -----
        setOpaque(true);
        setBackground(Estilos.CREMA);
        Estilos.estilizarFondo(jP_EncabezadoEd);
        Estilos.estilizarPanelConSombra(jP_EdCard);
        for (JPanel p : new JPanel[]{jP_EdBuscar, jP_EdDatos, jP_EdConfirmar, jP_Resultado}) {
            p.setOpaque(true);
            p.setBackground(Estilos.CREMA_CLARO);
        }
        jP_Resultado.setBorder(Estilos.borde(2));

        // ----- Encabezado: solo las pestañas, a la izquierda -----
        jLabel1.setVisible(false);
        jP_EncabezadoEd.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 8));
        jTBuscar.setText("1. BUSCAR ALUMNO");
        jTDatos.setText("2. DATOS DEL ALUMNO");
        jTConfirmar.setText("3. CONFIRMAR");
        for (AbstractButton t : new AbstractButton[]{jTBuscar, jTDatos, jTConfirmar}) {
            t.setFont(Estilos.fuente(Font.PLAIN, 20));
            t.setFocusPainted(false);
            t.setContentAreaFilled(false);
            t.setOpaque(true);
            t.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            t.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Estilos.NEGRO, 3),
                    BorderFactory.createEmptyBorder(3, 14, 3, 14)));
        }

        // ----- Etiquetas -----
        for (JLabel l : new JLabel[]{lblNoControl, jLabel2, jLabel3, jLabel4, jLabel5,
            jLabel6, jLabel7, jLabel8, jLabel9, jLabel10, lblResumen}) {
            Estilos.estilizarEtiqueta(l);
        }
        Estilos.estilizarSubtitulo(jLabel11);
        jLabel11.setText("CONFIRMAR CAMBIOS");
        lblEstado.setFont(Estilos.fuente(Font.BOLD, 16));
        lblEstado.setForeground(Estilos.AZUL_OSCURO);

        // Tarjeta del alumno encontrado (como en el diseño de Baja)
        jLbl_Nombre.setFont(Estilos.fuente(Font.PLAIN, 28));
        for (JLabel l : new JLabel[]{jLbl_Carrera, jLbl_Semestre, jLbl_Correo, jLbl_Fecha}) {
            l.setFont(Estilos.fuente(Font.BOLD, 16));
        }
        jLbl_Promedio.setFont(Estilos.fuente(Font.PLAIN, 36));
        jLbl_Promedio.setForeground(Estilos.AZUL_OSCURO);

        // ----- Campos -----
        Estilos.estilizarCampo(txtNoControl);
        Estilos.estilizarCampo(txtNoControl2);
        txtNoControl2.setBackground(Estilos.CREMA);   // se ve "bloqueado"
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
        jScrollPane1.setBorder(Estilos.borde(2));

        // ----- Tabla y casilla del paso 3 -----
        Estilos.estilizarTabla(tblCambios);
        jScrollPane2.setBorder(Estilos.borde(2));
        chkConfirmo.setOpaque(false);
        chkConfirmo.setFont(Estilos.fuente(Font.BOLD, 16));
        chkConfirmo.setForeground(Estilos.NEGRO);
        chkConfirmo.setText("ENTIENDO QUE ESTA ACCIÓN MODIFICA LOS DATOS DEL ALUMNO");

        // ----- Botones -----
        btnBuscar.setText("BUSCAR");
        jBCancelar1.setText("CANCELAR");
        jBContinuar1.setText("CONTINUAR");
        btnAtras2.setText("ATRÁS");
        btnRestablecer.setText("RESTABLECER");
        btnCancelar2.setText("CANCELAR");
        btnContinuar2.setText("CONTINUAR");
        btnAtras3.setText("ATRÁS");
        btnGuardar.setText("GUARDAR CAMBIOS");

        JButton[] normales = {btnBuscar, jBCancelar1, btnAtras2, btnRestablecer, btnCancelar2, btnAtras3};
        JButton[] principales = {jBContinuar1, btnContinuar2, btnGuardar};
        for (JButton b : normales) {
            Estilos.estilizarBoton(b);
            b.setFont(Estilos.fuente(Font.PLAIN, 18));
        }
        for (JButton b : principales) {
            Estilos.estilizarBotonPrincipal(b);
            b.setFont(Estilos.fuente(Font.PLAIN, 18));
        }

        // ----- Acomodar: los componentes
        // Paso 1
        poner(jP_EdBuscar, lblNoControl, 60, 40, -1, -1);
        poner(jP_EdBuscar, txtNoControl, 60, 70, 300, 40);
        poner(jP_EdBuscar, btnBuscar, 380, 70, -1, 40);
        poner(jP_EdBuscar, lblEstado, 60, 120, 600, -1);
        poner(jP_EdBuscar, jP_Resultado, 60, 160, 540, 130);
        poner(jP_EdBuscar, jBCancelar1, 60, 320, -1, -1);
        poner(jP_EdBuscar, jBContinuar1, 220, 320, -1, -1);
        // Paso 2 (botones sin los de agregar/quitar materia, que están ocultos)
        poner(jP_EdDatos, btnAtras2, 60, 600, -1, -1);
        poner(jP_EdDatos, btnRestablecer, 200, 600, -1, -1);
        poner(jP_EdDatos, btnCancelar2, 360, 600, -1, -1);
        poner(jP_EdDatos, btnContinuar2, 500, 600, -1, -1);

        pintarPestanas();
    }

    private void limpiarResultado() {
        jLbl_Nombre.setText("");
        jLbl_Carrera.setText("");
        jLbl_Semestre.setText("");
        jLbl_Correo.setText("");
        jLbl_Fecha.setText("");
        jLbl_Promedio.setText("");
        lblEstado.setText("");
        jP_Resultado.setVisible(false);
    }

    private void reiniciar() {
        txtNoControl.setText("");
        alumnoOriginal = null;
        limpiarResultado();
        jBContinuar1.setEnabled(false);
        irA("buscar");
    }

    private void llenarFormulario(Alumno a) {
        txtNoControl2.setText(a.getNo_control());
        txtNoControl2.setEditable(false);
        txtNombre.setText(a.getNombre());
        txtCorreo.setText(a.getCorreo());
        cmbCarrera.setSelectedItem(a.getCarerra());
        txtPromedio.setText(String.valueOf(a.getPromedio()));
        dcFechaNac.setDate(a.getFechaNaciomiento());
        seleccionarMaterias(a.getMaterias());
    }

    private void seleccionarMaterias(List<String> materias) {
        List<Integer> idx = new ArrayList<>();
        ListModel<String> m = lstMaterias.getModel();
        for (int i = 0; i < m.getSize(); i++) {
            if (materias.contains(m.getElementAt(i))) {
                idx.add(i);
            }
        }
        lstMaterias.setSelectedIndices(idx.stream().mapToInt(Integer::intValue).toArray());
    }

    private void error(String titulo, String msg) {
        JOptionPane.showMessageDialog(this, msg, titulo, JOptionPane.ERROR_MESSAGE);
    }

    private boolean fechaAnterior1950(Date f) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(f);
        return c.get(java.util.Calendar.YEAR) < 1950;
    }

    private boolean validarFormulario() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String promedio = txtPromedio.getText().trim();
        Date fecha = dcFechaNac.getDate();

        if (ValidadorAlumno.esVacio(nombre)) {
            error("Campo requerido", "El nombre es OBLIGATORIO");
            return false;
        }
        if (!ValidadorAlumno.nombreValido(nombre)) {
            error("Error de validación", "El nombre solo puede contener letras y espacios.");
            return false;
        }
        if (ValidadorAlumno.esVacio(correo)) {
            error("Campo requerido", "El correo es OBLIGATORIO");
            return false;
        }
        if (!ValidadorAlumno.correoValido(correo)) {
            error("Error de validación", "El formato del correo no es válido. Ejemplo: usuario@dominio.com");
            txtCorreo.requestFocus();
            return false;
        }
        if (ValidadorAlumno.seleccionCb(cmbCarrera.getSelectedIndex())) {
            error("Campo requerido", "La carrera es OBLIGATORIA");
            return false;
        }
        if (ValidadorAlumno.promedioVacio(promedio)) {
            error("Campo requerido", "No puede quedar vacío el Promedio");
            return false;
        }
        if (!ValidadorAlumno.promedioValido(promedio)) {
            error("Error de validación", "El promedio debe ser numérico");
            return false;
        }
        if (!ValidadorAlumno.promedioValid(Double.parseDouble(promedio))) {
            error("Error en el rango", "El promedio debe estar en un rango de 0 a 100");
            return false;
        }
        if (ValidadorAlumno.fechaVacia(fecha)) {
            error("Campo requerido", "Debe ingresar la fecha de nacimiento");
            return false;
        }
        if (ValidadorAlumno.fechaFutura(fecha) || fechaAnterior1950(fecha)) {
            error("Error en fecha", "La fecha no puede ser futura ni anterior a 1950");
            return false;
        }
        if (ValidadorAlumno.materiasVacias(lstMaterias.getSelectedValuesList())) {
            error("Campo requerido", "Debe seleccionar al menos una materia");
            return false;
        }
        return true;
    }

    private void agregarSiCambio(DefaultTableModel m, String campo, String antes, String despues) {
        if (!java.util.Objects.equals(antes, despues)) {
            m.addRow(new Object[]{campo, antes, despues});
        }
    }

    private void cargarTablaCambios() {
        DefaultTableModel m = new DefaultTableModel(new String[]{"Campo", "Antes", "Después"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jComboBox1 = new javax.swing.JComboBox<>();
        jP_EncabezadoEd = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTBuscar = new javax.swing.JToggleButton();
        jTDatos = new javax.swing.JToggleButton();
        jTConfirmar = new javax.swing.JToggleButton();
        jP_EdCard = new javax.swing.JPanel();
        jP_EdDatos = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtNoControl2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtApPat = new javax.swing.JTextField();
        txtApMaterno = new javax.swing.JTextField();
        txtCorreo = new javax.swing.JTextField();
        cmbCarrera = new javax.swing.JComboBox<>();
        txtPromedio = new javax.swing.JTextField();
        dcFechaNac = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstMaterias = new javax.swing.JList<>();
        btnAgregarMat = new javax.swing.JButton();
        btnQuitarMat = new javax.swing.JButton();
        btnAtras2 = new javax.swing.JButton();
        btnRestablecer = new javax.swing.JButton();
        btnCancelar2 = new javax.swing.JButton();
        btnContinuar2 = new javax.swing.JButton();
        jP_EdConfirmar = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        lblResumen = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
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

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel1.setText("EDICION");

        jTBuscar.setText("1.Buscar");
        jTBuscar.addActionListener(this::jTBuscarActionPerformed);

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
                .addContainerGap(79, Short.MAX_VALUE))
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

        add(jP_EncabezadoEd, java.awt.BorderLayout.PAGE_START);

        jP_EdCard.setLayout(new java.awt.CardLayout());

        jP_EdDatos.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setText("Num.Ctrl");
        jP_EdDatos.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, -1, -1));

        txtNoControl2.setEditable(false);
        jP_EdDatos.add(txtNoControl2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 60, 280, -1));

        jLabel3.setText("Nombre");
        jP_EdDatos.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 110, -1, -1));

        jLabel4.setText("Apellido paterno");
        jP_EdDatos.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 160, -1, -1));

        jLabel5.setText("Apellido materno");
        jP_EdDatos.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 210, -1, -1));

        jLabel6.setText("Correo");
        jP_EdDatos.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 250, -1, -1));

        jLabel7.setText("Carrera");
        jP_EdDatos.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 300, -1, -1));

        jLabel8.setText("Promedio");
        jP_EdDatos.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 350, -1, -1));

        jLabel9.setText("Fecha nac.");
        jP_EdDatos.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 400, -1, -1));

        txtNombre.addActionListener(this::txtNombreActionPerformed);
        jP_EdDatos.add(txtNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 110, 280, -1));
        jP_EdDatos.add(txtApPat, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 160, 280, -1));
        jP_EdDatos.add(txtApMaterno, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 210, 280, -1));

        txtCorreo.addActionListener(this::txtCorreoActionPerformed);
        jP_EdDatos.add(txtCorreo, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 250, 280, -1));

        cmbCarrera.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jP_EdDatos.add(cmbCarrera, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 300, 280, -1));
        jP_EdDatos.add(txtPromedio, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 350, 280, -1));
        jP_EdDatos.add(dcFechaNac, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 400, 280, -1));

        jLabel10.setText("Materias");
        jP_EdDatos.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 450, -1, -1));

        lstMaterias.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(lstMaterias);

        jP_EdDatos.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 480, 470, 100));

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
        btnContinuar2.addActionListener(this::btnContinuar2ActionPerformed);
        jP_EdDatos.add(btnContinuar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 650, -1, -1));

        jP_EdCard.add(jP_EdDatos, "datos");

        jLabel11.setText("Confirmar cambios");

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
        jScrollPane2.setViewportView(tblCambios);

        chkConfirmo.setText("Revise los cambios y deseo guardarlos");
        chkConfirmo.addActionListener(this::chkConfirmoActionPerformed);

        btnAtras3.setText("Atras");
        btnAtras3.addActionListener(this::btnAtras3ActionPerformed);

        btnGuardar.setText("Guardar cambios");
        btnGuardar.setEnabled(false);
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

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
                                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 565, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkConfirmo, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jP_EdConfirmarLayout.createSequentialGroup()
                        .addGap(326, 326, 326)
                        .addComponent(btnAtras3)
                        .addGap(48, 48, 48)
                        .addComponent(btnGuardar)))
                .addContainerGap(117, Short.MAX_VALUE))
        );
        jP_EdConfirmarLayout.setVerticalGroup(
            jP_EdConfirmarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_EdConfirmarLayout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblResumen)
                .addGroup(jP_EdConfirmarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jP_EdConfirmarLayout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(chkConfirmo)
                        .addContainerGap(161, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jP_EdConfirmarLayout.createSequentialGroup()
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

        txtNoControl.addActionListener(this::txtNoControlActionPerformed);
        jP_EdBuscar.add(txtNoControl, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 110, 240, 20));

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(this::btnBuscarActionPerformed);
        jP_EdBuscar.add(btnBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 110, -1, -1));

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
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jP_EdBuscar.add(jP_Resultado, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 540, 130));

        jBContinuar1.setText("Continuar");
        jBContinuar1.setEnabled(false);
        jBContinuar1.addActionListener(this::jBContinuar1ActionPerformed);
        jP_EdBuscar.add(jBContinuar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 300, -1, 20));

        jBCancelar1.setText("Cancelar");
        jBCancelar1.addActionListener(this::jBCancelar1ActionPerformed);
        jP_EdBuscar.add(jBCancelar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 300, -1, -1));

        jP_EdCard.add(jP_EdBuscar, "buscar");

        add(jP_EdCard, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jTBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTBuscarActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jTBuscarActionPerformed

    private void txtNoControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNoControlActionPerformed
        // TODO add your handling code here:
        btnBuscar.doClick();
    }//GEN-LAST:event_txtNoControlActionPerformed

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtCorreoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCorreoActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtCorreoActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        // TODO add your handling code here:
        String nc = txtNoControl.getText().trim();

        // 1. Que no esté vacío
        if (nc.isEmpty()) {
            lblEstado.setText("Escribe un número de control");
            return;
        }

        // 2. Que solo tenga números (sin espacios, letras ni símbolos)
        if (!nc.matches("\\d+")) {
            limpiarResultado();
            alumnoOriginal = null;
            jBContinuar1.setEnabled(false);
            lblEstado.setText("El No. de control solo debe tener números, sin espacios ni símbolos");
            return;
        }

        // 3. Buscar al alumno
        Alumno a = controller.buscarAlumnoPorNoControl(nc);
        if (a == null) {
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

    private void btnContinuar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinuar2ActionPerformed
        // TODO add your handling code here:
        if (!validarFormulario()) {
            return;
        }
        cargarTablaCambios();
        irA("confirmar");

    }//GEN-LAST:event_btnContinuar2ActionPerformed

    private void jBContinuar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBContinuar1ActionPerformed
        llenarFormulario(alumnoOriginal);
        irA("datos");
    }//GEN-LAST:event_jBContinuar1ActionPerformed

    private void jBCancelar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelar1ActionPerformed
        // TODO add your handling code here:
        reiniciar();
    }//GEN-LAST:event_jBCancelar1ActionPerformed

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

        if (controller.actualizarAlumno(nuevo)) {
            JOptionPane.showMessageDialog(this, "Alumno actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            reiniciar();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el alumno.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btnGuardarActionPerformed

    private void chkConfirmoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkConfirmoActionPerformed
        // TODO add your handling code here:
        btnGuardar.setEnabled(chkConfirmo.isSelected() && tblCambios.getRowCount() > 0);

    }//GEN-LAST:event_chkConfirmoActionPerformed

    private void btnAtras3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtras3ActionPerformed
        // TODO add your handling code here:
        irA("datos");

    }//GEN-LAST:event_btnAtras3ActionPerformed


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
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chkConfirmo;
    private javax.swing.JComboBox<String> cmbCarrera;
    private com.toedter.calendar.JDateChooser dcFechaNac;
    private javax.swing.JButton jBCancelar1;
    private javax.swing.JButton jBContinuar1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLbl_Carrera;
    private javax.swing.JLabel jLbl_Correo;
    private javax.swing.JLabel jLbl_Fecha;
    private javax.swing.JLabel jLbl_Nombre;
    private javax.swing.JLabel jLbl_Promedio;
    private javax.swing.JLabel jLbl_Semestre;
    private javax.swing.JPanel jP_EdBuscar;
    private javax.swing.JPanel jP_EdCard;
    private javax.swing.JPanel jP_EdConfirmar;
    private javax.swing.JPanel jP_EdDatos;
    private javax.swing.JPanel jP_EncabezadoEd;
    private javax.swing.JPanel jP_Resultado;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToggleButton jTBuscar;
    private javax.swing.JToggleButton jTConfirmar;
    private javax.swing.JToggleButton jTDatos;
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
