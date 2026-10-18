package app;

import controller.AlumnoController;
import views.VtnHome;

public class Main
{

    public static void main(String[] args)
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                AlumnoController controller = new AlumnoController();
                new VtnHome(controller).setVisible(true);
            }
        });
    }

}
