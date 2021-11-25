package com.intellivest.postservice.controller;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.intellivest.postservice.posts.Post;
import com.intellivest.postservice.posts.PostCreationRequest;
import com.intellivest.postservice.posts.PostDAO;
import com.intellivest.postservice.util.PostUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class PostsController {

    @Autowired
    PostDAO postDAO;

    @Autowired
    PostUtil postUtil;

    /**
     * api endpoint for adding a new post into the database poster id is added from
     * the jwt owner id
     * 
     * @param newPostRequest
     * @param authorization
     * @return
     * @throws Exception
     * @throws RuntimeException
     */
    @CrossOrigin
    @PutMapping("/post")
    public Post createNewPost(@RequestBody PostCreationRequest newPostRequest,
            @RequestHeader("Authorization") String authorization) throws Exception, RuntimeException {
        /** create post from JSON and add to DB */
        String posterId = PostUtil.decodeIdFromJwt(authorization);
        Post post = Post.createNewPost(newPostRequest, posterId);
        postDAO.addPost(post);
        return post;
    }

    /**
     * api endpoint that is used to get a post from the database via its id
     * 
     * @param id
     * @return
     * @throws Exception
     */
    @CrossOrigin
    @GetMapping("/post")
    public Post getPostById(@RequestParam String postId) throws Exception {
        Post result = postDAO.getPostById(postId);
        return result;
    }

    /**
     * api endpoint that is used to query all of the posts made by the given user id
     * 
     * 
     * @param id
     * @return
     * @throws Exception
     */
    @CrossOrigin
    @GetMapping("/post/user")
    public List<Post> getPostsByUser(@RequestParam String userId) throws Exception {
        List<Post> result = postDAO.getPostsByUser(userId);
        return result;
    }

    /**
     * api endpoint to delete a post from the databse. 
     * will return 404 if post is not found. 
     * @param postId
     */
    @CrossOrigin
    @DeleteMapping("/post")
    public void deletePost(@RequestParam String postId) {
        Post post = postDAO.getPostById(postId);
        postDAO.deletePost(post);
        return;
    }
}
