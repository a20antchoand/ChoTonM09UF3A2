import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ControladorConexioClients extends ControlMissatges implements Runnable {

    final String RESPOSTA_SERVIDOR = " My name is Pong.";
    final PongServer SERVIDOR;

    public ControladorConexioClients(PongServer pong, Socket conexio) throws IOException {
        super(conexio);
        this.SERVIDOR = pong;
        this.conexio = conexio;
        this.is = conexio.getInputStream();
        this.os = conexio.getOutputStream();

        System.out.println("[" + conexio.getInetAddress().getHostAddress() + ":" + conexio.getPort() + "] -> Conexio establerta.");

    }

    @Override
    public void run() {
        boolean pararConexio = false;
        try {
            while (!pararConexio) {

                OpcionsServidor opcio;

                opcio = esperarOpcio();

                switch (opcio) {
                    case ENVIAR_MISSATGE -> esperarMissatgeClient();

                    case ENVIAR_CHISTE -> esperarChiste();

                    case TANCAR_CONEXIO -> {
                        conexio.close();
                        pararConexio = true;
                        System.out.println("[" + conexio.getInetAddress().getHostAddress() + ":" + conexio.getPort() + "] -> Conexió tancada.");
                    }

                    case TANCAR_SERVIDOR -> {
                        conexio.close();
                        pararConexio = true;
                        tancarConexio(true);
                    }
                }

            }
        } catch (SocketException s){
            if (!s.getMessage().equals("Connection reset"))
                s.printStackTrace();
            else
                try {
                    tancarConexio(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tancarConexio(boolean tancarServidor) {

        System.out.println("[" + conexio.getInetAddress().getHostAddress() + ":" + conexio.getPort() + "] -> Conexio tancada.");

        if (tancarServidor)
            SERVIDOR.pararServidor();
    }

    public void esperarMissatgeClient() throws IOException {

        String mensajeCliente = "";
        StringBuilder missatgeEnviat = new StringBuilder();

        mensajeCliente = super.esperarMissatge();

        missatgeEnviat.append(SERVIDOR.getNumeroMissatge() + ":: M'han dit: ")
                .append(mensajeCliente)
                .append(" i jo responc:")
                .append(RESPOSTA_SERVIDOR);

        System.out.println(missatgeEnviat);

        super.enviarMissatge(RESPOSTA_SERVIDOR);

        SERVIDOR.setNumeroMissatge(SERVIDOR.getNumeroMissatge() + 1);

    }

    private void esperarChiste() throws IOException {

        String mensajeCliente = "";

        mensajeCliente = super.esperarMissatge();

        System.out.println("[" + conexio.getInetAddress().getHostAddress() + ":" + conexio.getPort() + "] -> " + mensajeCliente);

        switch (mensajeCliente) {
            case "Qui es?" -> super.enviarMissatge("Advocat");
            case "Quin advocat?" -> super.enviarMissatge("El que tinc aquí penjat!");

            case "Quien es?" -> super.enviarMissatge("Thomas");
            case "¿Qué Thomas?" -> super.enviarMissatge("Yo un cubata, ¿y tú?");

            case "Who is" -> super.enviarMissatge("Atch");
            case "Atch who?" -> super.enviarMissatge("Bless you!");

            default -> super.enviarMissatge("Knock, Knock");
        }

    }

}



/*        else if (mensajeCliente.equals("Qui és?") || mensajeCliente.equals("Qui es?") || mensajeCliente.equals("Qui es") ||
            mensajeCliente.equals("qui és?") || mensajeCliente.equals("qui es?") || mensajeCliente.equals("qui es"))
            super.enviarMissatge("Advocat");

        else if (mensajeCliente.equals("Quin advocat?") || mensajeCliente.equals("Quin advocat") ||
                mensajeCliente.equals("quin advocat?") || mensajeCliente.equals("quin advocat"))
            super.enviarMissatge("El que tinc aquí penjat!");

        else
            super.enviarMissatge("Esperava més de tu...");*/