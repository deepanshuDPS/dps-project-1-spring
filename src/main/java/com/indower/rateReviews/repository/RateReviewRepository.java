package com.indower.rateReviews.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.indower.models.AverageResult;
import com.indower.rateReviews.models.docModels.RateReview;

public interface RateReviewRepository extends MongoRepository<RateReview, String> {

    Page<RateReview> findByReviewedId(String reviewedId, Pageable pageable);

    // @Aggregation("{  $match: { reviewedId: '?0' rating: { $ne: null, $ne: 0 } } }")
    // List<Document> findAvgAndCountOfRatings(String userId);

    @Aggregation(pipeline = {
        "{ $match: { reviewedId: ?0, rating: { $ne: null, $ne: 0 } } }",
        "{ $group: { _id: null, average: { $avg: \"$rating\" }, totalCount: { $sum: 1 } } }"
    })
    AggregationResults<AverageResult> findAvgAndCountOfRatingsByUserId(String userId);


    @Query("{reviewerId: '?0' _id:'?1'}")
    RateReview findReview(String reviewerId, String _id);

    @Query("{reviewerId: '?0' reviewedId:'?1'}")
    List<RateReview> findReviewForUser(String reviewerId, String reviewedId);

    @Query(value = "{reviewerId: '?0'}", count = true)
    Integer findReviewerCount(String reviewerId);

    default RateReview insertReview(RateReview review) {
        review.setCreatedAt(new Date());
        return insert(review);
    }

    default RateReview saveReview(RateReview review) {
        review.setUpdatedAt(new Date());
        return save(review);
    }
}
