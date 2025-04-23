package sd.lab1;

import java.net.* ;
import java.util.logging.Logger;

/**
 * Basic TCP server... 
 *
 */
public class  TcpServer {
	private static Logger Log = Logger.getLogger(TcpServer.class.getName());

    private static final int PORT = 9000;
	private static final int BUF_SIZE = 1024;

	public static void main(String[] args) throws Exception {
        
		//TODO: Use Discovery to announce the uri of this server, in the form of (tcp://hostname:port)

        
		//Create a server socket and wait for incoming TCP connections from client
		try(ServerSocket ssocket = new ServerSocket( PORT )) {
			Log.info("My IP address is: " + InetAddress.getLocalHost().getHostAddress());
			Log.info("Accepting connections at: " + ssocket.getLocalSocketAddress() ) ;
            while( true ) {
                Socket csocket = ssocket.accept() ;
                
                System.err.println("Accepted connection from client at: " + csocket.getRemoteSocketAddress() ) ;
                
                int n;
                byte[] buf = new byte[ BUF_SIZE];
                
                //Until the connection is closed receive lines of text from the client and print them out
                while( (n = csocket.getInputStream().read(buf)) > 0)
                	System.out.write( buf, 0, n);
                
                Log.info("Connection closed.") ;
            }        	
        }
    }
    
}
