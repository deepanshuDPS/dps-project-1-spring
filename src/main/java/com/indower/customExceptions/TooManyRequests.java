package com.indower.customExceptions;

public class TooManyRequests extends CustomErrorException {
    
    public TooManyRequests(String msg){
        super(msg);
    }
}
