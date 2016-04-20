import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Matteo Marras 827872 on 31/12/2014 IntelliJ IDEA.
 */

public class Server{

    /*
     * Utilizzo dei campi statici al server in modo da essere accessibili all'esterno tramite appositi metodi
     * dai relativi thread.
     */

    private static int newID = 0;

    private static Map<Integer,Messaggio> messagges = Collections.synchronizedMap(new HashMap<Integer,Messaggio>());

    private static Set<Utente> users = Collections.synchronizedSet(new HashSet<Utente>());

    public static Set<Utente> getUsers() {
        return users;
    }

    public static Utente getUser(String idUtente){
        for(Utente u : users){
            if(u.getIdUtente().equals(idUtente))
                return u;
        }
        return null;
    }

    public static Map<Integer, Messaggio> getMessagges() {
        return messagges;
    }

    public static synchronized int getNewID() {
        return ++newID;
    }

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(51); //Metto un limite di 50 connessioni contemporanee al server
        try(ServerSocket server = new ServerSocket(8000)){
            MessageHandler mhl = new MessageHandler(); //viene lanciato il thread che parallelamente all'esecuzione del server gestisce il database dei messaggi
            pool.submit(mhl);
            while(true){
                try{
                    Socket s = server.accept();
                    ProcessConnectionInstance pci = new ProcessConnectionInstance();
                    pci.setClient(s);
                    pool.submit(pci);

                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }

        } catch (IOException e) {
            System.err.println("IO Error");
        }
    }

}
