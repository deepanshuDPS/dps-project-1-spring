package com.indower.indtest.customExceptions;

public class TooManyRequests extends CustomErrorException {
    
    public TooManyRequests(String msg){
        super(msg);
    }
}
