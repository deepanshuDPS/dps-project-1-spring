package com.indower.indtest.models.responseModels;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class TextPrediction {

    @SerializedName("data")
    private List<String> data;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }


}
