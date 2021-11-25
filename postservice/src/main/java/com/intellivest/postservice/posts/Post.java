package com.intellivest.postservice.posts;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * post entity object
 */
@Entity
@Table(name = "posts")
public class Post {

    /** POST ID */
    @Id
    @Column(name = "id", nullable = false, unique = true)
    public String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /** Poster ID */
    @Column(name = "poster_id", nullable = false)
    public String posterId;

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    /** POST TITLE */
    @Column(name = "post_title", nullable = false)
    public String postTitle;

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    /** POST CONTENT */
    @Column(name = "post_content", nullable = false)
    public String postContent;

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    /** TIME OF POST */
    @Column(name = "time_of_post", nullable = false)
    public Timestamp timeOfPost;

    public Timestamp getTimeOfPost() {
        return timeOfPost;
    }

    public void setTimeOfPost(Timestamp timeOfPost) {
        this.timeOfPost = timeOfPost;
    }

    public String toString() {
        return "Id: " + getId() + ", poster_id: " + getPosterId();

    }

    public static Post createNewPost(PostCreationRequest newPostRequest, String posterId) {
        // create a unique id for the post 
        UUID uuid = UUID.randomUUID();
        // get and format a timestamp for post */
        Timestamp timestamp = Timestamp.from(Instant.now());
        Post newPost = new Post();
        newPost.setId(uuid.toString());
        newPost.setTimeOfPost(timestamp);
        newPost.setPostContent(newPostRequest.getPostContent());
        newPost.setPostTitle(newPostRequest.getPostTitle());
        newPost.setPosterId(posterId);
        return newPost;
    }

}
