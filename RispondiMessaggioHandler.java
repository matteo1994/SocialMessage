import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matteo Marras 827872 on 31/12/2014 IntelliJ IDEA.
 */


public class RispondiMessaggioHandler implements GestoreRichiesta {

    private Map<String, String> param;
    private String answer;
    private boolean correct;

    public RispondiMessaggioHandler(String query) {
        param = new HashMap<String, String>();
        String[] element = query.split("&");
        for (String s : element) {
            String[] pair = s.split("=");
            if (pair.length == 2)
                param.put(pair[0].trim(), pair[1].trim());
        }
    }

    @Override
    public void gestisci() {
        if (param.size() == 4 && param.containsKey("idUtente") && param.containsKey("idMessaggio") && param.containsKey("text") && param.containsKey("durata")) {

            //Verifico la correttezza sintattico e logica della durata
            correct = true;
            int ttl = 0;
            try {
                ttl = Integer.parseInt(param.get("durata"));
            } catch (NumberFormatException nfe) {
                correct = false;
            }

            //Ricavo l'utente relativo alla risorsa richiesta, se presente nel database
            Utente richiedente = Server.getUser(param.get("idUtente"));

            //controlli di coerenza, e invio del messaggio
            if( correct && ttl > 0) {
                correct = false;
                if (richiedente != null){
                    Map<Integer, Messaggio> mess = richiedente.getInBox();

                    if (mess.containsKey(Integer.parseInt(param.get("idMessaggio")))) {

                        //Ricavo dal database il messaggio a cui rispondere
                        Messaggio m = mess.get(Integer.parseInt(param.get("idMessaggio")));

                        //Utilizzo un blocco syncronized sul messaggio cui devo modificare la durata, fornirò dovute motivazioni
                        //di questo blocco in sede d'orale
                        synchronized(m){
                            m.getDeath().add(Calendar.MILLISECOND, Integer.parseInt(param.get("durata")) * 1000);
                        }

                        Messaggio g;
                        MessageHandler.addMessage(g = new Messaggio(Integer.parseInt(param.get("durata")), param.get("text"), richiedente, m.getOwner()));
                        correct = true;
                        answer = "id_mes: " + g.getId();
                    } else {
                        answer = "L'utente " + richiedente.getIdUtente() + " non possiede nella sua casella di posta il messaggio specificato, impossibile rispondere.";
                    }
                } else {
                    answer = "L'utente " + param.get("idUtente") + " e' inesistente.";
                }
            } else {
                answer = (!correct) ? "Errore, la durata deve essere un numero." : "Errore, inserita una durata negativa.";
            }
        } else{
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
