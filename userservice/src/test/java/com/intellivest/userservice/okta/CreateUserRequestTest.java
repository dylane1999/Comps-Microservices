package com.intellivest.userservice.okta;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intellivest.userservice.util.OktaUtil;

import org.junit.jupiter.api.Test;

public class CreateUserRequestTest {

    /**
     * test for ability to convert the request to the okta format
     */
    @Test
    public void testForConvertingRequestToOktaFormat() {
        CreateUserRequest createUserRequest = new CreateUserRequest("email", "first_name", "last_name", "password");
        ObjectNode node = OktaUtil.convertRequestToOktaFormat(createUserRequest);
        assertEquals("email", node.at("/profile/email").asText());
        assertEquals("email", node.at("/profile/login").asText());
        assertEquals("first_name", node.at("/profile/firstName").asText());
        assertEquals("last_name", node.at("/profile/lastName").asText());
        assertEquals("password", node.at("/credentials/password/value").asText());
    }

}
