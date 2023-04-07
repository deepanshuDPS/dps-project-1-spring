package com.indower.indtest.user.models;

import com.indower.indtest.rateReviews.models.docModels.RateReview;
import com.indower.indtest.user.models.documentModels.UserDoc;

public class UserData extends UserDoc{
    

    private Integer anTextCount;

    private Float reviewsAvg;

    private Integer anTextAsDoer;

    private Integer reviewsAsDoer;

    private RateReview yourRating;

    public RateReview getYourRating() {
        return yourRating;
    }

    public void setYourRating(RateReview yourRating) {
        this.yourRating = yourRating;
    }

    public Integer getAnTextAsDoer() {
        return anTextAsDoer;
    }

    public void setAnTextAsDoer(Integer anTextAsDoer) {
        this.anTextAsDoer = anTextAsDoer;
    }

    public Integer getReviewsAsDoer() {
        return reviewsAsDoer;
    }

    public void setReviewsAsDoer(Integer reviewsAsDoer) {
        this.reviewsAsDoer = reviewsAsDoer;
    }

    public Integer getAnTextCount() {
        return anTextCount;
    }

    public void setAnTextCount(Integer anTextCount) {
        this.anTextCount = anTextCount;
    }

    public Float getReviewsAvg() {
        return reviewsAvg;
    }

    public void setReviewsAvg(Float reviewsAvg) {
        this.reviewsAvg = reviewsAvg;
    }


    
}
