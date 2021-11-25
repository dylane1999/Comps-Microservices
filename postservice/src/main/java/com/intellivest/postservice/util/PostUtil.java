package com.intellivest.postservice.util;

import java.util.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

@Component
public class PostUtil {

    private static Base64.Decoder decoder = Base64.getDecoder();

    private static ObjectMapper mapper = new ObjectMapper();


    /**
     * util function to decode the user id from a given access token 
     * @param authToken
     * @return
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public static String decodeIdFromJwt(String authToken) throws JsonMappingException, JsonProcessingException {
        String[] chunks = authToken.split("\\.");
        String payload = new String(decoder.decode(chunks[1]));
        JsonNode node = mapper.readTree(payload);
        String userId = node.at("/uid").asText();
        return userId;
    }

}
