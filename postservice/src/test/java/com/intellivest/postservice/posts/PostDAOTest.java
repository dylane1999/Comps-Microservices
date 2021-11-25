package com.intellivest.postservice.posts;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.transaction.Transactional;

import com.intellivest.postservice.config.HibernateConfig;
import com.intellivest.postservice.exceptions.PostNotFoundException;
import com.intellivest.postservice.util.TestUtil;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Transactional
@EnableTransactionManagement
@ActiveProfiles(profiles = "dev")
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class PostDAOTest {

    @Autowired
    private PostDAO postDAO;

    @Autowired
    HibernateConfig hibernateConfig;

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
     * test for successful adding post to the db
     */
    @DirtiesContext
    @Test
    public void test_AddPost_Success() {
        TestUtil.clearPostsFromDatabase(basicDataSource);
        assertDoesNotThrow(() -> postDAO.addPost(TestUtil.getTestPost()),
                "check that adding a user to the DB does not throw an exception");
    }

    /**
     * test that verifies a javax PersistenceException will be thrown when a
     * duplicate post is attempted to be added to the db
     * 
     */
    @DirtiesContext
    @Test
    public void test_AddPost_Failure_PostAlreadyExists() {
        TestUtil.clearPostsFromDatabase(basicDataSource);
        TestUtil.loadPostIntoDatabase(basicDataSource);
        assertThrows(javax.persistence.PersistenceException.class, () -> postDAO.addPost(TestUtil.getTestPost()),
                "check that adding a post w/ ID that already exists throws an error");
    }

    /**
     * test to succesfully query posts made by a given user
     * 
     */
    @DirtiesContext
    @Test
    public void test_getPostsByUser_success() {
        // clear and then load post into database
        TestUtil.clearPostsFromDatabase(basicDataSource);
        TestUtil.loadPostIntoDatabase(basicDataSource);
        // get the user id of the poster and the thier lists of posts
        String userId = TestUtil.getTestPost().getPosterId();
        List<Post> userPosts = postDAO.getPostsByUser(userId);
        assertEquals(1, userPosts.size(), "there should be one post by this user");
        assertTrue(TestUtil.arePostsTheSame(TestUtil.getTestPost(), userPosts.get(0)),
                "check that the test post and the post that was queried are the same");
    }

    /**
     * test to check that querying the posts of a user with no posts will return an
     * empty list.
     */
    @DirtiesContext
    @Test
    public void test_getPostsByUser_success_userHasNoPosts() {
        TestUtil.clearPostsFromDatabase(basicDataSource);
        // get the posts from the given user id
        String userId = TestUtil.getTestPost().getPosterId();
        List<Post> posts = postDAO.getPostsByUser(userId);
        assertEquals(0, posts.size(), "check that the user has no posts available");
    }

    /**
     * test for the success of querying a post by the post Id
     * 
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void test_getPostById_success() throws Exception {
        // clear and then load in a post
        TestUtil.clearPostsFromDatabase(basicDataSource);
        TestUtil.loadPostIntoDatabase(basicDataSource);
        // query the post by the given id
        Post actualPost = postDAO.getPostById(TestUtil.getTestPost().getId());
        assertTrue(TestUtil.arePostsTheSame(TestUtil.getTestPost(), actualPost),
                "check that the test post and the queried post are the same");
    }

    /**
     * test for failure when querying for a post by id that does not exist
     */
    @DirtiesContext
    @Test
    public void test_getPostById_failure_postDoesNotExist() {
        TestUtil.clearPostsFromDatabase(basicDataSource);
        assertThrows(PostNotFoundException.class, () -> postDAO.getPostById(TestUtil.getTestPost().getId()),
                "check that querying a post by id that does not exist will throw an error");
    }

    /**
     * test for successfully deleting a post from the databse
     */
    @DirtiesContext
    @Test
    public void test_deletePost_success() {
        TestUtil.clearPostsFromDatabase(basicDataSource);
        TestUtil.loadPostIntoDatabase(basicDataSource);
        assertDoesNotThrow(() -> postDAO.deletePost(TestUtil.getTestPost()),
                "check that deleting the post does not throw any errors");

    }

    /**
     * test for success when delete an already non-existant post this is because if
     * it doesnt exist it is already removed 
     */
    @DirtiesContext
    @Test
    public void test_deletePost_success_deleteUnkownPost() {
        TestUtil.clearPostsFromDatabase(basicDataSource);
        assertDoesNotThrow(() -> postDAO.deletePost(TestUtil.getTestPost()),
                "check that deleting the post does not throw any errors");
    }

}
