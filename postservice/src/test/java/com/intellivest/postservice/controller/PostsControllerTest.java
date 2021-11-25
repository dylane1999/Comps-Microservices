package com.intellivest.postservice.controller;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellivest.postservice.posts.Post;
import com.intellivest.postservice.posts.PostCreationRequest;
import com.intellivest.postservice.util.TestUtil;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.util.List;
import java.util.UUID;

import javax.print.attribute.standard.Media;
import javax.transaction.Transactional;

@Transactional
@EnableTransactionManagement
@ActiveProfiles(profiles = "dev")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class PostsControllerTest {

    @Autowired
    MockMvc mockMvc;

    private static final String JWT_CONTENT = "eyJraWQiOiJreElsWS1rUU1pcnpTX1o0RG1yM3NES1ZEUzhVaHJvR0lhemdla1gyYWJjIiwiYWxnIjoiUlMyNTYifQ.eyJ2ZXIiOjEsImp0aSI6IkFULm9Ham5OdnRqVTZkS05jeWJRVGM3N1E2V2hWUVRrcDhMU3BxaDVfbHpCWHciLCJpc3MiOiJodHRwczovL2Rldi0yNTY2NTU3NS5va3RhLmNvbS9vYXV0aDIvZGVmYXVsdCIsImF1ZCI6ImFwaTovL2RlZmF1bHQiLCJpYXQiOjE2MzMyMTU5NDMsImV4cCI6MTYzMzMwMjM0MywiY2lkIjoiMG9hb2MwaHdkdW1QVzJrWlA1ZDYiLCJ1aWQiOiIwMHVuZnNyeG9NbG92UnBNazVkNiIsInNjcCI6WyJvcGVuaWQiXSwic3ViIjoiZHlsYW5lZHdhcmRzMjkwQGdtYWlsLmNvbSJ9.gpCNcEJ6C40sLaSGqSlStG81tRMwwrBwHYgvYjI9IK5Z01tMVAc1v9QmsdUrHJxPU2-z6_8k7MxuGJyzWNyL8MPzGbPOhTy9sFNaYOPPQYe-BVRLW-pwhsBtE6fCiAzMyXWVf_puDUI07SoG3SSxM9trh3wMpFCXtuwcNek-LaVykMfuCvZo2-mCMfCrUBdcLjgO0ASVLX-368AqGBHWqw3F58_nk3ZcMD2SEZW41U3gbIK9UWE8wVdYd5LJhrrAJhVlz2glS6-G-daqmP5ui7Ob_c7qn_APUGbaKXs_ZRpFf0HG0-WHA-JZymWPn8SF5vKm0S1Nkf5BfIP0sRKpnw";

    private Jwt testJwt = Jwt.withTokenValue(JWT_CONTENT).header("alg", "none").claim("sub", "user")
            .claim("scope", "read").build();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${database.username}")
    private String username;

    @Value("${database.password}")
    private String password;

    @Value("${database.jdbcUrl}")
    private String jdbcUrl;

    public BasicDataSource basicDataSource = new BasicDataSource();

    @BeforeEach
    public void setUp() {
        basicDataSource.setDriverClassName("org.h2.Driver");
        basicDataSource.setPassword(password);
        basicDataSource.setUsername(username);
        basicDataSource.setUrl(jdbcUrl);
    }

    /**
     * api test for creating a new post
     * 
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void test_createNewPost_Success() throws Exception {
        // clear the posts from the databse
        TestUtil.clearPostsFromDatabase(basicDataSource);
        // do the api call to add a post to the db
        String requestBody = objectMapper.writeValueAsString(TestUtil.getPostCreationRequest());
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/post")
                .contentType(MediaType.APPLICATION_JSON).content(requestBody).with(jwt().jwt(testJwt))
                .header("Authorization", testJwt.getTokenValue()).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
        // check that the added post has the expected title and post content
        Post addedPost = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsByteArray(),
                Post.class);
        assertEquals(TestUtil.getTestPost().getPostTitle(), addedPost.getPostTitle(),
                "check that the titles are the same");
        assertEquals(TestUtil.getTestPost().getPostContent(), addedPost.getPostContent(),
                "check that the content is the same");
        // check that the id and the time are not the same
        assertNotEquals(TestUtil.getTestPost().getId(), addedPost.getId(),
                "check that the Id are not the same as a new one should have been created");
        assertTrue(TestUtil.getTestPost().getTimeOfPost().before(addedPost.getTimeOfPost()),
                "check that a new time was added when post was made so it should be after the test post timestamp");
    }

    /**
     * api test for get request to query a post by its Id
     * 
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void test_getPostById_success() throws Exception {
        // clear and load post
        TestUtil.clearPostsFromDatabase(basicDataSource);
        TestUtil.loadPostIntoDatabase(basicDataSource);
        // make api call to query the post
        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders.get("/post").contentType(MediaType.APPLICATION_JSON)
                        .param("postId", TestUtil.getTestPost().getId()).with(jwt().jwt(testJwt))
                        .header("Authorization", testJwt.getTokenValue()).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
        // get the queried post from JSON
        Post queriedPost = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsByteArray(),
                Post.class);
        // check that queried post is the same as test post
        assertTrue(TestUtil.arePostsTheSame(TestUtil.getTestPost(), queriedPost), "check that posts are the same");

    }

    /**
     * api test for failure to query a post by that does not exist
     * 
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void test_getPostById_failure_postDoesNotExist() throws Exception {
        // clear post
        TestUtil.clearPostsFromDatabase(basicDataSource);
        // make api call to query the post that does not exist
        mockMvc.perform(MockMvcRequestBuilders.get("/post").contentType(MediaType.APPLICATION_JSON)
                .param("postId", "FAKE_ID").with(jwt().jwt(testJwt)).header("Authorization", testJwt.getTokenValue())
                .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * api test for succesfully querying the posts of a given user
     * 
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void test_getPostsByUser_success() throws Exception {
        // clear and load post
        TestUtil.clearPostsFromDatabase(basicDataSource);
        TestUtil.loadPostIntoDatabase(basicDataSource);
        // make api call to query posts from user
        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders.get("/post/user").contentType(MediaType.APPLICATION_JSON)
                        .param("userId", TestUtil.getTestPost().getPosterId()).with(jwt().jwt(testJwt))
                        .header("Authorization", testJwt.getTokenValue()).accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        // get response
        List<Post> queriedPosts = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Post.class));
        assertEquals(1, queriedPosts.size(), "there should only be one post by this user");
        assertTrue(TestUtil.arePostsTheSame(TestUtil.getTestPost(), queriedPosts.get(0)),
                "check that the posts are the same");

    }

    /**
     * api test for querying the posts of a user who has none. returns an empty list
     * of posts.
     * 
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void test_getPostsByUser_success_userHasNoPost() throws Exception {
        // clear post
        TestUtil.clearPostsFromDatabase(basicDataSource);
        // make api call to query posts from user
        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders.get("/post/user").contentType(MediaType.APPLICATION_JSON)
                        .param("userId", TestUtil.getTestPost().getPosterId()).with(jwt().jwt(testJwt))
                        .header("Authorization", testJwt.getTokenValue()).accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());

        // get response
        List<Post> queriedPosts = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Post.class));
        assertEquals(0, queriedPosts.size(), "there should only be no posts from this user");

    }

    /**
     * test for api endpoint to successfuly delete post of given id
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void test_deletePost_success() throws Exception {
        // clear and load post
        TestUtil.clearPostsFromDatabase(basicDataSource);
        TestUtil.loadPostIntoDatabase(basicDataSource);
        // make api call to query posts from user
        mockMvc.perform(MockMvcRequestBuilders.delete("/post").contentType(MediaType.APPLICATION_JSON)
                .param("postId", TestUtil.getTestPost().getId()).with(jwt().jwt(testJwt))
                .header("Authorization", testJwt.getTokenValue()).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    /**
     * test for failure of api endpoint to delete when given unknown id 
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void test_deletePost_failure_unknownPost() throws Exception {
        // clear post
        TestUtil.clearPostsFromDatabase(basicDataSource);
        // make api call to query posts from user
        mockMvc.perform(MockMvcRequestBuilders.delete("/post").contentType(MediaType.APPLICATION_JSON)
                .param("postId", TestUtil.getTestPost().getId()).with(jwt().jwt(testJwt))
                .header("Authorization", testJwt.getTokenValue()).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
