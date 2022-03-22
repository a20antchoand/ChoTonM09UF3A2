import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ControladorConexioClients extends ControlMissatges implements Runnable {

    final static String CAT_1 = "Qui es?";
    final static String CAT_2 = "Advocat";
    final static String CAT_3 = "Quin advocat?";
    final static String CAT_4 = "El que tinc aqui penjat!";

    final static String ES_1 = "Quien es?";
    final static String ES_2 = "Tomas";
    final static String ES_3 = "Que Tomas?";
    final static String ES_4 = "Yo un cubata, ¿y tu?";

    final static String EN_1 = "Who is?";
    final static String EN_2 = "Atch";
    final static String EN_3 = "Atch who?";
    final static String EN_4 = "Bless you!";

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
                case CAT_1 -> super.enviarMissatge(CAT_2);
                case CAT_3 -> super.enviarMissatge(CAT_4);

                case ES_1 -> super.enviarMissatge(ES_2);
                case ES_3 -> super.enviarMissatge(ES_4);

                case EN_1 -> super.enviarMissatge(EN_2);
                case EN_3 -> super.enviarMissatge(EN_4);

                default -> super.enviarMissatge("OPT: ( " + CAT_1 + "," + ES_1 + "," + EN_1 + " )");
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