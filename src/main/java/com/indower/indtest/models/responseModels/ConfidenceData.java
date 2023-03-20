package com.indower.indtest.models.responseModels;

import com.google.gson.annotations.SerializedName;

public class ConfidenceData {

    public static final String NSFW = "nsfw";
    public static final String CAR = "car";

    @SerializedName("label")
    private String label;

    @SerializedName("confidence")
    private Double confidence;

    public String getLabel() {
        return label.toLowerCase();
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "label: "+getLabel()+" prediction:"+getConfidence();
    }

}
