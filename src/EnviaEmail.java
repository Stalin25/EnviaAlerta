
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class EnviaEmail {

    String miCorreo = "soporte@kradac.com";
    String miContraseña = "kradacloja";
    String servidorSMTP = "smtp.gmail.com";
    String puertoEnvio = "465";

    String asunto;
    String cuerpo;

    //metodo que recibe y envia el email
    public boolean EnviaEmail(String mailReceptor[], String asunto, String cuerpo) {
        boolean res = false;
        this.asunto = asunto;
        this.cuerpo = cuerpo;
        Properties props = new Properties();//propiedades a agragar
        props.put("mail.smtp.user", this.miCorreo);//correo origen
        props.put("mail.smtp.host", servidorSMTP);//tipo de servidor
        props.put("mail.smtp.port", puertoEnvio);//puesto de salida
        props.put("mail.smtp.starttls.enable", "true");//inicializar el servidor
        props.put("mail.smtp.auth", "true");//autentificacion
        props.put("mail.smtp.socketFactory.port", puertoEnvio);//activar el puerto
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        SecurityManager security = System.getSecurityManager();

        try {
            Authenticator auth = new autentificadorSMTP();//autentificar el correo
            Session session = Session.getInstance(props, auth);//se inica una session
            // session.setDebug(true);

            MimeMessage msg = new MimeMessage(session);//se crea un objeto donde ira la estructura del correo
            // msg.setText(this.cuerpo);//seteo el cuertpo del mensaje
            msg.setSubject(this.asunto);//setea asunto (opcional)
            msg.setContentID("KRADAC");
            msg.setHeader("Mensaje", "Correcto");
            msg.setContent(this.cuerpo, "text/html");
            msg.setFrom(new InternetAddress(this.miCorreo));//agrega la la propiedad del correo origen
            for (String cadaEmail : mailReceptor) {
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(cadaEmail)); //agrega el destinatario
            }
            Transport.send(msg);//envia el mensaje
            res = true;
        } catch (MessagingException mex) {//en caso de que ocurra un error se crea una excepcion
            System.err.println(mex);
        }
        return res;
    }

    private class autentificadorSMTP extends Authenticator {

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(miCorreo, miContraseña);//autentifica tanto el correo como la contraseña
        }
    }

    public void escribir(String nombreArchivo, String texto) {
        File f;
        f = new File(nombreArchivo);
        try {
            FileWriter w = new FileWriter(f);
            try (BufferedWriter bw = new BufferedWriter(w); PrintWriter wr = new PrintWriter(bw)) {
                wr.write(texto);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @SuppressWarnings("empty-statement")
    public String[] archivoAArreglo() {

        String para = "diego.romero@kradac.com", asun = "Sin descripción en TNM", men = "Ningun mensaje configurado en TNM", datos[] = new String[4];
        try {
            FileInputStream fstream = new FileInputStream("src\\datos.tnm"); // Abrimos el archivo
            DataInputStream entrada = new DataInputStream(fstream); // Creamos el objeto de entrada
            BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada)); // Creamos el Buffer de Lectura
            String strLinea;
            String par = buffer.readLine();

            while ((strLinea = buffer.readLine()) != null) {     // Leer el archivo linea por linea
                men += strLinea + "\n";      // Agregamos la línea en la variable men
            }

            String lin[] = men.split(":");
            men = lin[1];
            for (int i = 2; i < lin.length; i++) {
                men += ":" + lin[i];
            }
            lin = men.split("\n");
            men = lin[lin.length - 1];
            if (!par.isEmpty()) {
                para = par;
                para = para.split(":")[1];
                if (new Date().getMinutes() < 15) {
                    escribir("src\\datos.tnm", "Para:" + para + "\nMensaje:" + men+"\n");
                }
            }

            if (!men.split(";")[1].split(":")[1].equals(" ")) {
                asun = men.split(";")[1].split(":")[1];
            }
            asun += "\tEnviado: " + fechaFormato(new Date());
            men = men.split(";")[0];
            String fechaHora = men.split("]")[0].substring(1);
            men = men.split("]")[1];
            men = "<table><th align='right'>Fecha y hora:</th><td>"
                    + fechaHora + "</td><tr>" + men + "<br>";
            men = men + "<br><br>Mensaje enviado desde:<br>"
                    + "<br>Nombre de equipo que envió email: " + obtieneHost()
                    + "<br>Ip local de " + obtieneHost() + " : " + obtieneIp();;
            datos[0] = para;
            datos[1] = asun;
            datos[2] = men;
            String resultado = men.split("'")[17];
            datos[3] = resultado;
            entrada.close();   // Cerramos el archivo
        } catch (IOException e) { //Catch de excepciones
            System.err.println(e);
        }

        return datos;
    }

    public boolean enviaAVarios(String correos[], String asunto, String mensaje) {
        return EnviaEmail(correos, asunto, mensaje);
    }

    public String fechaFormato(Date d) {
        Date fecha = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaActual = formateador.format(fecha);
        return fechaActual;
    }

    public String obtieneHost() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostName();
        } catch (UnknownHostException ex) {
            return null;
        }
    }

    public String obtieneIp() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            // Cogemos la IP 
            byte[] bIPAddress = address.getAddress();
            // IP en formato String
            String sIPAddress = "";
            for (int x = 0; x < bIPAddress.length; x++) {
                if (x > 0) {
                    // A todos los numeros les anteponemos
                    // un punto menos al primero    
                    sIPAddress += ".";
                }
                // Jugamos con los bytes y cambiamos el bit del signo
                sIPAddress += bIPAddress[x] & 255;
            }
            return sIPAddress;
        } catch (UnknownHostException ex) {
            return null;
        }
    }
}
