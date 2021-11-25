package com.intellivest.userservice.okta;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intellivest.userservice.user.User;
import com.intellivest.userservice.util.OktaUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.intellivest.userservice.exceptions.CreateUserBadRequestException;
import com.intellivest.userservice.exceptions.OktaException;
import com.intellivest.userservice.exceptions.UserNotFoundException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;

/**
 * class that contains methofs used to interface with okta
 */
@Service
public class OktaService {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public OkHttpClient client = new OkHttpClient();

    @Value("${okta.base.url}")
    private String OKTA_BASE_URL;

    private ObjectMapper mapper = new ObjectMapper();

    @Value("${okta.authorization}")
    private String apiKey;

    @Value("${gateway.location}")
    private String gatewayLocation;

    /**
     * used to create a user from the response of creating a user in the okta oauth
     * server
     * 
     * @param {User} user
     * @return
     * @throws Exception
     */
    public User createUserOkta(ObjectNode createUserRequest) throws IOException {
        Response response = null;
        try {
            // ** write user as a json string and post to Okta to create user */
            String reqBodyAsString = mapper.writeValueAsString(createUserRequest);
            RequestBody reqBody = RequestBody.create(reqBodyAsString.getBytes(), JSON);
            String activateUserEndpoint = String.format("%s?%s", OKTA_BASE_URL, "activate=true");
            Request request = new Request.Builder().url(activateUserEndpoint).header("Authorization", apiKey)
                    .header("Accept", "application/json").header("Content-Type", "application/json").post(reqBody)
                    .build();
            Call call = client.newCall(request);
            /** get response and check if user was created */
            response = call.execute();
            if (!response.isSuccessful()) {
                if (response.code() == HttpStatus.BAD_REQUEST.value()) {
                    throw new CreateUserBadRequestException(
                            "failure to create user due to bad request: " + response.body().string());
                }
                throw new OktaException("user could not be created: " + response.body().string());
            }
            /** read UID from okta and create a new user object */
            JsonNode rootNode = mapper.readTree(response.body().string());
            User user = OktaUtil.createUserFromResponse(rootNode);
            return user;
        } finally {
            // close the response after completion
            if (response != null) {
                response.close();
            }
        }
    }

    private void deactivateUser(DeleteUserRequest deleteUserRequest) throws IOException {
        Response response = null;
        try {
            // ** create url path to deactivate user and make okta api call */
            String deactivateUserEndpoint = String.format("%s/%s/%s", OKTA_BASE_URL, deleteUserRequest.getId(),
                    "lifecycle/deactivate");
            RequestBody reqBody = RequestBody.create("", JSON);
            Request request = new Request.Builder().url(deactivateUserEndpoint).header("Authorization", apiKey)
                    .header("Accept", "application/json").header("Content-Type", "application/json").post(reqBody)
                    .build();
            Call call = client.newCall(request);
            /** get response and check if user was deactivated */
            response = call.execute();
            if (!response.isSuccessful()) {
                if (response.code() == HttpStatus.NOT_FOUND.value()) {
                    throw new UserNotFoundException("user not found" + response.body().string());
                }
                throw new OktaException("user could not be deactivated: " + response.body().string());
            }
        } finally {
            // close the response after completion
            if (response != null) {
                response.close();
            }
        }
    }

    private void deleteUserFromOkta(DeleteUserRequest deleteUserRequest) throws IOException {
        Response response = null;
        try {
            // ** create url path to delete user and make okta api call */
            String deleteUserEndpoint = String.format("%s/%s", OKTA_BASE_URL, deleteUserRequest.getId());
            Request request = new Request.Builder().url(deleteUserEndpoint).header("Authorization", apiKey)
                    .header("Accept", "application/json").header("Content-Type", "application/json").delete().build();
            Call call = client.newCall(request);
            /** get response and check if user was deleted */
            response = call.execute();
            if (!response.isSuccessful()) {
                if (response.code() == HttpStatus.NOT_FOUND.value()) {
                    throw new UserNotFoundException("user not found" + response.body().string());
                }
                throw new OktaException("user could not be deactivated: " + response.body().string());
            }
        } finally {
            // close the response after completion
            if (response != null) {
                response.close();
            }
        }
    }

    public DeleteUserRequest handleDeleteUser(DeleteUserRequest deleteUserRequest) throws IOException {
        this.deactivateUser(deleteUserRequest);
        this.deleteUserFromOkta(deleteUserRequest);
        return deleteUserRequest;
    }
}