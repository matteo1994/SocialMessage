import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Matteo Marras 827872 on 31/12/2014 IntelliJ IDEA.
 */

public class Messaggio {

    private Integer id;
    private GregorianCalendar rise;
    private int ttl;
    private GregorianCalendar death;
    private String text;
    private Utente owner;
    private Utente dest;

    public Messaggio(int ttl, String text, Utente owner, Utente dest) {
        this.ttl = ttl;
        this.text = text;
        this.owner = owner;
        this.dest = dest;
        id = Server.getNewID();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Utente getOwner() {
        return owner;
    }

    public void setOwner(Utente owner) {
        this.owner = owner;
    }

    public Utente getDest() {
        return dest;
    }

    public void setDest(Utente dest) {
        this.dest = dest;
    }

    public void setRise(GregorianCalendar rise) {
        this.rise = rise;
    }

    public void setDeath(GregorianCalendar death) {
        this.death = death;
    }

    public GregorianCalendar getDeath() {
        return death;
    }

    public Integer getId() {
        return id;
    }

    public int getTtl() {
        return ttl;
    }

    public String toString() {
        Date now = new Date();
        long durata = (getDeath().getTime().getTime()-now.getTime())/1000;
        return "id_mes:" + this.getId() + "&from:" + this.getOwner().getIdUtente() + "&testo:" + this.getText() + "&ttl:" + durata + "\r\n";

    }
}

