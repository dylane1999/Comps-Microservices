package com.intellivest.userservice.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intellivest.userservice.okta.CreateUserRequest;
import com.intellivest.userservice.user.User;

// util class for interacting with okta
public class OktaUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * util method that is used to create a user object from the okta create user
     * response
     * 
     * @param rootNode - the JSON response from okta
     * @return - returns the created user java object
     */
    public static User createUserFromResponse(JsonNode rootNode) {
        /** extract profile attributes from okta response */
        String oktaUID = rootNode.at("/id").asText();
        String firstName = rootNode.at("/profile/firstName").asText();
        String lastName = rootNode.at("/profile/lastName").asText();
        String email = rootNode.at("/profile/email").asText();
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setId(oktaUID);
        user.setEmail(email);
        return user;
    }

        /**
     * method to get the create user request in the specific okta format
     * @return
     */
    public static ObjectNode convertRequestToOktaFormat(CreateUserRequest createUserRequest) {
        // create main object root
        ObjectNode root = objectMapper.createObjectNode();
        // create and set profile node
        ObjectNode profileNode = objectMapper.createObjectNode();
        profileNode.put("firstName", createUserRequest.getFirstName());
        profileNode.put("lastName", createUserRequest.getLastName());
        profileNode.put("email", createUserRequest.getEmail());
        profileNode.put("login", createUserRequest.getEmail());
        root.set("profile", profileNode);
        // create and set password node
        ObjectNode credentialsNode = objectMapper.createObjectNode();
        ObjectNode passwordValueNode = objectMapper.createObjectNode();
        passwordValueNode.put("value", createUserRequest.getPassword());
        credentialsNode.set("password", passwordValueNode);
        root.set("credentials", credentialsNode);
        // return the completed okta create user json
        return root;
    }

}
