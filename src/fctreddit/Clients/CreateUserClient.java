package lab4.clients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import lab4.api.User;
import lab4.api.java.Result;
import lab4.clients.java.UsersClient;
import lab4.clients.rest.RestUsersClient;
import lab4.clients.grpc.GrpcUsersClient;

public class CreateUserClient {

	private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());
	
	public static void main(String[] args) throws IOException {
		
		if( args.length != 5) {
			System.err.println( "Use: java " + CreateUserClient.class.getCanonicalName() + " url userId fullName email password");
			return;
		}
		
		String serverUrl = args[0];
		String userId = args[1];
		String fullName = args[2];
		String email = args[3];
		String password = args[4];
		
		User usr = new User( userId, fullName, email, password);
		
		UsersClient client = null;
		
		if(serverUrl.endsWith("rest"))
			client = new RestUsersClient( URI.create( serverUrl ) );
		else
			client = new GrpcUsersClient( URI.create( serverUrl) );
		
		Result<String> result = client.createUser( usr );
		if( result.isOK()  )
			Log.info("Created user:" + result.value() );
		else
			Log.info("Create user failed with error: " + result.error());

	}
	
}
