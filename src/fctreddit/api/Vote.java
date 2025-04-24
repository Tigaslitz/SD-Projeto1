package fctreddit.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Represents a User in the system
 */
@Entity
public class Vote {

    @Id
    private String voteId;
    private String postId;
    private String userId;
    private boolean voted;

    public Vote(){
    }

    public Vote(String userId, String voteId, String postId, boolean voted) {
        this.userId = userId;
        this.voteId = voteId;
        this.postId = postId;
        this.voted = voted;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVoteId() {
        return voteId;
    }

    public void setVotedId(String voteId) {
        this.voteId = voteId;
    }

    public String getPostId() { return postId; }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }

    public boolean getVoted() {
        return this.voted;
    }

}