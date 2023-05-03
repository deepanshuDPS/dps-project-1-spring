package com.indower.indtest.ans.models.docModels;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "ans")
public class ANS {
    
    private String _id;

    // it's not fetched for response
    private String doerId;

    @JsonProperty(value = "toWhomId", required = true)
    @NotNull(message = "Please mention toWhomId")
    private String toWhomId;

    @JsonProperty(value = "text", required = true)
    @NotNull(message = "Please give text")
    private String text;

    // @JsonProperty(value = "isAbusive", required = true)
    // @NotNull(message = "Please predict comment first")
    // private Boolean isAbusive;
    
    // @JsonProperty(value = "predictionResult", required = true)
    // @NotNull(message = "Please predict comment first")
    // private Float predictionResult;

    private Boolean isShow = true;

    private Boolean isAbusive = false;

    private Boolean isHelpful = false;

    private Boolean isPredAbusive = false;

    public Boolean getIsAbusive() {
        return isAbusive;
    }

    public void setIsAbusive(Boolean isAbusive) {
        this.isAbusive = isAbusive;
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAt;
  
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date updatedAt;

    private Boolean isReported;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsReported() {
        return isReported;
    }

    public void setIsReported(Boolean isReported) {
        this.isReported = isReported;
    }

    public Boolean getIsShow() {
        return isShow;
    }

    public void setIsShow(Boolean isShow) {
        this.isShow = isShow;
    }

    //doer id will not visible
    public String getDoerId() {
        return null;
    }

    public void setDoerId(String doerId) {
        this.doerId = doerId;
    }

    public String getToWhomId() {
        return toWhomId;
    }

    public void setToWhomId(String toWhomId) {
        this.toWhomId = toWhomId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public Boolean getIsHelpful() {
        return isHelpful;
    }

    public void setIsHelpful(Boolean isHelpful) {
        this.isHelpful = isHelpful;
    }

    public Boolean getIsPredAbusive() {
        return isPredAbusive;
    }

    public void setIsPredAbusive(Boolean isPredAbusive) {
        this.isPredAbusive = isPredAbusive;
    }

}
