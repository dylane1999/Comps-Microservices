package com.intellivest.userservice.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellivest.userservice.okta.CreateUserRequest;
import com.intellivest.userservice.okta.DeleteUserRequest;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.util.UUID;
import javax.transaction.Transactional;


@Transactional
@EnableTransactionManagement
@ActiveProfiles(profiles = "dev")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class UserRestControllerTest {

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
    
        public  BasicDataSource basicDataSource = new BasicDataSource();
    
        @BeforeEach
        public void setUp(){
            basicDataSource.setDriverClassName("org.h2.Driver");
            basicDataSource.setPassword(password);
            basicDataSource.setUsername(username);
            basicDataSource.setUrl(jdbcUrl);
        }

        /**
         * test the api endpoint for succesfully querying users by Id
         * 
         * @throws Exception
         */
        @DirtiesContext
        @Test
        public void testForSuccessfulGetRequestToQueryUserById() throws Exception {
                TestUtil.clearUserFromDatabase(basicDataSource);
                TestUtil.loadUserIntoDatabase(basicDataSource);
                // execute the get request on /user to query user by userId
                ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/user")
                                .param("userId", TestUtil.getTestUser().getId()).with(jwt().jwt(testJwt))
                                .header("Authorization", testJwt.getTokenValue()))
                                .andExpect(MockMvcResultMatchers.status().isOk());
                // get the JSON response body and create the queried user
                JsonNode responseBody = objectMapper
                                .readTree(resultActions.andReturn().getResponse().getContentAsByteArray());
                User queriedUser = User.createUser(responseBody.at("/id").asText(),
                                responseBody.at("/firstName").asText(), responseBody.at("/lastName").asText(),
                                responseBody.at("/email").asText());
                // check that the users have the same values
                assertTrue(TestUtil.getTestUser().isUserTheSame(queriedUser),
                                "assert that the users have the same values");

        }

        /**
         * test the api endpoint for querying users by Id but use an unknown Id. the
         * request should result in a 404 not found status
         * 
         * @throws Exception
         */
        @DirtiesContext
        @Test
        public void testForFailedGetUserByIdDueToUnknownId() throws Exception {
                TestUtil.clearUserFromDatabase(basicDataSource);
                // execute the get request on /user but use a fake Id and check for 404 response
                mockMvc.perform(MockMvcRequestBuilders.get("/user").param("userId", "UNKNOWN_ID")
                                .with(jwt().jwt(testJwt)).header("Authorization", testJwt.getTokenValue()))
                                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        /**
         * test for creating user with api endpoint PUT /user, then directly after
         * delete that user with the DELETE /user endpoint
         * 
         * @throws Exception
         */
        @DirtiesContext
        @Test
        public void testForSuccessfulCreationAndDeleteUser() throws Exception {
                TestUtil.clearUserFromDatabase(basicDataSource);
                // create the CreateUserRequest and convert to json format
                String userEmail = String.format("%s@gmail.com", UUID.randomUUID().toString());
                CreateUserRequest createUserRequest = new CreateUserRequest(userEmail, "first_name", "last_name",
                                "lakssd$%(U*HR()*HNFSD5y4298");
                JsonNode createRequestBody = objectMapper.convertValue(createUserRequest, JsonNode.class);
                // perform the PUT request on /user to add new user
                ResultActions resultActions = mockMvc
                                .perform(MockMvcRequestBuilders.put("/user").content(createRequestBody.toString())
                                                .contentType(MediaType.APPLICATION_JSON).with(jwt().jwt(testJwt))
                                                .header("Authorization", testJwt.getTokenValue()))
                                .andExpect(MockMvcResultMatchers.status().isOk());

                // get the JSON response body and create the queried user
                JsonNode responseBody = objectMapper
                                .readTree(resultActions.andReturn().getResponse().getContentAsByteArray());
                User createdUser = User.createUser(responseBody.at("/id").asText(),
                                responseBody.at("/firstName").asText(), responseBody.at("/lastName").asText(),
                                responseBody.at("/email").asText());
                // check that the users have the same values
                assertEquals(createUserRequest.getEmail(), createdUser.getEmail(), "check that emails are the same");
                assertEquals(createUserRequest.getFirstName(), createdUser.getFirstName(),
                                "check that first names are the same");
                assertEquals(createUserRequest.getLastName(), createdUser.getLastName(),
                                "check that last names are the same");
                assertNotNull(createdUser.getId(), "check that the user has id");

                // now test for hitting the api endpoint to delete that user
                DeleteUserRequest deleteUserRequest = new DeleteUserRequest(createdUser.getId(), createdUser.getEmail(),
                                createdUser.getFirstName(), createdUser.getLastName());
                JsonNode deleteRequestBody = objectMapper.convertValue(deleteUserRequest, JsonNode.class);

                mockMvc.perform(MockMvcRequestBuilders.delete("/user").content(deleteRequestBody.toString())
                                .contentType(MediaType.APPLICATION_JSON).with(jwt().jwt(testJwt))
                                .header("Authorization", testJwt.getTokenValue()))
                                .andExpect(MockMvcResultMatchers.status().isOk());
        }

        /**
         * test for failure when attempting to hit the api endpoint for deletion on a
         * unknown user. the api should respond with a 404 not found code
         * 
         * @throws Exception
         */
        @DirtiesContext
        @Test
        public void testForFailedDeleteUserDueToMalformedUserInfo() throws Exception {
                TestUtil.clearUserFromDatabase(basicDataSource);
                TestUtil.loadUserIntoDatabase(basicDataSource);
                // construct the json for the fake user
                DeleteUserRequest deleteUserRequest = new DeleteUserRequest("ID", "EMAIL", "FNAME", "LNAME");
                JsonNode deleteRequestBody = objectMapper.convertValue(deleteUserRequest, JsonNode.class);
                // perform delete request with the fake user
                mockMvc.perform(MockMvcRequestBuilders.delete("/user").content(deleteRequestBody.toString())
                                .contentType(MediaType.APPLICATION_JSON).with(jwt().jwt(testJwt))
                                .header("Authorization", testJwt.getTokenValue()))
                                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        /**
         * test for failure to add user due to the user already existing in the system,
         * should return with a 400 bad request status
         * 
         * @throws Exception
         */
        @DirtiesContext
        @Test
        public void testForFailureAddUserDueToAlreadyExistingEmail() throws Exception {
                TestUtil.clearUserFromDatabase(basicDataSource);
                TestUtil.loadUserIntoDatabase(basicDataSource);
                // create the CreateUserRequest w/ test user info and convert to json format
                CreateUserRequest createUserRequest = new CreateUserRequest(TestUtil.getTestUser().getEmail(),
                                TestUtil.getTestUser().getFirstName(), TestUtil.getTestUser().getLastName(),
                                "lakssd$%(U*HR()*HNFSD5y4298");
                JsonNode createRequestBody = objectMapper.convertValue(createUserRequest, JsonNode.class);
                // perform the PUT request on /user to add new user
                mockMvc.perform(MockMvcRequestBuilders.put("/user").content(createRequestBody.toString())
                                .contentType(MediaType.APPLICATION_JSON).with(jwt().jwt(testJwt))
                                .header("Authorization", testJwt.getTokenValue()))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

}
