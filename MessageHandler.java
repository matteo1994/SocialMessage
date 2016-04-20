import java.util.*;

/**
 * Created by Matteo Marras 827872 on 31/12/2014 IntelliJ IDEA.
 */

public class MessageHandler implements Runnable {


    /*
     *  Questo metodo viene richiamato periodicamente e verifica se i messaggi presenti in memoria sono scaduti, nel
     *   caso in cui ne trovasse uno scaduto lo elimina automaticamente dal database.
     */
    private synchronized void checkMessages(Map<Integer,Messaggio> messagges){
        Date now = new Date();
        Map<Integer,Messaggio> tmp = new HashMap<Integer,Messaggio>(); //Utilizzo una copia della mappa come iteratore in modo da scongiurare qualsiasi tipo di ConcurrentModificationException
        tmp.putAll(messagges);
        Collection<Messaggio> iter = tmp.values();
        for(Messaggio m : iter){
            if(now.after(m.getDeath().getTime())){
                messagges.remove(m.getId());      //rimuovo l'oggetto relativo dalla collezione di messaggi
                m.getDest().getInBox().remove(m.getId());
                //System.out.println("Messaggio cancellato");
            }
        }
    }

    //Compone gli ultimi dettagli (id,durata) relativi al messaggio e lo inserisce nel server.
    public static boolean addMessage(Messaggio m){
        if(m != null){
            m.setRise(new GregorianCalendar());
            m.setDeath(new GregorianCalendar());
            m.getDeath().add(Calendar.MILLISECOND, m.getTtl() * 1000);
            m.getDest().addInBox(m);
            Server.getMessagges().put(m.getId(),m);
            return true;
        }
        return false;
    }

    @Override
    public void run() {   //Questo thread controlla ogni secondo i messaggi in modo da rilevare i messaggi scaduti ed eliminarli.
        int n = 0;
        while(true){
            try {
                //System.out.println(++n);
                Thread.sleep(1000);  //dorme per un secondo in modo da scandire i secondi come un orologio limitando le inutili computazioni
                checkMessages(Server.getMessagges());
            }catch(InterruptedException ie){
                ie.printStackTrace();
            }
        }
    }
}
