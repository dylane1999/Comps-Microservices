package com.intellivest.userservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "bad request creating user" )
public class CreateUserBadRequestException extends RuntimeException {
    public CreateUserBadRequestException(String message){
        super(message);
    }
    
}
