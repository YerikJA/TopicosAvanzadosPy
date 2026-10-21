package views;

import controller.AlumnoController;
import estilos.Estilos;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

public class VtnHome extends javax.swing.JFrame
{

    private AlumnoController controllerGeneral;

    public VtnHome(AlumnoController controller)
    {
        initComponents();
        controllerGeneral = controller;
        Estilos.estilizarVentana(this);   // fondo crema + centrada
        setTitle("Sistema de Alumnos");
        setResizable(false);
        Estilos.estilizarVentana(this);   // fondo crema + centrada

        // panel izquierdo
        jPanel_Izquierdo.setBackground(Estilos.AZUL_OSCURO);
        Estilos.estilizarTitulo(jL_Tituo1);
        jL_Tituo1.setFont(Estilos.fuente(java.awt.Font.PLAIN, 46));
        jL_Tituo1.setForeground(Color.WHITE);
        jP_Barra.setBackground(Estilos.NARANJA);
        jL_Subtitulo.setText("<html>SISTEMA DE<br>CONTROL ESCOLAR</html>");
        jL_Subtitulo.setFont(Estilos.fuente(java.awt.Font.PLAIN, 20));
        jL_Subtitulo.setForeground(Estilos.AZUL_SUAVE);

        // panel derecho
        Estilos.estilizarFondo(jPanel_Derecho);
        Estilos.estilizarEtiqueta(jL_Guia);
        jL_Guia.setFont(Estilos.fuente(java.awt.Font.PLAIN, 20));
        jL_Pie.setFont(Estilos.fuente(java.awt.Font.PLAIN, 14));
        jL_Pie.setForeground(Estilos.GRIS_TEXTO);
        jL_Pie.setHorizontalAlignment(SwingConstants.CENTER);

        // tarjeta con sombra + botón
        Estilos.estilizarPanelConSombra(jP_Tarjeta);
        Estilos.estilizarBotonPrincipal(jBtt_Administrador1);
        jBtt_Administrador1.setFont(Estilos.fuente(java.awt.Font.PLAIN, 28));
        jBtt_Administrador1.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        jBtt_Administrador1.setHorizontalTextPosition(SwingConstants.LEFT);
        jBtt_Administrador1.setIconTextGap(14);
        Estilos.icono(jBtt_Administrador1, FontAwesomeSolid.ARROW_RIGHT, 22);

        getRootPane().setDefaultButton(jBtt_Administrador1);   // Enter activa el botón
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jPanel_Izquierdo = new javax.swing.JPanel();
        jL_Tituo1 = new javax.swing.JLabel();
        jP_Barra = new javax.swing.JPanel();
        jL_Subtitulo = new javax.swing.JLabel();
        jL_Pie = new javax.swing.JLabel();
        jPanel_Derecho = new javax.swing.JPanel();
        jL_Guia = new javax.swing.JLabel();
        jP_Tarjeta = new javax.swing.JPanel();
        jBtt_Administrador1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jL_Tituo1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jL_Tituo1.setText("BIENVENIDO ...");

        javax.swing.GroupLayout jP_BarraLayout = new javax.swing.GroupLayout(jP_Barra);
        jP_Barra.setLayout(jP_BarraLayout);
        jP_BarraLayout.setHorizontalGroup(
            jP_BarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jP_BarraLayout.setVerticalGroup(
            jP_BarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        jL_Subtitulo.setText("\"SISTEMA DE CONTROL ESCOLAR\"");

        jL_Pie.setText("\"SISTEMA DE ALUMNOS\"");

        javax.swing.GroupLayout jPanel_IzquierdoLayout = new javax.swing.GroupLayout(jPanel_Izquierdo);
        jPanel_Izquierdo.setLayout(jPanel_IzquierdoLayout);
        jPanel_IzquierdoLayout.setHorizontalGroup(
            jPanel_IzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_IzquierdoLayout.createSequentialGroup()
                .addGroup(jPanel_IzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_IzquierdoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jL_Tituo1, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_IzquierdoLayout.createSequentialGroup()
                        .addGap(72, 72, 72)
                        .addComponent(jP_Barra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_IzquierdoLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jL_Subtitulo))
                    .addGroup(jPanel_IzquierdoLayout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jL_Pie, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_IzquierdoLayout.setVerticalGroup(
            jPanel_IzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_IzquierdoLayout.createSequentialGroup()
                .addGap(138, 138, 138)
                .addComponent(jL_Tituo1)
                .addGap(18, 18, 18)
                .addComponent(jP_Barra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jL_Subtitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
                .addComponent(jL_Pie)
                .addGap(15, 15, 15))
        );

        jL_Guia.setText("\"ACCEDE AL SISTEMA\"");

        jP_Tarjeta.setLayout(new java.awt.BorderLayout());

        jBtt_Administrador1.setText("CONTINUAR");
        jBtt_Administrador1.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                jBtt_Administrador1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                jBtt_Administrador1MouseExited(evt);
            }
        });
        jBtt_Administrador1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBtt_Administrador1ActionPerformed(evt);
            }
        });
        jP_Tarjeta.add(jBtt_Administrador1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel_DerechoLayout = new javax.swing.GroupLayout(jPanel_Derecho);
        jPanel_Derecho.setLayout(jPanel_DerechoLayout);
        jPanel_DerechoLayout.setHorizontalGroup(
            jPanel_DerechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_DerechoLayout.createSequentialGroup()
                .addGroup(jPanel_DerechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_DerechoLayout.createSequentialGroup()
                        .addGap(99, 99, 99)
                        .addComponent(jP_Tarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_DerechoLayout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addComponent(jL_Guia, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(83, Short.MAX_VALUE))
        );
        jPanel_DerechoLayout.setVerticalGroup(
            jPanel_DerechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_DerechoLayout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jL_Guia)
                .addGap(38, 38, 38)
                .addComponent(jP_Tarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(155, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_Izquierdo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(jPanel_Derecho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(64, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel_Izquierdo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel_Derecho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBtt_Administrador1MouseEntered(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jBtt_Administrador1MouseEntered
    {//GEN-HEADEREND:event_jBtt_Administrador1MouseEntered
        // TODO add your handling code here:
        jL_Tituo1.setText("BIENVENIDO ADMINISTRADOR");
    }//GEN-LAST:event_jBtt_Administrador1MouseEntered

    private void jBtt_Administrador1MouseExited(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jBtt_Administrador1MouseExited
    {//GEN-HEADEREND:event_jBtt_Administrador1MouseExited
        // TODO add your handling code here:
        jL_Tituo1.setText("BIENVENIDO ...");
    }//GEN-LAST:event_jBtt_Administrador1MouseExited

    private void jBtt_Administrador1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jBtt_Administrador1ActionPerformed
    {//GEN-HEADEREND:event_jBtt_Administrador1ActionPerformed
        VtnAdministrador administrador = new VtnAdministrador(controllerGeneral);
        administrador.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jBtt_Administrador1ActionPerformed

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtt_Administrador1;
    private javax.swing.JLabel jL_Guia;
    private javax.swing.JLabel jL_Pie;
    private javax.swing.JLabel jL_Subtitulo;
    private javax.swing.JLabel jL_Tituo1;
    private javax.swing.JPanel jP_Barra;
    private javax.swing.JPanel jP_Tarjeta;
    private javax.swing.JPanel jPanel_Derecho;
    private javax.swing.JPanel jPanel_Izquierdo;
    // End of variables declaration//GEN-END:variables
}
