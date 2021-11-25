package com.intellivest.userservice.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import com.intellivest.userservice.config.HibernateConfig;
import com.intellivest.userservice.exceptions.UserNotFoundException;
import com.intellivest.userservice.testUtil.TestUtil;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
public class UserDAOTest {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    HibernateConfig hibernateConfig;

    @Value("${database.username}")
    private String username;

    @Value("${database.password}")
    private String password;

    @Value("${database.jdbcUrl}")
    private String jdbcUrl;

    public  BasicDataSource basicDataSource = new BasicDataSource();

    @BeforeEach
    public void setUp(){
        basicDataSource.setDriverClassName("org.h2.Driver");
        basicDataSource.setPassword(password);
        basicDataSource.setUsername(username);
        basicDataSource.setUrl(jdbcUrl);
    }

    /**
     * test for succesfully getting a user from the DAO by the user's ID
     *
     */
    @DirtiesContext
    @Test
    public void testForSuccessfulGetUserById() {
        TestUtil.clearUserFromDatabase(basicDataSource);
        TestUtil.loadUserIntoDatabase(basicDataSource);
        User user = userDAO.getUserByID(TestUtil.getTestUser().getId());
        assertTrue(user.isUserTheSame(TestUtil.getTestUser()),
                "check that the user returned is the same as the test user");
    }

    /**
     * test for failure when looking up a user with an unknown Id
     */
    @DirtiesContext
    @Test
    public void testForFailedGetUserByIdDueToUnknownId() {
        TestUtil.clearUserFromDatabase(basicDataSource);
        TestUtil.loadUserIntoDatabase(basicDataSource);
        assertThrows(UserNotFoundException.class, () -> userDAO.getUserByID("USER_ID"));
    }

    /**
     * test for successfully adding a new user to the database
     */
    @DirtiesContext
    @Test
    public void testForSuccessfulAddUser() {
        TestUtil.clearUserFromDatabase(basicDataSource);
        assertDoesNotThrow(() -> userDAO.addPerson(TestUtil.getTestUser()),
                "check that adding a user to the database does not throw an exeption");
    }

    /**
     * test for failure in adding a user to the database that already exists
     */
    @DirtiesContext
    @Test
    public void testForFailedAddUserDueToAlreadyExistingUser() {
        TestUtil.clearUserFromDatabase(basicDataSource);
        TestUtil.loadUserIntoDatabase(basicDataSource);
        assertThrows(Exception.class, () -> userDAO.addPerson(TestUtil.getTestUser()),
                "check that adding user to db fails if that user already exists");
    }

    /**
     * test for succesfully deleting a given user from the database
     */
    @DirtiesContext
    @Test
    public void testForSuccessfulDeleteUser() {
        TestUtil.clearUserFromDatabase(basicDataSource);
        TestUtil.loadUserIntoDatabase(basicDataSource);
        assertDoesNotThrow(() -> userDAO.deletePerson(TestUtil.getTestUser()),
                "check that deleting a user from the db does not throw any errors");
    }

    /**
     * test for success when deleting a user from the db that doesn't exist
     */
    @DirtiesContext
    @Test
    public void testForSuccessDeleteUserDueToNonexistantUser() {
        TestUtil.clearUserFromDatabase(basicDataSource);
        assertDoesNotThrow(() -> userDAO.deletePerson(TestUtil.getTestUser()),
                "check that deleting a user from the db that doesn't exist will not throw any errors");
    }

}
