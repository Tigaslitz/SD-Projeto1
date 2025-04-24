package fctreddit.Clients.rest;

import fctreddit.Clients.java.ContentClient;
import fctreddit.api.Interfaces.Result;
import fctreddit.api.Interfaces.Result.ErrorCode;
import fctreddit.api.Post;
import fctreddit.api.Rest.RestContent;
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

public class RestContentClient extends ContentClient {

    private static Logger Log = Logger.getLogger(RestContentClient.class.getName());

    final URI serverURI;
    final Client client;
    final ClientConfig config;

    final WebTarget target;

    public RestContentClient(URI serverURI) {
        this.serverURI = serverURI;
        this.config = new ClientConfig();
        config.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
        config.property(ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);

        this.client = ClientBuilder.newClient(config);
        target = client.target(serverURI).path(RestContent.PATH);
    }


    //TODO:Implementar
    @Override
    public Result<String> createPost(Post post, String userPassword) {
        try {
            Response r = target.queryParam(RestContent.PASSWORD, userPassword)
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(post, MediaType.APPLICATION_JSON));

            int status = r.getStatus();
            if (status != Response.Status.OK.getStatusCode())
                return Result.error(getErrorCodeFrom(status));
            else
                return Result.ok(r.readEntity(String.class));

        } catch (ProcessingException x) {
            Log.info(x.getMessage());
        } catch (Exception x) {
            x.printStackTrace();
        }
        return Result.error(ErrorCode.INTERNAL_ERROR);
    }

    @Override
    public Result<List<String>> getPosts(long timestamp, String sortOrder) {
        try {
            Response r = target
                    .queryParam(RestContent.TIMESTAMP, timestamp)
                    .queryParam(RestContent.SORTBY, sortOrder)
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get();

            int status = r.getStatus();
            if (status != Response.Status.OK.getStatusCode())
                return Result.error(getErrorCodeFrom(status));
            else
                return Result.ok(r.readEntity(new GenericType<List<String>>() {
                }));

        } catch (ProcessingException x) {
            Log.info(x.getMessage());
        } catch (Exception x) {
            x.printStackTrace();
        }
        return Result.error(ErrorCode.INTERNAL_ERROR);
    }

    @Override
    public Result<Post> getPost(String postId) {
        try {
            Response r = target
                    .path(postId)
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get();

            int status = r.getStatus();
            Log.info("Status: " + status);
            if (status != Response.Status.OK.getStatusCode())
                return Result.error(getErrorCodeFrom(status));
            else
                return Result.ok(r.readEntity(Post.class));
        } catch (ProcessingException x) {
            Log.info(x.getMessage());
        } catch (Exception x) {
            x.printStackTrace();
        }
        return Result.error(ErrorCode.INTERNAL_ERROR);
    }

    @Override
    public Result<List<String>> getPostAnswers(String postId, long timeout) {
        try {
            Response r = target
                    .path(postId + "/" + RestContent.REPLIES)
                    .queryParam(RestContent.TIMEOUT, timeout)
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get();
            int status = r.getStatus();
            if (status != Response.Status.OK.getStatusCode())
                return Result.error(getErrorCodeFrom(status));
            else
                return Result.ok(r.readEntity(new GenericType<List<String>>() {
                }));
        } catch (Exception x) {
            x.printStackTrace();
        }
        return Result.error(ErrorCode.INTERNAL_ERROR);
    }

    @Override
    public Result<Post> updatePost(String postId, String userPassword, Post post) {
        try {
            Response r = target
                    .path(postId)
                    .queryParam(RestContent.PASSWORD, userPassword)
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(post, MediaType.APPLICATION_JSON));

            int status = r.getStatus();
            if (status != Response.Status.OK.getStatusCode())
                return Result.error(getErrorCodeFrom(status));
            else
                return Result.ok(r.readEntity(Post.class));
        } catch (Exception x) {
            x.printStackTrace();
        }
        return Result.error(ErrorCode.INTERNAL_ERROR);
    }

    @Override
    public Result<Void> deletePost(String postId, String userPassword) {
        try {
            Response r = target
                    .path(postId)
                    .queryParam(RestContent.PASSWORD, userPassword)
                    .request()
                    .delete();

        int status = r.getStatus();
        if (status != Response.Status.OK.getStatusCode())
            return Result.error(getErrorCodeFrom(status));
        else
            return Result.ok();
        }catch(Exception x ){
            x.printStackTrace();
        }
        return Result.error(ErrorCode.INTERNAL_ERROR);
    }

    @Override
    public Result<Void> upVotePost(String postId, String userId, String userPassword) {
        try {
            Response r = target
                .path(postId + "/" + RestContent.UPVOTE + "/" + userId)
                .queryParam(RestContent.PASSWORD, userPassword)
                .request()
                .post(null);

        int status = r.getStatus();
        if( status != Response.Status.OK.getStatusCode() )
            return Result.error( getErrorCodeFrom(status));
        else
            return Result.ok();
        }catch(Exception x ){
            x.printStackTrace();
        }
        return Result.error(ErrorCode.INTERNAL_ERROR);
    }

    @Override
    public Result<Void> removeUpVotePost(String postId, String userId, String userPassword) {
        try {
            Response r =target
            .path(postId + "/" + RestContent.UPVOTE + "/" + userId)
            .queryParam(RestContent.PASSWORD, userPassword)
            .request()
            .delete();

            int status = r.getStatus();
            if( status != Response.Status.OK.getStatusCode() )
                return Result.error( getErrorCodeFrom(status));
            else
                return Result.ok();
        }catch(Exception x ){
            x.printStackTrace();
        }
        return Result.error(ErrorCode.INTERNAL_ERROR);
    }

    @Override
    public Result<Void> downVotePost(String postId, String userId, String userPassword) {
        try {
            Response r =target
                .path(postId + "/" + RestContent.DOWNVOTE + "/" + userId)
                .queryParam(RestContent.PASSWORD, userPassword)
                .request()
                .post(null);

            int status = r.getStatus();
            if( status != Response.Status.OK.getStatusCode() )
                return Result.error( getErrorCodeFrom(status));
            else
                return Result.ok();
        }catch(Exception x ){
            x.printStackTrace();
        }
        return Result.error(ErrorCode.INTERNAL_ERROR);
    }

    @Override
    public Result<Void> removeDownVotePost(String postId, String userId, String userPassword) {
        try {
                Response r =target
                    .path(postId + "/" + RestContent.DOWNVOTE + "/" + userId)
                    .queryParam(RestContent.PASSWORD, userPassword)
                    .request()
                    .delete();

            int status = r.getStatus();
            if( status != Response.Status.OK.getStatusCode() )
                return Result.error( getErrorCodeFrom(status));
            else
                return Result.ok();
        }catch(Exception x ){
            x.printStackTrace();
        }
        return Result.error(ErrorCode.INTERNAL_ERROR);

    }

    @Override
    public Result<Integer> getupVotes(String postId) {
        try {
            Response r = target
                .path(postId + "/" + RestContent.UPVOTE)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

            int status = r.getStatus();
            if( status != Response.Status.OK.getStatusCode() )
                return Result.error( getErrorCodeFrom(status));
            else
                return Result.ok( r.readEntity( Integer.class ));
        }catch(Exception x ){
            x.printStackTrace();
        }
        return Result.error(ErrorCode.INTERNAL_ERROR);
    }

    @Override
    public Result<Integer> getDownVotes(String postId) {
        try {
            Response r = target
                .path(postId + "/" + RestContent.DOWNVOTE)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
            int status = r.getStatus();
            if( status != Response.Status.OK.getStatusCode() )
                return Result.error( getErrorCodeFrom(status));
            else
                return Result.ok( r.readEntity( Integer.class ));
        }catch(Exception x ){
            x.printStackTrace();
        }
        return Result.error(ErrorCode.INTERNAL_ERROR);
    }

    public static ErrorCode getErrorCodeFrom(int status) {
        return switch (status) {
            case 200, 209 -> ErrorCode.OK;
            case 409 -> ErrorCode.CONFLICT;
            case 403 -> ErrorCode.FORBIDDEN;
            case 404 -> ErrorCode.NOT_FOUND;
            case 400 -> ErrorCode.BAD_REQUEST;
            case 500 -> ErrorCode.INTERNAL_ERROR;
            case 501 -> ErrorCode.NOT_IMPLEMENTED;
            default -> ErrorCode.INTERNAL_ERROR;
        };
    }
}
