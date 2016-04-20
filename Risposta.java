import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Matteo Marras 827872 on 31/12/2014 IntelliJ IDEA.
 */

public class Risposta {

	private String protocol;
	private String codice;
	private String message;
	private String response;

	public Risposta(String protocol, String codice, String message,
                    String response){
		this.protocol = protocol;
		this.codice = codice;
		this.message = message;
		this.response = response;
	}

	public void send(OutputStream os){
		try{
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
			bw.write(protocol+" "+codice+" "+message+"\r\n");
            String connection = (protocol.equals("HTTP/1.1")) ? "connection: keep-alive\r\n" : "connection: close\r\n";
			bw.write(connection);
			bw.write("content-type: text/plain\r\n");
			bw.write("server: MarrasServer\r\n");
			bw.newLine();
			bw.flush();
			bw.write(response);
			bw.flush();
            //System.out.println("\n" + response);
		}catch(IOException ioe){
			System.out.println("Errore nella scrittura");
		}
	}
}
