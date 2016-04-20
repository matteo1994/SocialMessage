import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Matteo Marras 827872 on 31/12/2014 IntelliJ IDEA.
 */

public class AggiungiUtenteHandler implements GestoreRichiesta {

    private Map<String,String> param;
    private String answer;
    private boolean correct;

    public AggiungiUtenteHandler(String query) {
        param = new HashMap<String, String>();
        String[] element = query.split("&");
        for(String s : element) {
            String[] pair = s.split("=");
            if(pair.length == 2)
                param.put(pair[0].trim(), pair[1].trim());
        }
    }

    @Override
    public void gestisci() {
        if(param.size() == 1 && param.containsKey("idUtente")) {
            correct = true;
            answer = "Utente " + param.get("idUtente") + " inserito con successo.";
            Utente u = new Utente(param.get("idUtente"));

            //Aggiunta utente al database se non ancora presente
            Set<Utente> c = Server.getUsers();
            boolean insert = c.add(u);
            if(!insert){
                correct = false;
                answer = "Errore, l'utente " + param.get("idUtente") + " e' gia' presente nel database.";
            }
        }
        else{
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
