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
    final String RESPOSTA_CHISTE = " Knock, Knock";
    final PongServer SERVIDOR;

    public ControladorConexioClients(PongServer pong, Socket conexio) throws IOException {
        super(conexio);
        this.SERVIDOR = pong;
        this.conexio = conexio;
        this.is = conexio.getInputStream();
        this.os = conexio.getOutputStream();

        System.out.println("S'ha conectat l'usuari amb la ip: "
                + conexio.getInetAddress().getHostAddress()
                + " en el port: "
                + conexio.getPort());

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

                    case ENVIAR_CHISTE -> enviarChiste();

                    case TANCAR_CONEXIO -> {
                        conexio.close();
                        pararConexio = true;
                        System.out.println("Conexió tancada con el usuario con ip: " + conexio.getInetAddress().getHostAddress());
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

        System.out.println("Conexio tancada amb l'usuari amb ip: " + conexio.getInetAddress().getHostAddress());

        if (tancarServidor)
            SERVIDOR.pararServidor();
    }

    public void esperarMissatgeClient() throws IOException {

        String mensajeCliente = "";
        StringBuilder missatgeEnviat = new StringBuilder();

        mensajeCliente = super.esperarMissatge();

        missatgeEnviat.append(SERVIDOR.getNumeroMissatge() + ":: M'han dit: ")
                .append(new String(mensajeCliente))
                .append(" i jo responc:")
                .append(RESPOSTA_SERVIDOR);

        super.enviarMissatge(RESPOSTA_SERVIDOR);

        SERVIDOR.setNumeroMissatge(SERVIDOR.getNumeroMissatge() + 1);

    }

    private void enviarChiste() throws IOException {

        String mensajeCliente = "";
        StringBuilder missatgeEnviat = new StringBuilder();

        mensajeCliente = super.esperarMissatge();

        missatgeEnviat.append(SERVIDOR.getNumeroMissatge() + ":: M'han dit: ")
                .append(new String(mensajeCliente))
                .append(" i jo responc:")
                .append(RESPOSTA_SERVIDOR);

        super.enviarMissatge("Knock, Knock");


    }

}
