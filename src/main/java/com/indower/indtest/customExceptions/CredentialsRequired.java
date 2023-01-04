package com.indower.indtest.customExceptions;

public class CredentialsRequired extends Throwable {
    
    public CredentialsRequired(String msg){
        super(msg);
    }
}
