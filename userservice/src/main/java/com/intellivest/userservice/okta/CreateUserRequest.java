
package com.intellivest.userservice.okta;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * class that is used to represent the request to create a user in JSON
 */
public class CreateUserRequest {

    /**
     * public constructor for create user request object
     * 
     * @param email
     * @param firstName
     * @param lastName
     * @param password
     */
    public CreateUserRequest(String email, String firstName, String lastName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    private CreateUserRequest() {
    }

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    @Autowired
    ObjectMapper objectMapper;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
