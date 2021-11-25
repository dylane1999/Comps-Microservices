package com.intellivest.userservice.okta;


public class DeleteUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * public constructor for delete user request object
     * 
     * @param email
     * @param firstName
     * @param lastName
     * @param password
     */
    public DeleteUserRequest(String id, String email, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    private DeleteUserRequest(){

    }
}
