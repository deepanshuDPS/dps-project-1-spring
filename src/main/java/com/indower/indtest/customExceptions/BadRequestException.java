package com.indower.indtest.customExceptions;

public class BadRequestException extends CustomErrorException {
    
    public BadRequestException(String msg){
        super(msg);
    }
}
