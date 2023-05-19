package com.indower.user.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.indower.user.models.documentModels.UserDoc;

import jakarta.validation.constraints.NotNull;

public class User extends UserDoc {

    @JsonProperty(value = "base64Image", required = true)
    @NotNull(message = "Please choose your image")
    private String base64Image;

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

}
