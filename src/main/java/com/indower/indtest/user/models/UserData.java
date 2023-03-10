package com.indower.indtest.user.models;

import com.indower.indtest.user.models.documentModels.UserDoc;

public class UserData extends UserDoc{
    

    private Integer anTextCount;

    private Float reviewsAvg;

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
