package com.intellivest.userservice.okta;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.UUID;

import com.intellivest.userservice.exceptions.CreateUserBadRequestException;
import com.intellivest.userservice.exceptions.OktaException;
import com.intellivest.userservice.exceptions.UserNotFoundException;
import com.intellivest.userservice.testUtil.TestUtil;
import com.intellivest.userservice.user.User;
import com.intellivest.userservice.util.OktaUtil;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles(profiles = "dev")
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class OktaServiceTest {

    @Autowired
    OktaService oktaService;

    /**
     * test for succesfully creating a user in okta and then deleting that user
     * 
     * @throws IOException
     */
    @Test
    @Order(1)
    public void testForSuccesfulCreateUserInOkta() throws IOException {
        String userEmail = String.format("%s@gmail.com", UUID.randomUUID().toString());
        CreateUserRequest createUserRequest = new CreateUserRequest(userEmail, "first_name", "last_name",
                "lakssd$%(U*HR()*HNFSD5y4298");
        User createdUser = oktaService.createUserOkta(OktaUtil.convertRequestToOktaFormat(createUserRequest));
        assertEquals(userEmail, createdUser.getEmail(), "check that the email matches");
        assertEquals("first_name", createdUser.getFirstName(), "check that the first name matches");
        assertEquals("last_name", createdUser.getLastName(), "check that the last name matches");
        assertNotNull(createdUser.getId(), "verify that the user id exists");
        // now test for deleting this user
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest(createdUser.getId(), createdUser.getEmail(),
                createdUser.getFirstName(), createdUser.getLastName());
        assertDoesNotThrow(() -> oktaService.handleDeleteUser(deleteUserRequest));
    }

    /**
     * test for failure when creating a user in okta that already exists
     */
    @Test
    @Order(2)
    public void testForFailedCreateUserInOktaDueToRepeatedEmail() {
        CreateUserRequest createUserRequest = new CreateUserRequest(TestUtil.getTestUser().getEmail(),
                TestUtil.getTestUser().getFirstName(), TestUtil.getTestUser().getLastName(),
                "lakssd$%(U*HR()*HNFSD5y4298");
        assertThrows(CreateUserBadRequestException.class,
                () -> oktaService.createUserOkta(OktaUtil.convertRequestToOktaFormat(createUserRequest)));
    }

    /**
     * test failure for deleting a user that does not exist in okta
     * 
     * @throws IOException
     */
    @Test
    @Order(3)
    public void testForFailureDeletingUserFromOktaDueToUnknownUser() throws IOException {
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest("ID", "EMAIL", "FNAME", "LNAME");
        assertThrows(UserNotFoundException.class, () -> oktaService.handleDeleteUser(deleteUserRequest));
    }

}
