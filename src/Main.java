
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
    public static void main(String[] args) throws InterruptedException {
        EnviaEmail e = new EnviaEmail();
        String datos[] = e.archivoAArreglo();
        Alerta a = new Alerta();
        //a.setResultado(datos[3]);
        a.setCorreos(datos[0].split(","));
        a.setAsunto(datos[1]);
        a.setResultado(datos[2]);
        if (!datos[3].equals("Request timed out. 50% losses.")) {
//        if (!datos[3].equals("Can not connect to port") && !datos[3].equals("Request timed out. 50% losses.")) {
            System.err.print("Mensaje a enviar:\n");
            String mensajeListo = mensajeEnviar(a);
            Thread.sleep(1000);
            if (e.enviaAVarios(a.getCorreos(), a.getAsunto(), mensajeListo)) {
                System.out.println(mensajeListo + "\n");
                visualizaDialogo(null, "Correo(s) enviado(s) correctamente", "Mensaje enviado", 1000);
            } else {
                System.out.println(mensajeListo + "\n");
                System.out.println(new Date());
                JOptionPane.showMessageDialog(null, "Problemas al enviar correo eléctronico,");
            }
        }
        System.exit(0);
    }

    public static String mensajeEnviar(Alerta a) {
        String mensaje;
        mensaje = a.getResultado();
        return mensaje;
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
