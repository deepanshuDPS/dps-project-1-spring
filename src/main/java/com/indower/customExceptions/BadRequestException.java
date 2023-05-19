package com.indower.customExceptions;

public class BadRequestException extends CustomErrorException {
    
    public BadRequestException(String msg){
        super(msg);
    }
}
