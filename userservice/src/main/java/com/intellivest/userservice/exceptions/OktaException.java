
package com.intellivest.userservice.exceptions;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * exception for errors thrown in communication with okta
 */
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Problem communicating with Okta")
public class OktaException extends IOException {

    public OktaException(String exception) {
        super(exception);
    }

}
