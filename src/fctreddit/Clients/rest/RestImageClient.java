package fctreddit.Clients.rest;

import fctreddit.Clients.java.ImageClient;
import fctreddit.api.Interfaces.Result;
import fctreddit.api.Rest.RestUsers;
import fctreddit.api.User;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

public class RestImageClient extends ImageClient {
    private static Logger Log = Logger.getLogger(RestUsersClient.class.getName());

    final URI serverURI;
    final Client client;
    final ClientConfig config;

    final WebTarget target;

    public RestImageClient(URI serverURI) {
        this.serverURI = serverURI;
        this.config = new ClientConfig();
        config.property( ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
        config.property( ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);

        this.client = ClientBuilder.newClient(config);
        target = client.target( serverURI ).path( RestUsers.PATH );
    }

    //TODO: IMPLEMENTAR
    @Override
    public Result<String> createImage(String userId, byte[] imageContents, String password) {
        try {

            Response r = target.request()
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(imageContents, MediaType.APPLICATION_JSON));

            int status = r.getStatus();
            if (status != Response.Status.OK.getStatusCode())
                return Result.error(getErrorCodeFrom(status));
            else
                return Result.ok(r.readEntity(String.class));

        } catch (ProcessingException x) {
            Log.info(x.getMessage());
        } catch( Exception x ) {
            x.printStackTrace();
        }
        return Result.error(  Result.ErrorCode.TIMEOUT );
    }

    @Override
    public Result<byte[]> getImage(String userId, String imageId) {
        try {
            Response r = target.path( imageId )
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get();

            int status = r.getStatus();
            if (status != Response.Status.OK.getStatusCode())
                return Result.error(getErrorCodeFrom(status));
            else
                return Result.ok(r.readEntity(new GenericType<List<User>>() {}));

        } catch (ProcessingException x) {
            Log.info(x.getMessage());
        } catch( Exception x ) {
            x.printStackTrace();
        }
        return Result.error(  Result.ErrorCode.TIMEOUT );
    }

    @Override
    public Result<Void> deleteImage(String userId, String imageId, String password) {
        try {
            Response r = target.queryParam(RestUsers.QUERY, pattern).
                    request().accept(MediaType.APPLICATION_JSON).get();

            int status = r.getStatus();
            if (status != Response.Status.OK.getStatusCode())
                return Result.error(getErrorCodeFrom(status));
            else
                return Result.ok(r.readEntity(new GenericType<List<User>>() {}));

        } catch (ProcessingException x) {
            Log.info(x.getMessage());
        } catch( Exception x ) {
            x.printStackTrace();
        }
        return Result.error(  Result.ErrorCode.TIMEOUT );
    }

    public static Result.ErrorCode getErrorCodeFrom(int status) {
        return switch (status) {
            case 200, 209 -> Result.ErrorCode.OK;
            case 409 -> Result.ErrorCode.CONFLICT;
            case 403 -> Result.ErrorCode.FORBIDDEN;
            case 404 -> Result.ErrorCode.NOT_FOUND;
            case 400 -> Result.ErrorCode.BAD_REQUEST;
            case 500 -> Result.ErrorCode.INTERNAL_ERROR;
            case 501 -> Result.ErrorCode.NOT_IMPLEMENTED;
            default -> Result.ErrorCode.INTERNAL_ERROR;
        };
    }
}
