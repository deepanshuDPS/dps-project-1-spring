package com.indower.indtest.models.responseModels;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("label")
    private String label;

    @SerializedName("confidences")
    private List<Preditions> confidences;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Preditions> getConfidences() {
        return confidences;
    }

    public void setConfidences(List<Preditions> confidences) {
        this.confidences = confidences;
    }
}
