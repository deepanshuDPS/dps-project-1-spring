package com.indower.indtest.models.responseModels;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ImagePrediction {

    @SerializedName("data")
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }


}
