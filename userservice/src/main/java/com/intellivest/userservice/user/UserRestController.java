package com.intellivest.userservice.user;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellivest.userservice.exceptions.UserNotFoundException;
import com.intellivest.userservice.okta.CreateUserRequest;
import com.intellivest.userservice.okta.DeleteUserRequest;
import com.intellivest.userservice.okta.OktaService;
import com.intellivest.userservice.util.OktaUtil;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// controller for rest endpoints of user service
@Component
@RestController
public class UserRestController {

    @Autowired
    public UserDAO userDAO;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public OktaService oktaService;

    /**
     * rest api route for querying a user by the id
     * 
     * @param id - the user id given as a request param
     * @return
     * @throws Exception
     * @throws UserNotFoundException
     */
    @CrossOrigin
    @GetMapping("/user")
    public User getUserById(@RequestParam String userId, @RequestHeader("Authorization") String authorization)
            throws Exception, UserNotFoundException {
        User result = userDAO.getUserByID(userId);
        return result;
    }

    /**
     * the api route to add a new user, will create a new user with okta and then
     * add that user to the db
     * 
     * @param createUserRequest
     * @return
     * @throws Exception
     * @throws UserNotFoundException
     */
    @CrossOrigin
    @PutMapping("/user")
    public User addUser(@RequestBody CreateUserRequest createUserRequest) throws Exception, UserNotFoundException {
        User newUser = oktaService.createUserOkta(OktaUtil.convertRequestToOktaFormat(createUserRequest));
        userDAO.addPerson(newUser);
        return newUser;
    }

    /**
     * the api route that is used to delete a user from the application
     * 
     * @param deleteUserRequest
     * @return
     * @throws IOException
     */
    @CrossOrigin
    @DeleteMapping("/user")
    public void deleteUser(@RequestBody DeleteUserRequest deleteUserRequest) throws IOException {
        oktaService.handleDeleteUser(deleteUserRequest);
        User deletedUser = User.createUser(deleteUserRequest.getId(), deleteUserRequest.getFirstName(),
                deleteUserRequest.getLastName(), deleteUserRequest.getEmail());
        userDAO.deletePerson(deletedUser);
    }

}
