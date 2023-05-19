package com.indower.rateReviews.models.docModels;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "rate-review")
public class RateReview {
    
    private String _id;

    private String reviewerId;

    @JsonProperty(value = "reviewedId", required = true)
    @NotNull(message = "Please mention reviewedId")
    private String reviewedId;

    @Min(1)
    @Max(5)
    @Nullable
    private Integer rating;

    private String review;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAt;
  
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date updatedAt;

    private Boolean isReported;

    @JsonProperty(value = "revName", required = true)
    @NotNull(message = "Reviewer name not exist")
    private String revName;

    // @JsonProperty(value = "revImage", required = true)
    // @NotNull(message = "Reviewer image not exist")
    // private String revImage;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewedId() {
        return reviewedId;
    }

    public void setReviewedId(String reviewedId) {
        this.reviewedId = reviewedId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
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

    public String getRevName() {
        return revName;
    }

    public void setRevName(String revName) {
        this.revName = revName;
    }

    // public String getRevImage() {
    //     return revImage;
    // }

    // public void setRevImage(String revImage) {
    //     this.revImage = revImage;
    // }

    
    
}
