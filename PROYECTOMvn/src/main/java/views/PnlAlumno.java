package views;

import com.toedter.calendar.JDateChooser;
import controller.AlumnoController;
import estilos.Estilos;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import model.Alumno;
import model.ItemCatalogo;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import validaciones.ValidadorAlumno;

/**
 * Formulario de ALTA o EDICIÓN de un alumno que se muestra DENTRO de la
 * ventana principal, en el panel derecho (no es ventana emergente).
 *
 * - Si "original" es null funciona como alta. - Si trae un alumno, funciona
 * como edición: el No. de control no se puede cambiar, los campos modificados
 * se marcan en ámbar y abajo se resume qué cambió.
 *
 * Al guardar o cancelar avisa a VtnAdministrador con "alTerminar" (recibe el
 * alumno guardado, o null si se canceló) para que vuelva a mostrar la ficha.
 * Enter = guardar, Esc = cancelar.
 */
public class PnlAlumno extends JPanel
{

    private static final Logger log = LoggerFactory.getLogger(PnlAlumno.class);

    private static final Color ROJO = new Color(0xB42318);
    private static final Color AMBAR = new Color(0xC27C0E);
    private static final String SELECCIONE = "Seleccione una carrera";

    /**
     * Materias de respaldo si la base de datos no responde (las mismas que
     * tenía la tabla del alta anterior).
     */
    private static final String[] MATERIAS_RESPALDO =
    {
        "Top. Avan. Prog.", "Simulacion", "Metodos Numericos", "Prolog", "Arq.Computadoras."
    };

    private final AlumnoController controller;
    private final Alumno original;   // null = alta
    private final Consumer<Alumno> alTerminar; // se llama al guardar (alumno) o cancelar (null)

    // ---------- CAMPOS ----------
    private final JTextField txtNombre = VtnAdministrador.campoTexto("Ej. Luis Alberto", 16);
    private final JTextField txtApPat = VtnAdministrador.campoTexto("Ej. Estrada", 16);
    private final JTextField txtApMat = VtnAdministrador.campoTexto("Ej. Peña", 16);
    private final JDateChooser dcNacimiento = new JDateChooser();
    private final JTextField txtNoControl = VtnAdministrador.campoTexto("Solo números", 16);
    private final JComboBox<String> cmbCarrera = new JComboBox<>();
    private final JTextField txtCorreo = VtnAdministrador.campoTexto("usuario@dominio.com", 16);
    private final JTextField txtPromedio = VtnAdministrador.campoTexto("Ej. 85.5", 6);
    private final List<JCheckBox> chkMaterias = new ArrayList<>();

    // mensajes de error y bordes originales por campo
    private final Map<JComponent, JLabel> errores = new LinkedHashMap<>();
    private final Map<JComponent, Border> bordes = new LinkedHashMap<>();
    private final Map<JComponent, JLabel> titulos = new LinkedHashMap<>();
    private JPanel panelMaterias;

    private final JLabel lblResumen = new JLabel(" ");
    private final JButton btnRestablecer = new JButton("Restablecer");
    private final JButton btnCancelar = new JButton("Cancelar");
    private final JButton btnGuardar = new JButton();

    public PnlAlumno(AlumnoController controller, List<String> carreras,
            List<String> materias, Alumno original, Consumer<Alumno> alTerminar)
    {
        super(new BorderLayout());
        this.controller = controller;
        this.original = original;
        this.alTerminar = alTerminar;

        construir(carreras, materias);
        if (original != null)
        {
            llenar(original);
        }
        escucharCambios();
        actualizarResumen();
    }

    /**
     * Pone el cursor en el primer campo que se puede escribir.
     */
    public void enfocar()
    {
        txtNombre.requestFocusInWindow();
    }

    /**
     * Al mostrarse, Enter activa "Guardar"; al ocultarse se quita.
     */
    @Override
    public void addNotify()
    {
        super.addNotify();
        if (SwingUtilities.getRootPane(this) != null)
        {
            SwingUtilities.getRootPane(this).setDefaultButton(btnGuardar);
        }
    }

    @Override
    public void removeNotify()
    {
        if (SwingUtilities.getRootPane(this) != null
                && SwingUtilities.getRootPane(this).getDefaultButton() == btnGuardar)
        {
            SwingUtilities.getRootPane(this).setDefaultButton(null);
        }
        super.removeNotify();
    }

