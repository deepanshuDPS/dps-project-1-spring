package com.indower.controllerAdvice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.indower.customExceptions.BadRequestException;
import com.indower.customExceptions.TooManyRequests;
import com.indower.customExceptions.CustomErrorException;
import com.indower.models.ApiError;
import com.indower.utils.MyResponseUtils;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiError apiError = new ApiError();
        if (ex.getBindingResult().getFieldErrors().size() > 0) {
            apiError.setMessage(ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
        }
        if (ex.getBindingResult().getGlobalErrors().size() > 0) {
            apiError.setGlobalMessage(ex.getBindingResult().getGlobalErrors().get(0).getDefaultMessage());
        }
        return MyResponseUtils.badRequest(apiError);
    }

    // we can handle different types of exceptions with this custom reponse
    @ExceptionHandler({ TooManyRequests.class, BadRequestException.class })
    public ResponseEntity<Object> handleTwoExceptions(HttpServletRequest request, CustomErrorException exception) {
        if((exception instanceof TooManyRequests))
            return MyResponseUtils.tooManyRequest(new ApiError(exception.getMessage()));
        else
            return MyResponseUtils.badRequest(new ApiError(exception.getMessage()));
    }

}
