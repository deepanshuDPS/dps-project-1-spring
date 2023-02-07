package com.indower.indtest.controllerAdvice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.indower.indtest.customExceptions.CredentialsRequired;
import com.indower.indtest.models.ApiError;
import com.indower.indtest.utils.MyResponseUtils;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiError apiError = new ApiError();
        if(ex.getBindingResult().getFieldErrors().size()>0){
            apiError.setMessage(ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
        }
        if(ex.getBindingResult().getGlobalErrors().size()>0){
            apiError.setGlobalMessage(ex.getBindingResult().getGlobalErrors().get(0).getDefaultMessage());
        }
        return MyResponseUtils.badRequest(apiError);
    }

    // we can handle different types of exceptions with this custom reponse
    @ExceptionHandler({ CredentialsRequired.class })
    public ResponseEntity<Object> handleTwoExceptions(HttpServletRequest request, CredentialsRequired exception) {
        return MyResponseUtils.badRequest(new ApiError(exception.getMessage()));
    }

}
