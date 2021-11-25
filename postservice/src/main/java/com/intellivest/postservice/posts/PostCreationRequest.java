package com.intellivest.postservice.posts;

/**
 * class to represent post creation JSON requests
 */
public class PostCreationRequest {

    private String postTitle;
    private String postContent;

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public PostCreationRequest(String postTitle, String postContent) {
        this.postTitle = postTitle;
        this.postContent = postContent;
    }

    private PostCreationRequest(){
    }

}
