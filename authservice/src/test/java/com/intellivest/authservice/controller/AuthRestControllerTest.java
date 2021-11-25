package com.intellivest.authservice.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// @TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles(profiles = "dev")
@ExtendWith({ SpringExtension.class })
@SpringBootTest
@AutoConfigureMockMvc // need this in Spring Boot test
public class AuthRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${testing.username}")
    private String username;

    @Value("${testing.password}")
    private String password;

    /**
     * test for hitting rest api to get tokens successfully w correct info
     * 
     * @throws IOException
     * @throws Exception
     */
    @Test
    public void testForSuccesfulAuthTokenRetrieval() throws IOException, Exception {
        JsonNode response = objectMapper.readTree(mockMvc
                .perform(post("/auth/token").param("username", username).param("password", password)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsByteArray());

        assertEquals("Bearer", response.at("/token_type").asText(), "the token should be of the type bearer");
        assertFalse(response.at("/access_token").isMissingNode(), "the access_token should be in the payload");
        assertFalse(response.at("/id_token").isMissingNode(), "the id_token should be in the payload");

    }

    /**
     * test for failure when hitting the api endpoint for tokens with bad
     * credentials
     * 
     * @throws Exception
     */
    @Test
    public void testForFailedAuthTokenRetrivealDueToIncorrectCredentials() throws Exception {
        mockMvc.perform(
                post("/auth/token").param("username", "").param("password", "").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}
