import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matteo Marras 827872 on 31/12/2014 IntelliJ IDEA.
 */


public class VediMessaggiHandler implements GestoreRichiesta {

    private Map<String,String> param;
    private String answer;
    private boolean correct;

    public VediMessaggiHandler(String query) {
        param = new HashMap<String, String>();
        String[] element = query.split("&");
        for(String s: element) {
            String[] pair = s.split("=");
            if(pair.length == 2)
                param.put(pair[0].trim(), pair[1].trim());
        }
    }

    @Override
    public void gestisci(){
        if(param.size() == 1 && param.containsKey("idUtente")){
            Utente richiedente = Server.getUser(param.get("idUtente"));
            if(richiedente != null){
                correct = true;
                answer = richiedente.printMessaggi();
            }
            else{
                correct = false;
                answer = "L'utente " + param.get("idUtente") + " e' inesistente.";
            }
        }
        else{
            correct = false;
            answer = (param.size() < 1) ? "Errore, parametri mancanti." : "Errore nei parametri inseriti.";
        }

    }

    @Override
    public String getResponse() {
        return answer;
    }

    @Override
    public boolean isCorrect() {
        return correct;
    }
}
