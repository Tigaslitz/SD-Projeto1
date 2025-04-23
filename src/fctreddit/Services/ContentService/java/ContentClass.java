package fctreddit.Services.ContentService.java;

import fctreddit.Discovery.Discovery;
import fctreddit.Hibernate;
import fctreddit.api.Interfaces.Content;
import fctreddit.api.Interfaces.Result;
import fctreddit.api.Interfaces.Users;
import fctreddit.api.Post;
import fctreddit.api.User;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class ContentClass implements Content {

    private static Logger Log = Logger.getLogger(ContentClass.class.getName());
    private final Hibernate hibernate = Hibernate.getInstance();

    Discovery discovery;
    Users usersServer;

    public ContentClass() throws IOException {
        discovery = new Discovery(Discovery.DISCOVERY_ADDR);
        discovery.start();
        usersServer = discovery.findServer("Users");
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
        Log.info("parentPostURI: " + parentPostURI);
        if (parentPostURI != null) {
            Result<Post> parentPost = getPost(parentPostURI);
            if (!parentPost.isOK()) {
                return Result.error(parentPost.error());
            }
        }

        try {
            hibernate.persist(post);
            return Result.ok(post.getMediaUrl());
        } catch (Exception e) {
            Log.info("Failed to create Post: " + e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<List<String>> getPosts(long timestamp, String sortOrder) {
        Log.info("getPosts : timestamp = " + timestamp + ", sortOrder = " + sortOrder);

        try {
            StringBuilder jpql = new StringBuilder("SELECT p.postId FROM Post p WHERE p.parentId IS NULL");

            if (timestamp > 0) {
                jpql.append(" AND p.creationTime >= ").append(timestamp);
            }

            if (sortOrder != null) {
                switch (sortOrder) {
                    case MOST_UP_VOTES -> jpql.append(" ORDER BY p.upVotes DESC, p.postId ASC");
                    case MOST_REPLIES -> jpql.append(" ORDER BY SIZE(p.replies) DESC, p.postId ASC");
                    default -> {
                        Log.warning("Unknown sort order: " + sortOrder);
                        return Result.error(Result.ErrorCode.BAD_REQUEST);
                    }
                }
            } else {
                jpql.append(" ORDER BY p.creationTime ASC");
            }
            Log.info("JPQL: " + jpql);

            List<String> postIds = hibernate.jpql(jpql.toString(), String.class);
            return Result.ok(postIds);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Result.ErrorCode.INTERNAL_ERROR);
        }
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
        Log.info("getPostAnswers : postId = " + postId + ", timeout = " + maxTimeout);

        try {

            Result<Post> post = getPost(postId);
            if (!post.isOK())
                return Result.error(post.error());

            List<String> original = hibernate.jpql(
                    "SELECT p.postId FROM Post p WHERE p.parentId = '" + postId + "' ORDER BY p.creationTime ASC",
                    String.class);

            // Se o timeout for maior que 0, espera por nova resposta (se surgir)
            if (maxTimeout > 0) {
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < maxTimeout) {

                    List<String> current = hibernate.jpql(
                            "SELECT p.postId FROM Post p WHERE p.parentId = '" + postId + "' ORDER BY p.creationTime ASC",
                            String.class);

                    if (current.size() != original.size()) {
                        return Result.ok(current);
                    }

                    Thread.sleep(100); // espera 100ms antes de verificar novamente
                }
            }

            return Result.ok(original);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Result.ErrorCode.INTERNAL_ERROR);
        }
    }

    @Override
    public Result<Post> updatePost(String postId, String userPassword, Post post) {
        Log.info("updatePost: post = " + postId + "; pwd = " + userPassword);

        if (postId == null || post == null) {
            Log.info("PostID or post is null.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        Result<Post> res = getPost(postId);
        if (!res.isOK())
            return Result.error(res.error());

        Post original = res.value();
        Result<User> user = usersServer.getUser(post.getAuthorId(), userPassword);
        if (!user.isOK())
            return Result.error(user.error());

        try{
            if (post.getMediaUrl() != null)
                original.setMediaUrl(post.getMediaUrl());

            if (post.getContent() != null)
                original.setContent(post.getContent());

            hibernate.update(original);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return Result.ok(post);
    }

    @Override
    public Result<Void> deletePost(String postId, String userPassword) {

        Result<Post> res = getPost(postId);
        if (!res.isOK())
            return Result.error(res.error());

        Post post = res.value();

        Result<User> resUser = usersServer.getUser(post.getAuthorId(), userPassword);
        if (!resUser.isOK())
            return Result.error(resUser.error());

        try{
            deletePostRec(postId);
        }catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return Result.ok();
    }

    private void deletePostRec(String postId){
        List<String> postReplies = getPostAnswers(postId, 0).value();
        for (String current : postReplies){
            deletePostRec(current);
        }
        hibernate.delete(getPost(postId).value());
    }

    @Override
    public Result<Void> upVotePost(String postId, String userId, String userPassword) {

        Result<Post> res = getPost(postId);
        if (!res.isOK())
            return Result.error(res.error());

        Result<User> resUser = usersServer.getUser(userId, userPassword);
        if (!resUser.isOK())
            return Result.error(resUser.error());

        Post post = res.value();

        if (post.getUpVotedUsers().contains(userId) || post.getDownVotedUsers().contains(userId))
            return Result.error(Result.ErrorCode.CONFLICT);

        try {

            post.getUpVotedUsers().add(userId);
            post.setUpVote(post.getUpVote() + 1);

            hibernate.update(post);
            return Result.ok();

        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public Result<Void> removeUpVotePost(String postId, String userId, String userPassword) {
        Log.info("removeUpVotePost: postId = " + postId + ", userId = " + userId);
        Result<Post> res = getPost(postId);
        if (!res.isOK())
            return Result.error(res.error());

        Result<User> resUser = usersServer.getUser(userId, userPassword);
        if (!resUser.isOK())
            return Result.error(resUser.error());

        Post post = res.value();

        try {

            if(!post.getUpVotedUsers().remove(userId))
                return Result.error(Result.ErrorCode.CONFLICT);
            post.setUpVote(post.getUpVote() - 1);

            hibernate.update(post);
            return Result.ok();

        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<Void> downVotePost(String postId, String userId, String userPassword) {
        Result<Post> res = getPost(postId);
        if (!res.isOK())
            return Result.error(res.error());

        Result<User> resUser = usersServer.getUser(userId, userPassword);
        if (!resUser.isOK())
            return Result.error(resUser.error());

        Post post = res.value();

        if (post.getUpVotedUsers().contains(userId) || post.getDownVotedUsers().contains(userId))
            return Result.error(Result.ErrorCode.CONFLICT);

        try {

            post.getDownVotedUsers().add(userId);
            post.setDownVote(post.getDownVote() + 1);

            hibernate.update(post);
            return Result.ok();

        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<Void> removeDownVotePost(String postId, String userId, String userPassword) {
        Log.info("removeDownVotePost: postId = " + postId + ", userId = " + userId);
        Result<Post> res = getPost(postId);
        if (!res.isOK())
            return Result.error(res.error());

        Result<User> resUser = usersServer.getUser(userId, userPassword);
        if (!resUser.isOK())
            return Result.error(resUser.error());

        Post post = res.value();

        try {

            if(!post.getDownVotedUsers().remove(userId))
                return Result.error(Result.ErrorCode.CONFLICT);
            post.setDownVote(post.getDownVote() - 1);

            hibernate.update(post);
            return Result.ok();

        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<Integer> getupVotes(String postId) {
        Log.info("getUpVotes: postId = " + postId);
        Result<Post> res = getPost(postId);
        if (!res.isOK())
            return Result.error(res.error());

        Post post = res.value();

        return Result.ok(post.getUpVote());
    }

    @Override
    public Result<Integer> getDownVotes(String postId) {
        Log.info("getDownVotes: postId = " + postId);
        Result<Post> res = getPost(postId);
        if (!res.isOK())
            return Result.error(res.error());

        Post post = res.value();

        return Result.ok(post.getDownVote());
    }
}
