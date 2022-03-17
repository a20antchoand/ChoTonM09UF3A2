import java.util.Arrays;

public enum OpcionsServidor {

    ENVIAR_MISSATGE((byte) 1),
    ENVIAR_CHISTE((byte) 2),
    TANCAR_SERVIDOR((byte) 3),
    TANCAR_CONEXIO((byte) 4);

     private byte opcio;
     public byte getOpcio() {return opcio;}

    OpcionsServidor(byte opcio) {
         this.opcio = opcio;
    }

    public static OpcionsServidor getOpcioFromByte(byte opcioBytes) {
        OpcionsServidor resultat = Arrays.stream(OpcionsServidor.values())
                .filter(comandosServidor -> comandosServidor.getOpcio() == opcioBytes)
                .findAny()
                .orElse(null);

        return resultat;
    }

}
