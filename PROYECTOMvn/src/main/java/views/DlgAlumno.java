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
import javax.swing.JDialog;
import javax.swing.JFrame;
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
 * Diálogo único para dar de ALTA o EDITAR un alumno.
 *
 * - Si "original" es null funciona como alta. - Si trae un alumno, funciona
 * como edición: el No. de control no se puede cambiar, los campos modificados
 * se marcan y abajo se resume qué cambió (reemplaza el paso "Confirmar").
 *
 * Todos los campos están en una sola pantalla (sin pasos ni "Continuar"). Los
 * errores se muestran junto al campo. Enter = guardar, Esc = cancelar.
 */
public class DlgAlumno extends JDialog
{

    private static final Logger log = LoggerFactory.getLogger(DlgAlumno.class);

    private static final Color ROJO = new Color(0xB42318);
    private static final int ALTO_CAMPO = 40; // misma altura para todos los campos
    private static final Color AMBAR = new Color(0xC27C0E);
    private static final ItemCatalogo SELECCIONE = new ItemCatalogo(0, "Seleccione una carrera");

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
    private Alumno resultado;        // alumno guardado (null si se canceló)

    // ---------- CAMPOS ----------
    private final JTextField txtNombre = VtnAdministrador.campoTexto("Ej. Luis Alberto", 16);
    private final JTextField txtApPat = VtnAdministrador.campoTexto("Ej. Estrada", 16);
    private final JTextField txtApMat = VtnAdministrador.campoTexto("Ej. Peña", 16);
    private final JDateChooser dcNacimiento = new JDateChooser();
    private final JTextField txtNoControl = VtnAdministrador.campoTexto("Solo números", 16);
    private final JComboBox<ItemCatalogo> cmbCarrera = new JComboBox<>();
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

    public DlgAlumno(JFrame duenio, AlumnoController controller, List<ItemCatalogo> carreras,
            List<ItemCatalogo> materias, Alumno original)
    {
        super(duenio, original == null ? "Nuevo alumno" : "Editar alumno · " + original.getNo_control(), true);
        this.controller = controller;
        this.original = original;

        construir(carreras, materias);
        if (original != null)
        {
            llenar(original);
        }
        escucharCambios();
        actualizarResumen();

        pack();
        setSize(Math.max(getWidth(), 820), getHeight());
        setMinimumSize(getSize());
        setLocationRelativeTo(duenio);
    }

    /**
     * Alumno guardado, o null si el usuario canceló.
     */
    public Alumno getResultado()
    {
        return resultado;
    }

