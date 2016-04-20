import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

/**
 * Created by Matteo Marras 827872 on 31/12/2014 IntelliJ IDEA.
 */
public class ProcessConnectionInstance implements Runnable {

    private Socket client; //Gestisce un singolo client per volta

    public void setClient(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        InputStream is;
        OutputStream os;
        boolean keepAlive = false;

        try {
            do{
                is = client.getInputStream();
                System.out.println("Connessione stabilita con il client " + client.toString());
                RichiestaHTTP richiestaClient = new RichiestaHTTP(is);
                //Gestione della richiesta inviata dal client
                if (richiestaClient.isValid() && richiestaClient.getAccessMethod().equals("GET")){ //richiesta valida e di tipo GET
                    if((keepAlive = (richiestaClient.getProtocol().equals("HTTP/1.1")) ? true : false))
                        client.setKeepAlive(true);
                    else
                        client.setKeepAlive(false);
                    try {
                        URL u = new URL("file://" + richiestaClient.getUrl());   //Check automatico della sintassi
                        String path = u.getPath();      //Estraggo  nome della risorsa chiamata senza parte relativa alla query
                        String corpoRisposta = "";
                        GestoreRichiesta rh = null;

					    /*
					    * Lo swtich gestisce le risorsa messe a disposizione del servizio. Se voglio inserire una nuova risorsa devo introdurre un nuovo case. Ogni risorsa e' gestita da un oggetto
					    * che implementa la gestione di quella risorsa, incluso il controllo dei parametri passati come query
					    */

                        switch(path){
                            case "/aggiungiUtente":
                                rh = new AggiungiUtenteHandler(u.getQuery());
                                rh.gestisci();
                                break;
                            case "/aggiungiAmico":
                                rh = new AggiungiAmicoHandler(u.getQuery());
                                rh.gestisci();
                                break;
                            case "/vediAmici":
                                rh = new VediAmiciHandler(u.getQuery());
                                rh.gestisci();
                                break;
                            case "/invia":
                                rh = new InviaMessaggioHandler(u.getQuery());
                                rh.gestisci();
                                break;
                            case "/vediMessaggi":
                                rh = new VediMessaggiHandler(u.getQuery());
                                rh.gestisci();
                                break;
                            case "/rispondi":
                                rh = new RispondiMessaggioHandler(u.getQuery());
                                rh.gestisci();
                                break;
                        }
                        Risposta risposta = null;
                        if (rh != null) {           //la risorsa richiesta esiste
                            if (rh.isCorrect()) { //la gestione della risorsa non ha causato errori
                                corpoRisposta = (keepAlive)? rh.getResponse() + "\r\n\r\n" : rh.getResponse();
                                risposta = new Risposta(richiestaClient.getProtocol(), "200", "OK", corpoRisposta);
                            } else {
                                corpoRisposta = (keepAlive)? rh.getResponse() + "\r\n\r\n" : rh.getResponse();
                                risposta = new Risposta(richiestaClient.getProtocol(), "400", "ERRORE", corpoRisposta);
                            }
                        } else {
                            risposta = new Risposta(richiestaClient.getProtocol(), "400", "ERRORE", "Risorsa " + path + " non supportata\r\n\r\n");
                        }
                        os = client.getOutputStream();
                        risposta.send(os);
                        os.flush();

                        if(!keepAlive){    //controllo se devo chiudere la connessione oppure no a seconda del protocollo scelto dal client (HTTP/1.1 o HTTP/1.0)
                            os.close();
                            client.close();
                        }

                    } catch (MalformedURLException mue) { //Se l'url non e' sintatticamente corretto lo comunico con un messaggio di errore al client
                        os = client.getOutputStream();
                        Risposta rispostaErrore = new Risposta(richiestaClient.getProtocol(), "400", "Errore", "Errore nella sintassi della richiesta\r\n");
                        rispostaErrore.send(os);
                        os.close();
                    }
                }
                else{
                    System.out.println(richiestaClient.getErrorMessage());   //Nel caso in cui vi siano errori di formato HTTP verrà stampato nel display del server un messaggio di errore
                    keepAlive = false;                                       //relativo e chiusa la connessione con il client
                    client.close();
                }
            }while(keepAlive);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
