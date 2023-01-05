package com.indower.indtest.user.models;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.indower.indtest.user.models.documentModels.UserDoc;

import jakarta.validation.constraints.NotNull;

public class User extends UserDoc {

    @JsonProperty(value = "languages", required = true)
    @NotNull(message = "Please select languages you know")
    private List<String> languages;

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

}
