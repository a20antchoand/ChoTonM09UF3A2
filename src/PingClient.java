import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PingClient extends ControlMissatges{


    public PingClient() throws IOException {
        super(new InetSocketAddress("127.0.0.1", 2022));

        is = conexio.getInputStream();
        os = conexio.getOutputStream();
    }

    public static void main(String[] args) throws IOException {

        PingClient client = new PingClient();

        client.MostrarMenuInici();

    }

    public void MostrarMenuInici() throws IOException {

        Scanner s = new Scanner(System.in);
        String opcio;
        StringBuilder menu = new StringBuilder();
        boolean salir = false;

        menu.append("Conectat correctament al servidor ")
                .append(conexio.getInetAddress().getHostAddress())
                .append(" al port ")
                .append(conexio.getPort() + "\n\n");

        menu.append("//---------------- MENU ----------------\\\\ \n");
        menu.append("|| 1. Enviar missatge al servidor       ||\n");
        menu.append("|| 2. Pedir chiste                      ||\n");
        menu.append("|| 3. Tancar conexió i sortir           ||\n");
        menu.append("|| 4. Apagar servidor                   ||\n");
        menu.append("\\\\======================================//\n\n");
        menu.append("Opcio: ");


        while (!salir) {
            System.out.print(menu);

            opcio = s.next();

            switch (opcio) {
                case "1" -> enviarMissatge();
                case "2" -> pedirChiste();
                case "3" -> {
                    tancarConexio();
                    salir = true;
                }
                case "4" -> {
                    tancarServidor();
                    salir = true;
                }
            }

        }

    }

    public void pedirChiste() throws IOException {

        Scanner s = new Scanner(System.in);
        String missatge = "L'usuari demana un acudit";
        String resultat;

        do {
            enviarOpcio(OpcionsServidor.ENVIAR_CHISTE);

            enviarMissatge(missatge);

            resultat = esperarMissatge();

            System.out.println("RESPOSTA: " + resultat);

            if (!resultat.equals("El que tinc aquí penjat!") &&
                !resultat.equals("Yo un cubata, ¿y tu?") &&
                !resultat.equals("Bless you!")) {

                System.out.print("USUARI: ");
                missatge = s.nextLine();

                if (missatge.equals(""))
                    missatge = "empty";

            }

        } while (!resultat.equals(ControladorConexioClients.CAT_4) || !resultat.equals(ControladorConexioClients.ES_4) || !resultat.equals(ControladorConexioClients.EN_4));
    }

    public void enviarMissatge() throws IOException {

        Scanner s = new Scanner(System.in);
        String missatge;

        System.out.print("Indica el missatge: ");
        missatge = s.nextLine();

        enviarOpcio(OpcionsServidor.ENVIAR_MISSATGE);

        enviarMissatge(missatge);

        String resultat = esperarMissatge();

        System.out.println("Servidor: " + resultat);

    }

    public void tancarConexio () throws IOException {

        os.write(new byte[] {OpcionsServidor.TANCAR_CONEXIO.getOpcio()}, 0, 1);
        os.flush();
        conexio.close();

    }

    public void tancarServidor() throws IOException {

        os.write(new byte[] {OpcionsServidor.TANCAR_SERVIDOR.getOpcio()}, 0, 1);
        os.flush();
        conexio.close();

    }


}
