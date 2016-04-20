import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Matteo Marras 827872 on 31/12/2014 IntelliJ IDEA.
 */

public class AggiungiAmicoHandler implements GestoreRichiesta {

    private Map<String,String> param;
    private String answer;
    private boolean correct;

    public AggiungiAmicoHandler(String query) {
        param = new HashMap<String, String>();
        String[] element = query.split("&");
        for(String s: element) {
            String[] pair = s.split("=");
            if(pair.length == 2)
                param.put(pair[0].trim(), pair[1].trim());
        }
    }

    @Override
    public void gestisci() {
        if(param.size()==2 && param.containsKey("idUtente") && param.containsKey("idAmico")){
            Set<Utente> s = Server.getUsers();

            //ricavo dal database gli utenti richiesti se presenti
            Utente richiedente = Server.getUser(param.get("idUtente"));
            Utente bersaglio = Server.getUser(param.get("idAmico"));

            //relativi controlli di coerenza e aggiunta della relazione di amicizia tra gli utenti
            if(richiedente != null && bersaglio != null && !richiedente.equals(bersaglio)){
                correct = true;
                answer = "Amicizia con " + bersaglio.getIdUtente() + " creata con successo.";
                if(!(richiedente.addFriend(bersaglio) && bersaglio.addFriend(richiedente))) {
                    correct = false;
                    answer = richiedente.getIdUtente() + " e " + bersaglio.getIdUtente() + " sono gia' amici.";
                }
            }
            else{
                correct = false;
                answer = (richiedente == null) ? "L'utente " + param.get("idUtente") + " e' inesistente." :  "L'utente " + param.get("idAmico") + " e' inesistente.";
            }
        }
        else{
            correct = false;
            answer = (param.size() < 2) ? "Errore, parametri mancanti." : "Errore nei parametri inseriti.";
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
