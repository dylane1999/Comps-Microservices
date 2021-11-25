
package com.intellivest.authservice.exceptions;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class OktaException extends IOException {

    public OktaException(String exception){
        super(exception);
    }
    
}

