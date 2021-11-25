package com.intellivest.authservice.okta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellivest.authservice.exceptions.OktaException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles(profiles = "dev")
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class OktaTokenServiceTest {

    @Autowired
    OktaTokenService oktaTokenService;

    @Value("${testing.username}")
    private String username;

    @Value("${testing.password}")
    private String password;


    /**
     * Unit test for validating url encoded string creation
     */
    @Test
    public void testForUrlEncodedStringCreation(){
        String desired = "grant_type=password&username=login&password=password&scope=openid";
        String actual = OktaTokenService.createUrlencodedBodyString("login", "password");
        assertEquals(desired, actual);
    }

    /**
     * test getting a token from the okta service
     * 
     * @throws IOException
     */
    @Test
    public void testForSuccesfulTokenRetrieval() throws IOException {
        JsonNode root = oktaTokenService.getOktaToken(username, password);
        assertEquals("Bearer", root.at("/token_type").asText(), "the token should be of the type bearer");
        assertFalse(root.at("/access_token").isMissingNode(), "the access_token should be in the payload");
        assertFalse(root.at("/id_token").isMissingNode(), "the id_token should be in the payload");
    }

    /**
     * test failure getting an okta token due to bad creds
     */
    @Test
    public void testForFailedTokenRetrievalDueToIncorrectCredentials() {
        assertThrows(OktaException.class, () -> oktaTokenService.getOktaToken("username", "password"));
    }

}
