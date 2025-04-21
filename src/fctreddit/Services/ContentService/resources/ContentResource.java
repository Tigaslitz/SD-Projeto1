package fctreddit.Services.ContentService.resources;

import fctreddit.Services.ContentService.java.ContentClass;
import fctreddit.Services.UserService.java.UsersClass;
import fctreddit.Services.UserService.resources.UsersResource;
import fctreddit.api.Interfaces.Content;
import fctreddit.api.Interfaces.Result;
import fctreddit.api.Interfaces.Users;
import fctreddit.api.Post;
import fctreddit.api.Rest.RestContent;
import fctreddit.api.User;
import jakarta.ws.rs.WebApplicationException;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class ContentResource implements RestContent {

    private static Logger Log = Logger.getLogger(ContentResource.class.getName());
    private final Content impl = new ContentClass();


    @Override
    public String createPost(Post post, String userPassword) {
        Result<String> res = impl.createPost(post, userPassword);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
        return res.value();
    }

    @Override
    public List<String> getPosts(long timestamp, String sortOrder) {
        Result<List<String>> res = impl.getPosts(timestamp, sortOrder);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
        return res.value();
    }

    @Override
    public Post getPost(String postId) {
        Result<Post> res = impl.getPost(postId);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
        return res.value();
    }

    @Override
    public List<String> getPostAnswers(String postId, long timeout) {
        Result<List<String>> res = impl.getPostAnswers(postId, timeout);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
        return res.value();
    }

    @Override
    public Post updatePost(String postId, String userPassword, Post post) {
        Result<Post> res = impl.updatePost(postId, userPassword, post);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
        return res.value();
    }

    @Override
    public void deletePost(String postId, String userPassword) {
        Result<Void> res = impl.deletePost(postId, userPassword);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
    }

    @Override
    public void upVotePost(String postId, String userId, String userPassword) {
        Result<Void> res = impl.upVotePost(postId, userId, userPassword);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
    }

    @Override
    public void removeUpVotePost(String postId, String userId, String userPassword) {
        Result<Void> res = impl.removeUpVotePost(postId, userId, userPassword);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
    }

    @Override
    public void downVotePost(String postId, String userId, String userPassword) {
        Result<Void> res = impl.downVotePost(postId, userId, userPassword);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
    }

    @Override
    public void removeDownVotePost(String postId, String userId, String userPassword) {
        Result<Void> res = impl.removeDownVotePost(postId, userId, userPassword);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
    }

    @Override
    public Integer getupVotes(String postId) {
        Result<Integer> res = impl.getupVotes(postId);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
        return res.value();
    }

    @Override
    public Integer getDownVotes(String postId) {
        Result<Integer> res = impl.getDownVotes(postId);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
        return res.value();
    }

    private static Throwable errorCodeToStatus( Result.ErrorCode error ) {
        var status =  switch( error) {
            case NOT_FOUND -> io.grpc.Status.NOT_FOUND;
            case CONFLICT -> io.grpc.Status.ALREADY_EXISTS;
            case FORBIDDEN -> io.grpc.Status.PERMISSION_DENIED;
            case NOT_IMPLEMENTED -> io.grpc.Status.UNIMPLEMENTED;
            case BAD_REQUEST -> io.grpc.Status.INVALID_ARGUMENT;
            default -> io.grpc.Status.INTERNAL;
        };

        return status.asException();
    }
}
