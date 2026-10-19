package views;

import controller.AlumnoController;

public class VtnHome extends javax.swing.JFrame
{

    private AlumnoController controllerGeneral;

    public VtnHome(AlumnoController controller)
    {
        initComponents();
        controllerGeneral = controller;
        Estilos.estilizarVentana(this);   // fondo crema + centrada
        setTitle("Inicio");

        Estilos.estilizarTitulo(jL_Tituo);
        jL_Tituo.setFont(Estilos.fuente(java.awt.Font.PLAIN, 38)); // más chico para que quepa

        Estilos.estilizarBoton(jBtt_Estudiante);
        Estilos.estilizarBoton(jBtt_Profesor);
        Estilos.estilizarBoton(jBtt_Administrador);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jL_Tituo = new javax.swing.JLabel();
        jBtt_Estudiante = new javax.swing.JButton();
        jBtt_Profesor = new javax.swing.JButton();
        jBtt_Administrador = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jL_Tituo.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jL_Tituo.setText("BIENVENIDO ...");

        jBtt_Estudiante.setText("ESTUDIANTE");
        jBtt_Estudiante.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                jBtt_EstudianteMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                jBtt_EstudianteMouseExited(evt);
            }
        });
        jBtt_Estudiante.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBtt_EstudianteActionPerformed(evt);
            }
        });

        jBtt_Profesor.setText("PROFESOR");
        jBtt_Profesor.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                jBtt_ProfesorMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                jBtt_ProfesorMouseExited(evt);
            }
        });

        jBtt_Administrador.setText("ADMINISTRADOR");
        jBtt_Administrador.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                jBtt_AdministradorMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                jBtt_AdministradorMouseExited(evt);
            }
        });
        jBtt_Administrador.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBtt_AdministradorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(258, 258, 258)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jBtt_Estudiante, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtt_Profesor, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtt_Administrador, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(259, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jL_Tituo, javax.swing.GroupLayout.PREFERRED_SIZE, 495, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jL_Tituo)
                .addGap(18, 18, 18)
                .addComponent(jBtt_Estudiante, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(jBtt_Profesor, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jBtt_Administrador, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(98, 98, 98))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBtt_EstudianteActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jBtt_EstudianteActionPerformed
    {//GEN-HEADEREND:event_jBtt_EstudianteActionPerformed

    }//GEN-LAST:event_jBtt_EstudianteActionPerformed

    private void jBtt_EstudianteMouseEntered(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jBtt_EstudianteMouseEntered
    {//GEN-HEADEREND:event_jBtt_EstudianteMouseEntered
        // TODO add your handling code here:
        jL_Tituo.setText("BIENVENIDO ESTUDIANTE");

    }//GEN-LAST:event_jBtt_EstudianteMouseEntered

    private void jBtt_EstudianteMouseExited(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jBtt_EstudianteMouseExited
    {//GEN-HEADEREND:event_jBtt_EstudianteMouseExited
        // TODO add your handling code here:
        jL_Tituo.setText("BIENVENIDO ...");
    }//GEN-LAST:event_jBtt_EstudianteMouseExited

    private void jBtt_ProfesorMouseEntered(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jBtt_ProfesorMouseEntered
    {//GEN-HEADEREND:event_jBtt_ProfesorMouseEntered
        // TODO add your handling code here:
        jL_Tituo.setText("BIENVENIDO PROFESOR");
    }//GEN-LAST:event_jBtt_ProfesorMouseEntered

    private void jBtt_ProfesorMouseExited(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jBtt_ProfesorMouseExited
    {//GEN-HEADEREND:event_jBtt_ProfesorMouseExited
        // TODO add your handling code here:
        jL_Tituo.setText("BIENVENIDO ...");
    }//GEN-LAST:event_jBtt_ProfesorMouseExited

    private void jBtt_AdministradorMouseEntered(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jBtt_AdministradorMouseEntered
    {//GEN-HEADEREND:event_jBtt_AdministradorMouseEntered
        // TODO add your handling code here:
        jL_Tituo.setText("BIENVENIDO ADMINISTRADOR");
    }//GEN-LAST:event_jBtt_AdministradorMouseEntered

    private void jBtt_AdministradorMouseExited(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jBtt_AdministradorMouseExited
    {//GEN-HEADEREND:event_jBtt_AdministradorMouseExited
        // TODO add your handling code here:
        jL_Tituo.setText("BIENVENIDO ...");
    }//GEN-LAST:event_jBtt_AdministradorMouseExited

    private void jBtt_AdministradorActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jBtt_AdministradorActionPerformed
    {//GEN-HEADEREND:event_jBtt_AdministradorActionPerformed
        VtnAdministrador administrador = new VtnAdministrador(controllerGeneral);
        administrador.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jBtt_AdministradorActionPerformed

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtt_Administrador;
    private javax.swing.JButton jBtt_Estudiante;
    private javax.swing.JButton jBtt_Profesor;
    private javax.swing.JLabel jL_Tituo;
    // End of variables declaration//GEN-END:variables
}
