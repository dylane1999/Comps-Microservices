package com.intellivest.postservice.util;

import java.sql.Timestamp;

import javax.sql.DataSource;

import com.intellivest.postservice.posts.Post;
import com.intellivest.postservice.posts.PostCreationRequest;

import org.springframework.jdbc.core.JdbcTemplate;

public class TestUtil {

    private static PostCreationRequest postCreationRequest = new PostCreationRequest("postTitle", "postContent");

    private static Post testPost = new Post();
    static {
        testPost.setId("72001973-f807-4f0d-869c-6c9250d20936");
        testPost.setPosterId("00unfsrxoMlovRpMk5d6");
        testPost.setPostContent("postContent");
        testPost.setPostTitle("postTitle");
        testPost.setTimeOfPost(Timestamp.valueOf("2021-10-14 13:51:18"));
    }

    public static Post getTestPost() {
        return testPost;
    }
    public static PostCreationRequest getPostCreationRequest() {
        return postCreationRequest;
    }

    

    /**
     * a test util finction to add a post into the database
     * 
     * @param dataSource
     */
    public static void loadPostIntoDatabase(DataSource dataSource) {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate();
            jdbcTemplate.setDataSource(dataSource);
            jdbcTemplate.execute("USE intellivest_posts_db;");
            jdbcTemplate.execute(
                    "INSERT INTO posts (id, poster_id, post_title, post_content, time_of_post) values ('72001973-f807-4f0d-869c-6c9250d20936','00unfsrxoMlovRpMk5d6', 'postTitle', 'postContent', '2021-10-14 13:51:18');");
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * a test util function to create a clean database with no posts
     * 
     * @param dataSource
     */
    public static void clearPostsFromDatabase(DataSource dataSource) {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate();
            jdbcTemplate.setDataSource(dataSource);
            jdbcTemplate.execute("USE intellivest_posts_db;");
            jdbcTemplate.execute("TRUNCATE TABLE posts;");
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * util function to check if two posts are the same 
     * 
     * @param expectedPost
     * @param actualPost
     * @return
     */
    public static boolean arePostsTheSame(Post expectedPost, Post actualPost) {
        if (!expectedPost.getId().equals(actualPost.getId())) {
            return false;
        }
        if (!expectedPost.getPostContent().equals(actualPost.getPostContent())) {
            return false;
        }
        if (!expectedPost.getPostTitle().equals(actualPost.getPostTitle())) {
            return false;
        }
        if (!expectedPost.getPosterId().equals(actualPost.getPosterId())) {
            return false;
        }
        if (!expectedPost.getTimeOfPost().equals(actualPost.getTimeOfPost())) {
            return false;
        }
        return true;
    }

}
