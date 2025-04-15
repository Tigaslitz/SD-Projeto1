package sd.lab1;

import java.net.* ;
import java.util.*;

/**
 * Basic TCP client... 
 *
 */
public class TcpClient {
    
	private static final String QUIT = "!quit";

	public static void main(String[] args) throws Exception {
        
    	//TODO: Change to use the Discovery to obtain the hostname and port of the server (format tpc://hostname:port;
		Scanner scan = new Scanner(System.in);
		String serverAddr = scan.nextLine();
		
		//Process the string with the server address to extract IP address and port
		
		String[] elements = serverAddr.split(":");
		
    	int port = Integer.parseInt(elements[2]);
    	String hostname = elements[1].replaceAll("//", ""); 
    	
    	//Establish a TCP connection to the server and send lines of text from standard input until input is !quit
    	try( Socket sock = new Socket( hostname, port)) {
    		String input;
    		do {
    			input = scan.nextLine();
    			sock.getOutputStream().write( (input + System.lineSeparator()).getBytes() );
    		} while( ! input.equals(QUIT));
    		
    	}
    	
    	scan.close();
		
    }  
}
