import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matteo Marras 827872 on 31/12/2014 IntelliJ IDEA.
 */

public class InviaMessaggioHandler implements GestoreRichiesta {

    private Map<String,String> param;
    private String answer;
    private boolean correct;

    public InviaMessaggioHandler(String query) {
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
        if(param.size() == 4 && param.containsKey("idUtente") && param.containsKey("idAmico") && param.containsKey("text") && param.containsKey("durata")){

            //verifico la correttezza sintattica e logica della durata
            correct = true;
            int ttl = 0;
            try {
                ttl = Integer.parseInt(param.get("durata"));
            } catch (NumberFormatException nfe) {
                correct = false;
            }

            //Ricavo gli utenti richiesti dal database, se presenti
            Utente richiedente = Server.getUser(param.get("idUtente"));
            Utente bersaglio = Server.getUser(param.get("idAmico"));
            //System.out.println(richiedente);
            //System.out.println(bersaglio);

            //controlli di coerenza dei dati inseriti, creazione del messaggio, invio e aggiunta al database
            if(correct && ttl > 0) {
                correct = false;
                if (richiedente != null && bersaglio != null) {
                    if (!richiedente.equals(bersaglio)) {
                        if (richiedente.areFriend(bersaglio)) {
                            Messaggio m = new Messaggio(Integer.parseInt(param.get("durata")), param.get("text"), richiedente, bersaglio);

                            //Utilizzo la classe MassageHandler che si occuperà di gestire le operazioni relative ai messaggi
                            MessageHandler.addMessage(m);
                            correct = true;
                            answer = "id_mes: " + m.getId();
                        } else {
                            answer = "Invio fallito, " + richiedente.getIdUtente() + " e " + bersaglio.getIdUtente() + " non sono amici.";
                        }
                    } else {
                        answer = richiedente.getIdUtente() + " non puo' inviare un messaggio a se stesso.";
                    }
                } else {
                    answer = (richiedente == null) ? "L'utente " + param.get("idUtente") + " e' inesistente" : "L'utente " + param.get("idAmico") + " e' inesistente";
                }
            }else{
                answer = (!correct) ? "Errore, la durata deve essere un numero." : "Errore, inserita una durata negativa.";
            }
        }
        else{
            answer = (param.size() < 4) ? "Errore, parametri mancanti." : "Errore nei parametri inseriti.";
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
