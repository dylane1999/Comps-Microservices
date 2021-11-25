package com.intellivest.authservice.controller;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intellivest.authservice.okta.OktaTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthRestController {

    @Autowired
    public OktaTokenService oktaTokenService;


    /**
     * route that is used to get an auth JWT from Okta
     * 
     * @param userCredentials - a JSON body with fields username and password
     * @return
     * @throws IOException
     */
    @PostMapping("/auth/token")
    public JsonNode fetchToken(@RequestParam("username") String username, @RequestParam("password") String password) throws IOException {
        /** extract credentails from JSON Request Body */
        JsonNode responseBody = oktaTokenService.getOktaToken(username, password);
        return responseBody;
    }


}