    // ==================================================================
    // INTERFAZ
    // ==================================================================
    private void construir(List<ItemCatalogo> carreras, List<ItemCatalogo> materias)
    {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Estilos.CREMA_CLARO);
        setContentPane(raiz);

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
                ((JTextField) c).setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            } else if (c instanceof JButton)
            {
                // botón del calendario dentro del borde, separado por una línea
                JButton b = (JButton) c;
                b.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, Estilos.NEGRO));
                b.setPreferredSize(new Dimension(36, 36));
                b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        }

        // todos los campos con la misma altura: la fecha ya no se ve cortada
        for (JComponent c : new JComponent[]
        {
            txtNombre, txtApPat, txtApMat, txtNoControl, txtCorreo, txtPromedio, cmbCarrera, dcNacimiento
        })
        {
            c.setPreferredSize(new Dimension(200, ALTO_CAMPO));
            c.setMinimumSize(new Dimension(120, ALTO_CAMPO));
        }

        // ---- formulario en 2 columnas ----
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(20, 28, 10, 28));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.weightx = 1;

        seccion(form, g, 0, "Datos personales");
        campo(form, g, 1, 0, "Nombre(s)", txtNombre);
        campo(form, g, 1, 1, "Fecha de nacimiento", dcNacimiento);
        campo(form, g, 3, 0, "Apellido paterno", txtApPat);
        campo(form, g, 3, 1, "Apellido materno", txtApMat);

        seccion(form, g, 5, "Cuenta y datos académicos");
        campo(form, g, 6, 0, "No. de control", txtNoControl);
        campo(form, g, 6, 1, "Carrera", cmbCarrera);
        campo(form, g, 8, 0, "Correo", txtCorreo);
        campo(form, g, 8, 1, "Promedio (0 a 100)", txtPromedio);

        // materias: casillas en 2 columnas (caben nombres largos del catálogo)
        panelMaterias = new JPanel(new GridLayout(0, 2, 20, 10));
        panelMaterias.setBackground(Estilos.CREMA_CLARO);
        panelMaterias.setOpaque(true);
        panelMaterias.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        List<ItemCatalogo> lista = materias;
        if (lista == null || lista.isEmpty())
        {
            // respaldo si la BD no responde (ids negativos para no chocar con los reales)
            lista = new ArrayList<>();
            for (int i = 0; i < MATERIAS_RESPALDO.length; i++)
            {
                lista.add(new ItemCatalogo(-(i + 1), MATERIAS_RESPALDO[i]));
            }
        }
        for (ItemCatalogo m : lista)
        {
            agregarCasilla(m);
        }
        JScrollPane spMaterias = new JScrollPane(panelMaterias,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        Estilos.estilizarScroll(spMaterias);
        spMaterias.setPreferredSize(new Dimension(0, 110));
        spMaterias.getVerticalScrollBar().setUnitIncrement(16);
        campo(form, g, 10, 0, "Materias que cursa", spMaterias);
        // el campo de materias ocupa las 2 columnas
        GridBagLayout gl = (GridBagLayout) form.getLayout();
        for (Component c : form.getComponents())
        {
            GridBagConstraints gc = gl.getConstraints(c);
            if (gc.gridy >= 10)
            {
                gc.gridwidth = 2;
                gl.setConstraints(c, gc);
            }
        }
        raiz.add(form, BorderLayout.CENTER);

        // ---- carrera ----
        DefaultComboBoxModel<ItemCatalogo> mCarreras = new DefaultComboBoxModel<>();
        mCarreras.addElement(SELECCIONE);
        if (carreras != null)
        {
            for (ItemCatalogo c : carreras)
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
                BorderFactory.createEmptyBorder(10, 22, 10, 22)));

        lblResumen.setFont(VtnAdministrador.TEXTO);
        lblResumen.setForeground(Estilos.AZUL_OSCURO);
        pie.add(lblResumen, BorderLayout.CENTER);
        Estilos.estilizarBoton(btnRestablecer);
        VtnAdministrador.compactar(btnRestablecer, 18);
        Estilos.icono(btnRestablecer, FontAwesomeSolid.UNDO, 14);
        btnRestablecer.setToolTipText("Volver a los datos originales del alumno");
        btnRestablecer.setVisible(original != null);
        btnRestablecer.addActionListener(e -> llenar(original));

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        der.setOpaque(false);
        der.add(btnRestablecer);
        btnGuardar.setText(original == null ? "Guardar alumno" : "Guardar cambios");
        Estilos.estilizarBoton(btnCancelar);
        Estilos.estilizarBotonPrincipal(btnGuardar);
        VtnAdministrador.compactar(btnCancelar, 20);
        VtnAdministrador.compactar(btnGuardar, 20);
        Estilos.icono(btnCancelar, FontAwesomeSolid.TIMES, 16);
        Estilos.icono(btnGuardar, FontAwesomeSolid.SAVE, 16);
        btnCancelar.setToolTipText("Cerrar sin guardar (Esc)");
        btnGuardar.setToolTipText("Guardar (Enter)");
        btnCancelar.addActionListener(e -> cancelar());
        btnGuardar.addActionListener(e -> guardar());
        der.add(btnCancelar);
        der.add(btnGuardar);
        pie.add(der, BorderLayout.EAST);
        raiz.add(pie, BorderLayout.SOUTH);

        // Enter guarda, Esc cancela (estándar de diálogos)
        getRootPane().setDefaultButton(btnGuardar);
        VtnAdministrador.atajo(getRootPane(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelar", this::cancelar);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                cancelar();
            }
        });
    }

    private void seccion(JPanel p, GridBagConstraints g, int fila, String texto)
    {
        JLabel l = new JLabel(texto.toUpperCase());
        Estilos.estilizarBarraSeccion(l);   // barra negra con texto blanco
        g.gridx = 0;
        g.gridy = fila;
        g.gridwidth = 2;
        g.insets = new Insets(fila == 0 ? 0 : 16, 0, 12, 0);
        p.add(l, g);
        g.gridwidth = 1;
    }

    /**
     * Agrega etiqueta + campo + mensaje de error. Ocupa 2 filas de la rejilla.
     */
    private void campo(JPanel p, GridBagConstraints g, int fila, int col, String texto, JComponent c)
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

        g.gridx = col;
        g.gridy = fila;
        g.insets = new Insets(0, col == 0 ? 0 : 14, 8, col == 0 ? 14 : 0);
        p.add(celda, g);

        errores.put(c, error);
        bordes.put(c, c.getBorder());
        titulos.put(c, titulo);
    }

    private JCheckBox agregarCasilla(ItemCatalogo materia)
    {
        JCheckBox chk = new JCheckBox(materia.getNombre());
        chk.putClientProperty("materia", materia); // se guarda el id para la BD
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

        // la carrera se busca por nombre (el id puede venir en 0 de datos viejos)
        ItemCatalogo carrera = a.getCarerra();
        ItemCatalogo enLista = null;
        for (int i = 1; i < cmbCarrera.getItemCount(); i++)
        {
            if (carrera != null && cmbCarrera.getItemAt(i).getNombre().equalsIgnoreCase(carrera.getNombre()))
            {
                enLista = cmbCarrera.getItemAt(i);
            }
        }
        if (enLista == null && carrera != null && carrera.getNombre() != null)
        {
            cmbCarrera.addItem(carrera); // carrera que ya no está en el catálogo
            enLista = carrera;
        }
        cmbCarrera.setSelectedItem(enLista);

        List<String> suyas = VtnAdministrador.nombresMaterias(a.getMaterias());
        if (a.getMaterias() != null)
        {
            for (ItemCatalogo m : a.getMaterias())
            {
                if (m != null && m.getNombre() != null && buscarCasilla(m.getNombre()) == null)
                {
                    agregarCasilla(m); // materia que ya no está en el catálogo
                }
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
            marcarError(txtNombre, "Escribe el nombre (solo letras y espacios)");
        }
        if (!ValidadorAlumno.nombreValido(apPat))
        {
            marcarError(txtApPat, "Escribe el apellido paterno (solo letras)");
        }
        if (!ValidadorAlumno.nombreValido(apMat))
        {
            marcarError(txtApMat, "Escribe el apellido materno (solo letras)");
        }
        if (ValidadorAlumno.fechaVacia(fecha))
        {
            marcarError(dcNacimiento, "Elige la fecha de nacimiento");
        } else if (ValidadorAlumno.fechaFutura(fecha) || ValidadorAlumno.fechaPasada(fecha))
        {
            marcarError(dcNacimiento, "Debe ser entre 1950 y hoy");
        }
        if (!ValidadorAlumno.validarNumericos(noControl))
        {
            marcarError(txtNoControl, "Solo números, sin espacios ni símbolos");
        } else if (original == null && controller.buscarAlumnoPorNoControl(noControl) != null)
        {
            marcarError(txtNoControl, "Este No. de control ya está registrado");
        }
        if (ValidadorAlumno.seleccionCb(cmbCarrera.getSelectedIndex()))
        {
            marcarError(cmbCarrera, "Elige una carrera");
        }
        if (!ValidadorAlumno.correoValido(correo))
        {
            marcarError(txtCorreo, "Ejemplo: usuario@dominio.com");
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
        // materias marcadas con su id del catálogo
        List<ItemCatalogo> materias = new ArrayList<>();
        for (JCheckBox chk : chkMaterias)
        {
            if (chk.isSelected())
            {
                materias.add((ItemCatalogo) chk.getClientProperty("materia"));
            }
        }
        Alumno alumno = new Alumno(
                txtNoControl.getText().trim(),
                nombreCompleto(),
                txtCorreo.getText().trim(),
                (ItemCatalogo) cmbCarrera.getSelectedItem(),
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
        resultado = alumno;
        dispose();
    }

    /**
     * Cierra; si hay datos escritos pregunta antes de perderlos.
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
                    "Cerrar sin guardar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, opciones, opciones[1]);
            if (r != 0)
            {
                return;
            }
        }
        dispose();
    }
}
