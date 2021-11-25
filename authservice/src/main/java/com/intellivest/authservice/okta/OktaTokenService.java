package com.intellivest.authservice.okta;

import java.io.IOException;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intellivest.authservice.exceptions.OktaException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class OktaTokenService {

    private OkHttpClient client = new OkHttpClient();
    private MediaType formUrlencoded = MediaType.parse("application/x-www-form-urlencoded");
    private String BASE_URL = "https://dev-25665575.okta.com/oauth2/default/v1/token";

    @Autowired
    public ObjectMapper mapper;

    @Value("${okta.authorization}")
    private String oktaAuthorization;

    /**
     * method to get the okta id and access token given login and passowrd creds
     * 
     * @param login
     * @param password
     * @return
     * @throws IOException
     */
    public JsonNode getOktaToken(String login, String password) throws IOException {
        // ** write user as a json string and post to Okta to create user */
        String reqBodyAsString = createUrlencodedBodyString(login, password);
        /** construct and execute the http request to get the token from okta */
        RequestBody reqBody = RequestBody.create(reqBodyAsString.getBytes(), formUrlencoded);
        okhttp3.Request request = new Request.Builder().url(BASE_URL).addHeader("Authorization", oktaAuthorization)
                .header("Accept", "application/json").header("Content-Type", "application/x-www-form-urlencoded")
                .post(reqBody).build();
        Call call = client.newCall(request);
        /** get response and check if credentails were correct */
        Response response = call.execute();
        if (!response.isSuccessful()) {
            throw new OktaException("token could not be fetched: " + response.body().toString());
        }
        /** return the okta response containing the JWT auth */
        String jsonString = response.body().string();
        JsonNode rootNode = mapper.readTree(jsonString);
        return rootNode;
    }

    /**
     * util to create a url encoded body from the given credentials
     * @param login
     * @param password
     * @return
     */
    public static String createUrlencodedBodyString(String login, String password) {
        /** build a new url encoded form string to POST to okta */
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("grant_type=password&username=");
        stringBuilder.append(login);
        stringBuilder.append("&password=");
        stringBuilder.append(password);
        stringBuilder.append("&scope=openid");
        return stringBuilder.toString();
    }

}
