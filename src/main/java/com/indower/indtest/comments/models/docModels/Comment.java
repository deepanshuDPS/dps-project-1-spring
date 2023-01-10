package com.indower.indtest.comments.models.docModels;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "comment")
public class Comment {
    
    private String _id;

    // it's not fetched for response
    private String commentorId;

    @JsonProperty(value = "commentedId", required = true)
    @NotNull(message = "Please mention commentedId")
    private String commentedId;

    @JsonProperty(value = "comment", required = true)
    @NotNull(message = "Please give comment")
    private String comment;

    @JsonProperty(value = "isAbusive", required = true)
    @NotNull(message = "Please predict comment first")
    private Boolean isAbusive;
    
    @JsonProperty(value = "predictionResult", required = true)
    @NotNull(message = "Please predict comment first")
    private Float predictionResult;

    private Boolean isShow = true;

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

    // commentor id never open
    public String getCommentorId() {
        return null;
    }

    public void setCommentorId(String commentorId) {
        this.commentorId = commentorId;
    }

    public String getCommentedId() {
        return commentedId;
    }

    public void setCommentedId(String commentedId) {
        this.commentedId = commentedId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getIsAbusive() {
        return isAbusive;
    }

    public void setIsAbusive(Boolean isAbusive) {
        this.isAbusive = isAbusive;
    }

    public Float getPredictionResult() {
        return predictionResult;
    }

    public void setPredictionResult(Float predictionResult) {
        this.predictionResult = predictionResult;
    }

    public Boolean getIsShow() {
        return isShow;
    }

    public void setIsShow(Boolean isShow) {
        this.isShow = isShow;
    }

    
    
    
}
