import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Matteo Marras 827872 on 31/12/2014 IntelliJ IDEA.
 */
public class RichiestaHTTP {

    private String protocol;
    private String command;
    private String url;
    private HashMap<String,List<String>> headers;
    private boolean isValid;
    private String errorMessage = "";

    public RichiestaHTTP(InputStream is) {
        List<String> commands = Arrays.asList("GET"); //Crea una lista modulabile di comandi accettati
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try{
            String primaRiga = br.readLine(); //riga di intestazione
            String[] line = primaRiga.split("\\s+");
            if(line.length == 3){
                if(commands.contains(line[0].toUpperCase()) && (line[2].toUpperCase().equals("HTTP/1.0") || line[2].toUpperCase().equals("HTTP/1.1"))){
                    isValid = true;
                    headers = new HashMap<String,List<String>>();
                    protocol = line[2];
                    command = line[0];
                    url=line[1];
                    System.out.print(url);
                    String lineHeader = "";

                    //Analisi dell'header
                    while(!(lineHeader = br.readLine()).equals("")){
                        String[] headerElements = lineHeader.split("\\:");
                        if(headerElements.length == 2){
                            String[] value = headerElements[1].trim().split(",");
                            ArrayList<String> listValue = new ArrayList<>();
                            for (int i = 0; i < value.length; i++) {
                                listValue.add(value[i].trim());
                            }
                            headers.put(headerElements[0].trim(), listValue);
                        }
                    }
                }
                else{
                    isValid = false;
                    errorMessage = "Richiesta HTTP non riconosciuta.";
                }
            }else{
                isValid = false;
                errorMessage = "HTTP ERROR, numero dei parametri inseriti non corretto.";

            }
        } catch (MalformedURLException mURLe){
            System.out.println("Errore nella decodifica dell'URL richiesto.");
        } catch (IOException ioe){
            System.out.println("Errore nella lettura da socket");
        }
    }

    public String getUrl() {
        return url;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getAccessMethod() {
        return command;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
