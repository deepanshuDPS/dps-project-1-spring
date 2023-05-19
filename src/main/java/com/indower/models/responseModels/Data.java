package com.indower.models.responseModels;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("label")
    private String label;

    @SerializedName("confidences")
    private List<ConfidenceData> confidences;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<ConfidenceData> getConfidences() {
        return confidences;
    }

    public void setConfidences(List<ConfidenceData> confidences) {
        this.confidences = confidences;
    }
}
