import java.util.*;

/**
 * Created by Matteo Marras 827872 on 31/12/2014 IntelliJ IDEA.
 */

public class Utente {

    private String idUtente;
    private Map<Integer,Messaggio> inBox;
    private Set<Utente> friends;

    public Utente(String idUtente) {
        this.idUtente = idUtente;
        inBox = Collections.synchronizedMap(new HashMap<Integer,Messaggio>());
        friends = Collections.synchronizedSet(new HashSet<Utente>());
    }

    public void addInBox(Messaggio m){
        inBox.put(m.getId(),m);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Utente){
            return this.getIdUtente().equals(((Utente)o).getIdUtente());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if(idUtente != null)
            return idUtente.hashCode();
        return 0;
    }

    public String getIdUtente() {
        return this.idUtente;
    }

    public Map<Integer, Messaggio> getInBox() {
        return inBox;
    }

    public Set<Utente> getFriends() {
        return friends;
    }

    public boolean addFriend(Utente u){
        return friends.add(u);
    }

    public boolean areFriend(Utente u){
        return friends.contains(u);
    }

    public String printFriends(){
        String s = "";
        for(Utente u : friends)
           s += u.toString() + "\r\n";
        return s;
    }

    public String printMessaggi(){
        Collection<Messaggio> tmp = inBox.values();
        String s = "";
        for(Messaggio m : tmp){
            s += m.toString();
        }
        return s;
    }

    public String toString(){
        return "\"idUtente\": " + this.getIdUtente();
    }
}
