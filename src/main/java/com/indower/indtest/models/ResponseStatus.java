package com.indower.indtest.models;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class ResponseStatus {
    
    @Transient
    @JsonDeserialize
    private String message;

    @Transient
    @JsonDeserialize
    private Boolean status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
