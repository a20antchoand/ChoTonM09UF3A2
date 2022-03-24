import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Scanner;

public class ControlAcudits {

    private final Dictionary<String, String> Acudits = new Hashtable<>() {{
        put("es_1", "Thomas");
        put("es_2", "Yo un cubata, ¿y tú?");
        put("cat_1", "Advocat");
        put("cat_2", "El que tinc aquí penjat");
        put("en_1", "Atch");
        put("en_2", "Bless you!");
    }};

    private final Dictionary<String, String> RespostesClient = new Hashtable<>() {{
        put("es_0", "¿Quién es?");
        put("es_1", "¿Qué Thomas?");
        put("cat_0", "Qui és?");
        put("cat_1", "Quin advocat?");
        put("en_0", "Who is?");
        put("en_1", "Atch who?");
    }};

    private final ControlMissatges controlMissatges;

    String idioma;

    public ControlAcudits(ControlMissatges controlMissatges) {

        this.controlMissatges = controlMissatges;

    }




    public void esperarAcudit() throws IOException {
        Scanner s = new Scanner(System.in);
        String respostaServidor;
        String missatge;
        boolean continuar = true;

        controlMissatges.enviarOpcio(OpcionsServidor.ENVIAR_ACUDIT);

        while (continuar) {
            respostaServidor = controlMissatges.esperarMissatge();

            if (respostaServidor.matches(".*--1")) {
                System.out.println(respostaServidor.substring(0, respostaServidor.length() - 3));
                continuar = false;
            }
            else {
                System.out.print(respostaServidor + "\nCLIENT: ");
                missatge = s.nextLine();
                controlMissatges.enviarMissatge(missatge);
            }
        }
    }


    public void generarAcudit() throws IOException {
        String respostaClient;
        String resultatComprobacio;
        int i = 0;
        boolean error = false;


        while (i <= 2){

            String missatge;

            if (i == 0) {
                missatge = "knock, Knock!";
            }
            else {
                missatge = Acudits.get(idioma + "_" + i);
            }

            if (!error) {
                if (i < 2) {
                    controlMissatges.enviarMissatge("SERVIDOR: " + missatge);
                }
                else {
                    controlMissatges.enviarMissatge("SERVIDOR: " + missatge + " --1");
                }
            }
            else {

                missatge = RespostesClient.get(idioma + "_" + i);

                if (idioma == null) {
                    controlMissatges.enviarMissatge("SERVIDOR: No has introudit cap inici d'acudit valid. (Qui és?, ¿Quién és?, Who is?)");
                }
                else if (idioma.equals("es")) {
                    controlMissatges.enviarMissatge("SERVIDOR: La respuesta esperada és: " + missatge);
                }
                else if (idioma.equals("cat")) {
                    controlMissatges.enviarMissatge("SERVIDOR: La resposta esperada: " + missatge);
                }
                else if (idioma.equals("en")) {
                    controlMissatges.enviarMissatge("SERVER: The desidered answer is: " + missatge);
                }


            }

            error = false;

            if (i < 2) {
                respostaClient = controlMissatges.esperarMissatge();
                resultatComprobacio = comprobarResposta(i, respostaClient);

                if (resultatComprobacio == null) {
                    error = true;
                }
                else if (resultatComprobacio != idioma && i > 0) {
                    error = true;
                }
                else {
                    if (i == 0) {
                        idioma = resultatComprobacio;
                    }
                    i++;
                }
            }

            else i++;
        }
    }


    private String comprobarResposta(int i, String text) {
        if (text.equals(RespostesClient.get("es_" + i))) {
            return "es";
        }
        else if (text.equals(RespostesClient.get("cat_" + i))) {
            return "cat";
        }
        else if (text.equals(RespostesClient.get("en_" + i))) {
            return "en";
        }
        else {
            return null;
        }
    }
}
