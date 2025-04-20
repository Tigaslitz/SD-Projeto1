package fctreddit.Services.ContentService.java;

import fctreddit.Clients.rest.RestUsersClient;
import fctreddit.Discovery.Discovery;
import fctreddit.Hibernate;
import fctreddit.Services.ImageService.java.ImageClass;
import fctreddit.api.Image;
import fctreddit.api.Interfaces.Content;
import fctreddit.api.Interfaces.Result;
import fctreddit.api.Interfaces.Users;
import fctreddit.api.Post;
import fctreddit.api.User;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

public class ContentClass implements Content {

    private static Logger Log = Logger.getLogger(ImageClass.class.getName());
    private final Hibernate hibernate = Hibernate.getInstance();

    Discovery discovery;
    Users usersServer;

    public ContentClass() throws IOException {
        discovery = new Discovery(Discovery.DISCOVERY_ADDR);
        discovery.start();
        findServer();

    }

    //TODO: Implementação podre, rever
    private void findServer(){
        URI userServiceURI = discovery.knownUrisOf("users");
        while (userServiceURI == null) {
            userServiceURI = discovery.knownUrisOf("users");
        }
        usersServer = new RestUsersClient(userServiceURI);
    }

    @Override
    public Result<String> createPost(Post post, String userPassword) {
        Log.info("createPost");

        if (userPassword == null || post == null) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        Result<User> userResult = usersServer.getUser(post.getAuthorId(), userPassword);
        if (!userResult.isOK()) {
            return Result.error(userResult.error());
        }

        String parentPostURI = post.getParentUrl();
        if (parentPostURI != null) {
            Result<Post> parentPost = getPost(parentPostURI);
            if (!parentPost.isOK()) {
                return Result.error(parentPost.error());
            }
        }
        try {
            hibernate.persist(post);
            return Result.ok(post.getPostId());
        } catch (Exception e) {
            Log.info("Failed to create Post: " + e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<List<String>> getPosts(long timestamp, String sortOrder) {

        return null;
    }

    @Override
    public Result<Post> getPost(String postId) {
        Log.info("getPost : " + postId);
        try {
            Post post = hibernate.get(Post.class, postId);

            if (post == null)
                return Result.error(Result.ErrorCode.NOT_FOUND);
            return Result.ok(post);
        } catch (Exception e){
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<List<String>> getPostAnswers(String postId, long maxTimeout) {
        return null;
    }

    @Override
    public Result<Post> updatePost(String postId, String userPassword, Post post) {
        return null;
    }

    @Override
    public Result<Void> deletePost(String postId, String userPassword) {
        return null;
    }

    @Override
    public Result<Void> upVotePost(String postId, String userId, String userPassword) {
        return null;
    }

    @Override
    public Result<Void> removeUpVotePost(String postId, String userId, String userPassword) {
        return null;
    }

    @Override
    public Result<Void> downVotePost(String postId, String userId, String userPassword) {
        return null;
    }

    @Override
    public Result<Void> removeDownVotePost(String postId, String userId, String userPassword) {
        return null;
    }

    @Override
    public Result<Integer> getupVotes(String postId) {
        return null;
    }

    @Override
    public Result<Integer> getDownVotes(String postId) {
        return null;
    }
}
