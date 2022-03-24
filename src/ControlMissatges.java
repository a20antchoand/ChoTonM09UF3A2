import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class ControlMissatges {

    Socket conexio;
    InputStream is;
    OutputStream os;

    public ControlMissatges(Socket conexio) throws IOException {
        this.conexio = conexio;
        this.is = conexio.getInputStream();
        this.os = conexio.getOutputStream();
    }

    public ControlMissatges(InetSocketAddress direccio) throws IOException {
        this.conexio = new Socket();
        conexio.connect(direccio);
    }

    public void enviarMissatge (String missatge) throws IOException {
        byte[] missatgeBytes = missatge.getBytes(StandardCharsets.UTF_8);
        os.write(missatgeBytes, 0, missatgeBytes.length);
        os.write(-1);
        os.flush();
    }

    public void enviarOpcio(OpcionsServidor opcio) throws IOException {
        os.write(new byte[]{opcio.getOpcio()}, 0, 1);
        os.flush();
    }

    protected String esperarMissatge() throws IOException {

        List<Byte> missatgeRebut = new ArrayList<>();
        byte data;
        byte[] resultat;

        while ((data = (byte) is.read()) != -1 || missatgeRebut.isEmpty())
            missatgeRebut.add(data);

        resultat = new byte[missatgeRebut.size()];
        for (int i = 0; i < resultat.length; i++) {
            resultat[i] = missatgeRebut.get(i);
        }

        return new String(resultat);
    }

    public OpcionsServidor esperarOpcio () throws IOException {
        byte opcioByte;

        opcioByte = (byte) is.read();
        return OpcionsServidor.getOpcioFromByte(opcioByte);
    }
}
