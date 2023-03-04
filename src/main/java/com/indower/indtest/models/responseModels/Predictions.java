package com.indower.indtest.models.responseModels;

import com.google.gson.annotations.SerializedName;

class Preditions {
    @SerializedName("label")
    private String label;

    @SerializedName("confidence")
    private Double confidence;

    public String getLabel() {
        return label;
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
