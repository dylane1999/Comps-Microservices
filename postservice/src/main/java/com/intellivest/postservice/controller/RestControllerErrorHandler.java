package com.intellivest.postservice.controller;

import java.util.Date;

import com.intellivest.postservice.config.ExceptionResponse;
import com.intellivest.postservice.exceptions.PostNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * handle the formatting of the error messages returned in case of bad requests or internal errors
 */
@ControllerAdvice
public class RestControllerErrorHandler {

    /**
     * handle post not found exceptions 
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(PostNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handlePostNotFoundException(PostNotFoundException ex, WebRequest request) {
      ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
          request.getDescription(true));
      return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }


}