    // ==================================================================
    // INTERFAZ
    // ==================================================================
    private void construir(List<String> carreras, List<String> materias)
    {
        JPanel raiz = this;
        setBackground(Estilos.CREMA_CLARO);

        // ---- título: NUEVO ALUMNO / EDITANDO · No. control ----
        JLabel titulo = new JLabel(original == null ? "NUEVO ALUMNO" : "EDITANDO · " + original.getNo_control());
        titulo.setFont(Estilos.fuente(java.awt.Font.PLAIN, 30));
        titulo.setForeground(Estilos.AZUL);
        titulo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Estilos.NEGRO),
                BorderFactory.createEmptyBorder(10, 16, 8, 16)));
        raiz.add(titulo, BorderLayout.NORTH);

        // ---- estilos del proyecto (antes de registrar los campos) ----
        for (JTextField t : new JTextField[]
        {
            txtNombre, txtApPat, txtApMat, txtNoControl, txtCorreo, txtPromedio
        })
        {
            Estilos.estilizarCampo(t);
            t.setFont(VtnAdministrador.TEXTO); // letra normal: se distinguen mayúsculas/minúsculas
        }
        Estilos.estilizarCombo(cmbCarrera);
        dcNacimiento.setDateFormatString("dd/MM/yyyy");
        Estilos.estilizarFecha(dcNacimiento);
        for (Component c : dcNacimiento.getComponents())
        {
            if (c instanceof JTextField)
            {
                c.setFont(VtnAdministrador.TEXTO); // la fuente de títulos cortaba la fecha
            }
        }

        // ---- formulario: una columna, con los campos cortos en pares ----
        JPanel form = new PanelAjustable(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.weightx = 1;

        seccion(form, g, 0, "Datos personales");
        campo(form, g, 1, 0, 2, "Nombre(s)", txtNombre);
        campo(form, g, 2, 0, 1, "Apellido paterno", txtApPat);
        campo(form, g, 2, 1, 1, "Apellido materno", txtApMat);
        campo(form, g, 3, 0, 1, "Fecha de nacimiento", dcNacimiento);
        campo(form, g, 3, 1, 1, "No. de control", txtNoControl);

        seccion(form, g, 4, "Datos académicos");
        campo(form, g, 5, 0, 2, "Carrera", cmbCarrera);
        campo(form, g, 6, 0, 1, "Correo", txtCorreo);
        campo(form, g, 6, 1, 1, "Promedio (0 a 100)", txtPromedio);

        // materias: casillas en 2 columnas (se ven todas, sin Ctrl+clic)
        panelMaterias = new JPanel(new GridLayout(0, 2, 10, 4));
        panelMaterias.setBackground(Estilos.CREMA_CLARO);
        panelMaterias.setOpaque(true);
        panelMaterias.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        List<String> lista = (materias == null || materias.isEmpty()) ? List.of(MATERIAS_RESPALDO) : materias;
        for (String m : lista)
        {
            agregarCasilla(m);
        }
        JScrollPane spMaterias = new JScrollPane(panelMaterias);
        Estilos.estilizarScroll(spMaterias);
        spMaterias.setPreferredSize(new Dimension(0, 104));
        spMaterias.getVerticalScrollBar().setUnitIncrement(16);
        campo(form, g, 7, 0, 2, "Materias que cursa", spMaterias);

        // relleno para que todo quede arriba
        g.gridx = 0;
        g.gridy = 8;
        g.weighty = 1;
        form.add(javax.swing.Box.createVerticalGlue(), g);

        // si la ventana es chica, el formulario se desplaza
        JScrollPane scroll = new JScrollPane(form,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Estilos.CREMA_CLARO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        raiz.add(scroll, BorderLayout.CENTER);

        // ---- carrera ----
        DefaultComboBoxModel<String> mCarreras = new DefaultComboBoxModel<>();
        mCarreras.addElement(SELECCIONE);
        if (carreras != null)
        {
            for (String c : carreras)
            {
                mCarreras.addElement(c);
            }
        }
        cmbCarrera.setModel(mCarreras);


        // ---- pie: resumen + botones ----
        JPanel pie = new JPanel(new BorderLayout());
        pie.setBackground(Estilos.CREMA);
        pie.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, Estilos.NEGRO),
                BorderFactory.createEmptyBorder(8, 14, 10, 14)));

        lblResumen.setFont(VtnAdministrador.TEXTO);
        lblResumen.setForeground(Estilos.AZUL_OSCURO);
        lblResumen.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        pie.add(lblResumen, BorderLayout.NORTH);
        Estilos.estilizarBoton(btnRestablecer);
        VtnAdministrador.compactar(btnRestablecer, 18);
        btnRestablecer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.NEGRO, 2), BorderFactory.createEmptyBorder(5, 6, 5, 6)));
        Estilos.icono(btnRestablecer, FontAwesomeSolid.UNDO, 14);
        btnRestablecer.setToolTipText("Volver a los datos originales del alumno");
        btnRestablecer.setVisible(original != null);
        btnRestablecer.addActionListener(e -> llenar(original));

        // botones del mismo ancho, en una fila que siempre cabe en el panel
        JPanel der = new JPanel(new GridLayout(1, 0, 8, 0));
        der.setOpaque(false);
        if (original != null)
        {
            der.add(btnRestablecer);
        }
        btnGuardar.setText(original == null ? "Guardar alumno" : "Guardar cambios");
        Estilos.estilizarBoton(btnCancelar);
        Estilos.estilizarBotonPrincipal(btnGuardar);
        VtnAdministrador.compactar(btnCancelar, 18);
        VtnAdministrador.compactar(btnGuardar, 18);
        for (JButton b : new JButton[]
        {
            btnCancelar, btnGuardar
        })
        {
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Estilos.NEGRO, 2), BorderFactory.createEmptyBorder(5, 6, 5, 6)));
        }
        Estilos.icono(btnCancelar, FontAwesomeSolid.TIMES, 16);
        Estilos.icono(btnGuardar, FontAwesomeSolid.SAVE, 16);
        btnCancelar.setToolTipText("Salir sin guardar (Esc)");
        btnGuardar.setToolTipText("Guardar (Enter)");
        btnCancelar.addActionListener(e -> cancelar());
        btnGuardar.addActionListener(e -> guardar());
        der.add(btnCancelar);
        der.add(btnGuardar);
        pie.add(der, BorderLayout.SOUTH);
        raiz.add(pie, BorderLayout.SOUTH);

        // Esc cancela mientras el cursor esté dentro del formulario
        // (Enter = Guardar se configura en addNotify)
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelar");
        getActionMap().put("cancelar", VtnAdministrador.accion(this::cancelar));
    }

    private void seccion(JPanel p, GridBagConstraints g, int fila, String texto)
    {
        JLabel l = new JLabel(texto.toUpperCase());
        Estilos.estilizarBarraSeccion(l);   // barra negra con texto blanco
        g.gridx = 0;
        g.gridy = fila;
        g.gridwidth = 2;
        g.insets = new Insets(fila == 0 ? 0 : 8, 0, 8, 0);
        p.add(l, g);
        g.gridwidth = 1;
    }

    /**
     * Agrega etiqueta + campo + mensaje de error en la fila y columna dadas.
     * "ancho" = cuántas columnas ocupa (1 o 2).
     */
    private void campo(JPanel p, GridBagConstraints g, int fila, int col, int ancho, String texto, JComponent c)
    {
        JLabel titulo = VtnAdministrador.etiqueta(texto);
        titulo.setLabelFor(c);
        JLabel error = new JLabel(" ");
        error.setForeground(ROJO);
        error.setFont(VtnAdministrador.TEXTO.deriveFont(12f));

        JPanel celda = new JPanel(new BorderLayout(0, 3));
        celda.setOpaque(false);
        celda.add(titulo, BorderLayout.NORTH);
        celda.add(c, BorderLayout.CENTER);
        celda.add(error, BorderLayout.SOUTH);
        // mismo ancho base para que las dos columnas queden parejas
        int alto = celda.getPreferredSize().height;
        celda.setPreferredSize(new Dimension(ancho == 2 ? 200 : 100, alto));
        celda.setMinimumSize(new Dimension(ancho == 2 ? 200 : 100, alto));

        g.gridx = col;
        g.gridy = fila;
        g.gridwidth = ancho;
        boolean izq = col == 0 && ancho == 1;
        g.insets = new Insets(0, col == 1 ? 6 : 0, 2, izq ? 6 : 0);
        p.add(celda, g);
        g.gridwidth = 1;

        errores.put(c, error);
        bordes.put(c, c.getBorder());
        titulos.put(c, titulo);
    }

    private JCheckBox agregarCasilla(String materia)
    {
        JCheckBox chk = new JCheckBox(materia);
        chk.setOpaque(false);
        chk.setFont(VtnAdministrador.TEXTO);
        chk.setForeground(Estilos.NEGRO);
        chk.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chkMaterias.add(chk);
        panelMaterias.add(chk);
        return chk;
    }

    // ==================================================================
    // LLENAR / LEER
    // ==================================================================
    private void llenar(Alumno a)
    {
        String[] partes = descomponerNombre(a.getNombre());
        txtNombre.setText(partes[0]);
        txtApPat.setText(partes[1]);
        txtApMat.setText(partes[2]);
        dcNacimiento.setDate(a.getFechaNaciomiento());
        txtNoControl.setText(a.getNo_control());
        txtNoControl.setEditable(false);
        txtNoControl.setBackground(Estilos.CREMA); // se ve que no se puede cambiar
        txtNoControl.setToolTipText("El No. de control no se puede cambiar");
        txtCorreo.setText(a.getCorreo());
        txtPromedio.setText(String.valueOf(a.getPromedio()));

        String carrera = VtnAdministrador.texto(a.getCarerra());
        if (((DefaultComboBoxModel<String>) cmbCarrera.getModel()).getIndexOf(carrera) < 0 && !carrera.isEmpty())
        {
            cmbCarrera.addItem(carrera); // carrera que ya no está en el catálogo
        }
        cmbCarrera.setSelectedItem(carrera);

        List<String> suyas = VtnAdministrador.nombresMaterias(a.getMaterias());
        for (String m : suyas)
        {
            if (buscarCasilla(m) == null)
            {
                agregarCasilla(m); // materia que ya no está en el catálogo
            }
        }
        for (JCheckBox chk : chkMaterias)
        {
            chk.setSelected(suyas.contains(chk.getText()));
        }
        limpiarErrores();
        actualizarResumen();
    }

    private JCheckBox buscarCasilla(String materia)
    {
        for (JCheckBox chk : chkMaterias)
        {
            if (chk.getText().equals(materia))
            {
                return chk;
            }
        }
        return null;
    }

    private List<String> materiasMarcadas()
    {
        List<String> marcadas = new ArrayList<>();
        for (JCheckBox chk : chkMaterias)
        {
            if (chk.isSelected())
            {
                marcadas.add(chk.getText());
            }
        }
        return marcadas;
    }

    private String nombreCompleto()
    {
        return (txtNombre.getText().trim() + " " + txtApPat.getText().trim() + " "
                + txtApMat.getText().trim()).trim().replaceAll("\\s+", " ");
    }

    /**
     * Separa "Nombre(s) ApellidoPaterno ApellidoMaterno". Los dos últimos
     * textos son los apellidos; lo demás es el nombre.
     */
    private static String[] descomponerNombre(String completo)
    {
        String[] t = VtnAdministrador.texto(completo).split("\\s+");
        if (t.length >= 3)
        {
            return new String[]
            {
                String.join(" ", java.util.Arrays.copyOf(t, t.length - 2)), t[t.length - 2], t[t.length - 1]
            };
        }
        return new String[]
        {
            t.length > 0 ? t[0] : "", t.length > 1 ? t[1] : "", ""
        };
    }

    // ==================================================================
    // CAMBIOS (solo en edición)
    // ==================================================================
    private void escucharCambios()
    {
        DocumentListener dl = new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                actualizarResumen();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                actualizarResumen();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                actualizarResumen();
            }
        };
        for (JTextField t : new JTextField[]
        {
            txtNombre, txtApPat, txtApMat, txtNoControl, txtCorreo, txtPromedio
        })
        {
            t.getDocument().addDocumentListener(dl);
        }
        cmbCarrera.addActionListener(e -> actualizarResumen());
        dcNacimiento.addPropertyChangeListener("date", e -> actualizarResumen());
        for (JCheckBox chk : chkMaterias)
        {
            chk.addActionListener(e -> actualizarResumen());
        }
    }

    /**
     * Lista de campos que cambiaron respecto al alumno original.
     */
    private List<String> cambios()
    {
        List<String> c = new ArrayList<>();
        if (original == null)
        {
            return c;
        }
        String[] p = descomponerNombre(original.getNombre());
        marcar(txtNombre, !p[0].equals(txtNombre.getText().trim()), c, "Nombre");
        marcar(txtApPat, !p[1].equals(txtApPat.getText().trim()), c, "Apellido paterno");
        marcar(txtApMat, !p[2].equals(txtApMat.getText().trim()), c, "Apellido materno");
        marcar(dcNacimiento, !mismoDia(original.getFechaNaciomiento(), dcNacimiento.getDate()), c, "Fecha");
        marcar(cmbCarrera, !VtnAdministrador.texto(original.getCarerra())
                .equals(VtnAdministrador.texto(cmbCarrera.getSelectedItem())), c, "Carrera");
        marcar(txtCorreo, !VtnAdministrador.texto(original.getCorreo()).equals(txtCorreo.getText().trim()), c, "Correo");
        Double prom = ValidadorAlumno.promedioValido(txtPromedio.getText().trim());
        marcar(txtPromedio, prom == null || prom != original.getPromedio(), c, "Promedio");
        boolean materiasCambiaron = !new java.util.HashSet<>(VtnAdministrador.nombresMaterias(original.getMaterias()))
                .equals(new java.util.HashSet<>(materiasMarcadas()));
        if (materiasCambiaron)
        {
            c.add("Materias");
        }
        return c;
    }

    /**
     * Pinta el título del campo en ámbar con "· modificado" si cambió.
     */
    private void marcar(JComponent campo, boolean cambio, List<String> lista, String nombre)
    {
        JLabel t = titulos.get(campo);
        String base = t.getText().replace("  · MODIFICADO", "");
        t.setText(cambio ? base + "  · MODIFICADO" : base);
        t.setForeground(cambio ? AMBAR : Estilos.AZUL_OSCURO);
        if (cambio)
        {
            lista.add(nombre);
        }
    }

    private static boolean mismoDia(Date a, Date b)
    {
        if (a == null || b == null)
        {
            return Objects.equals(a, b);
        }
        java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("yyyyMMdd");
        return f.format(a).equals(f.format(b));
    }

    private void actualizarResumen()
    {
        if (original == null)
        {
            lblResumen.setText("Todos los campos son obligatorios");
            return;
        }
        List<String> c = cambios();
        lblResumen.setText(c.isEmpty() ? "Sin cambios"
                : c.size() + (c.size() == 1 ? " cambio: " : " cambios: ") + String.join(", ", c));
        btnGuardar.setEnabled(!c.isEmpty());
        btnRestablecer.setEnabled(!c.isEmpty());
    }

    // ==================================================================
    // VALIDAR Y GUARDAR
    // ==================================================================
    private boolean validar()
    {
        limpiarErrores();
        String nombre = txtNombre.getText().trim();
        String apPat = txtApPat.getText().trim();
        String apMat = txtApMat.getText().trim();
        Date fecha = dcNacimiento.getDate();
        String noControl = txtNoControl.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (!ValidadorAlumno.nombreValido(nombre))
        {
            marcarError(txtNombre, "Escribe el nombre (solo letras)");
        }
        if (!ValidadorAlumno.nombreValido(apPat))
        {
            marcarError(txtApPat, "Solo letras y espacios");
        }
        if (!ValidadorAlumno.nombreValido(apMat))
        {
            marcarError(txtApMat, "Solo letras y espacios");
        }
        if (ValidadorAlumno.fechaVacia(fecha))
        {
            marcarError(dcNacimiento, "Elige la fecha");
        } else if (ValidadorAlumno.fechaFutura(fecha) || ValidadorAlumno.fechaPasada(fecha))
        {
            marcarError(dcNacimiento, "Entre 1950 y hoy");
        }
        if (!ValidadorAlumno.validarNumericos(noControl))
        {
            marcarError(txtNoControl, "Solo números");
        } else if (original == null && controller.buscarAlumnoPorNoControl(noControl) != null)
        {
            marcarError(txtNoControl, "Ya está registrado");
        }
        if (ValidadorAlumno.seleccionCb(cmbCarrera.getSelectedIndex()))
        {
            marcarError(cmbCarrera, "Elige una carrera");
        }
        if (!ValidadorAlumno.correoValido(correo))
        {
            marcarError(txtCorreo, "Ej. usuario@dominio.com");
        }
        if (ValidadorAlumno.promedioValido(txtPromedio.getText().trim()) == null)
        {
            marcarError(txtPromedio, "Número entre 0 y 100");
        }
        if (ValidadorAlumno.materiasVacias(materiasMarcadas()))
        {
            marcarError((JComponent) panelMaterias.getParent().getParent(), "Marca al menos una materia");
        }

        // llevar el cursor al primer campo con error
        for (Map.Entry<JComponent, JLabel> e : errores.entrySet())
        {
            if (!e.getValue().getText().isBlank())
            {
                JComponent c = e.getKey();
                // desplazar el formulario hasta el campo con error
                if (c.getParent() instanceof JComponent)
                {
                    JComponent celda = (JComponent) c.getParent();
                    celda.scrollRectToVisible(new java.awt.Rectangle(0, 0, celda.getWidth(), celda.getHeight()));
                }
                (c instanceof JDateChooser ? ((JDateChooser) c).getDateEditor().getUiComponent() : c)
                        .requestFocusInWindow();
                return false;
            }
        }
        return true;
    }

    private void marcarError(JComponent c, String msg)
    {
        JLabel l = errores.get(c);
        if (l != null)
        {
            l.setText(msg);
        }
        c.setBorder(c instanceof JTextField
                ? BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ROJO, 2),
                        BorderFactory.createEmptyBorder(6, 8, 6, 8))
                : BorderFactory.createLineBorder(ROJO, 2));
    }

    private void limpiarErrores()
    {
        for (Map.Entry<JComponent, JLabel> e : errores.entrySet())
        {
            e.getValue().setText(" ");
            e.getKey().setBorder(bordes.get(e.getKey()));
        }
    }

    private void guardar()
    {
        if (!validar())
        {
            return;
        }
        List<ItemCatalogo> materias = new ArrayList<>();
        for (String m : materiasMarcadas())
        {
            materias.add(new ItemCatalogo(0, m));
        }
        Alumno alumno = new Alumno(
                txtNoControl.getText().trim(),
                nombreCompleto(),
                txtCorreo.getText().trim(),
                new ItemCatalogo(0, (String) cmbCarrera.getSelectedItem()),
                ValidadorAlumno.promedioValido(txtPromedio.getText().trim()),
                dcNacimiento.getDate(),
                materias);

        boolean ok = original == null
                ? controller.agergarAlumno(alumno)
                : controller.actualizarAlumno(alumno);
        if (!ok)
        {
            JOptionPane.showMessageDialog(this,
                    original == null ? "El No. de control ya está registrado." : "No se pudo actualizar el alumno.",
                    "No se guardó", JOptionPane.ERROR_MESSAGE);
            return;
        }
        log.info("Alumno {} {} (No. Control {})", alumno.getNombre(),
                original == null ? "registrado" : "actualizado", alumno.getNo_control());
        alTerminar.accept(alumno);
    }

    /**
     * Sale sin guardar; si hay datos escritos pregunta antes de perderlos.
     */
    private void cancelar()
    {
        boolean hayDatos = original == null
                ? !nombreCompleto().isEmpty() || !txtNoControl.getText().isBlank() || !txtCorreo.getText().isBlank()
                : !cambios().isEmpty();
        if (hayDatos)
        {
            Object[] opciones =
            {
                "Descartar", "Seguir editando"
            };
            int r = JOptionPane.showOptionDialog(this, "Hay datos sin guardar. ¿Descartarlos?",
                    "Salir sin guardar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, opciones, opciones[1]);
            if (r != 0)
            {
                return;
            }
        }
        alTerminar.accept(null);
    }

    /**
     * Panel que se ajusta al ancho del área visible: así el formulario nunca se
     * corta a la derecha y solo se desplaza hacia abajo.
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
            return false;
        }
    }
}
