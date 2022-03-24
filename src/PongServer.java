import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PongServer {

    private ServerSocket conexioServidor;

    private Boolean pararServidor = false;
    private int numeroMissatge;

    public PongServer() throws IOException {
        this.conexioServidor = new ServerSocket(2022);
        System.out.println("Servidor escoltant en el port: " + conexioServidor.getLocalPort());
        this.numeroMissatge = 1;
    }

    public synchronized int getNumeroMissatge() {
        return numeroMissatge;
    }

    public synchronized void setNumeroMissatge(int numeroMissatge) {
        this.numeroMissatge = numeroMissatge;
    }

    public static void main(String[] args) {

        PongServer servidor;

        try {
            servidor = new PongServer();
            servidor.escoltarClients();

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    public void escoltarClients () {

        ExecutorService conexioExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);

        while (!pararServidor) {
            try {
                controladorConexioClients controladorClients;
                Socket conexio = conexioServidor.accept();

                controladorClients = new controladorConexioClients(this, conexio);
                conexioExecutor.execute(controladorClients);

            }catch (SocketException s){
                    if (!s.getMessage().equals("Socket closed")) {
                        s.printStackTrace();
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        conexioExecutor.shutdown();

    }

    public void pararServidor() {
        try {
            conexioServidor.close();
            this.pararServidor = true;
            System.out.println("Servidor tancat.");
        } catch (IOException e) {
            System.out.println("No s'ha pogut tancar el servidor: " + e.getMessage());
        }

    }
}
