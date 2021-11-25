package com.intellivest.postservice.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * exception that is thrown when querying for a post that doesn't exist
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Post does not exist")
public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(String message){
        super(message);
    }
    
}
