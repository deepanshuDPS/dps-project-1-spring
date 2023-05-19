package com.indower.models;


public class ApiError {

    private String message = "Partial Information";
    private String globalMessage;
    private boolean status = false;

    public ApiError(){

    }
    
    public ApiError(String message) {
        this.message = message;
        this.globalMessage = null;
    }

    public ApiError(String message, String globalMessage) {
        this.message = message;
        this.globalMessage = globalMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGlobalMessage() {
        return globalMessage;
    }

    public void setGlobalMessage(String globalMessage) {
        this.globalMessage = globalMessage;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    
}
