
import java.awt.Component;
import java.util.Date;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author KRC
 */
public class Main {

    private static JOptionPane option = new JOptionPane("", JOptionPane.INFORMATION_MESSAGE);
    private static JDialog dialogo = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EnviaEmail e = new EnviaEmail();
        String datos[] = e.archivoAArreglo();
        if (!datos[3].equals("Can not connect to port")) {
            System.err.print("Mensaje a enviar:\n");
            if (e.enviaAVarios(datos[0].split(","), datos[1], datos[2])) {
                System.out.println(datos[2] + "\n");
                visualizaDialogo(null, "Correo(s) enviado(s) correctamente", "Mensaje enviado", 1000);
            } else {
                System.out.println(datos[2] + "\n");
                System.out.println(new Date());
                JOptionPane.showMessageDialog(null, "Problemas al enviar correo eléctronico,");
                
            }
        }
        System.exit(0);
    }

    public static void visualizaDialogo(Component padre, String texto, String titulo, final long timeout) {
        option.setMessage(texto);
        if (null == dialogo) {
            dialogo = option.createDialog(padre, titulo);
        } else {
            dialogo.setTitle(titulo);
        }
        Thread hilo = new Thread() {
            public void run() {
                try {
                    Thread.sleep(timeout);
                    if (dialogo.isVisible()) {
                        dialogo.setVisible(false);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        hilo.start();
        dialogo.setVisible(true);
    }
}
