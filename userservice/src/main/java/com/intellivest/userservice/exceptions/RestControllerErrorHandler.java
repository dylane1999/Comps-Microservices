package com.intellivest.userservice.exceptions;

import java.util.Date;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestControllerErrorHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
      ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
          request.getDescription(false));
      return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CreateUserBadRequestException.class)
    public final ResponseEntity<ExceptionResponse> handleUserNotFoundException(CreateUserBadRequestException ex, WebRequest request) {
      ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
          request.getDescription(false));
      return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }


}
