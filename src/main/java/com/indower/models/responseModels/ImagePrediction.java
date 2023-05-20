package com.indower.models.responseModels;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ImagePrediction {

    @SerializedName("data")
    private List<Object> data;

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }


}
