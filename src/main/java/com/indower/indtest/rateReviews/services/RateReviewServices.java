package com.indower.indtest.rateReviews.services;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.indower.indtest.rateReviews.models.docModels.RateReview;
import com.indower.indtest.rateReviews.repository.RateReviewRepository;
import com.indower.indtest.user.repository.UserRepository;
import com.indower.indtest.utils.AppConstants;

@Service
public class RateReviewServices {

    public class ResResult {
        private int type;
        private RateReview rateReview;

        public ResResult(int type, RateReview rateReview) {
            this.type = type;
            this.rateReview = rateReview;
        }

        public int getType() {
            return type;
        }

        public RateReview getRateReview() {
            return rateReview;
        }

    }

    @Autowired
    private RateReviewRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    // if has data then page else null
    @Nullable
    public Page<RateReview> getReviews(String userId, Integer page) {

        Pageable paging = PageRequest.of(page, AppConstants.PAGE_SIZE);
        Page<RateReview> reviews = repository.findByReviewedId(userId, paging);
        if (reviews.getContent() != null && !reviews.getContent().isEmpty())
            return reviews;
        else
            return null;
    }

    public ResResult postReview(String uid, String reviewerId, RateReview rateReview) {

        if (rateReview.getReviewedId() == reviewerId) {
            return new ResResult(1, null);
        } else if (rateReview.getReview() == null && rateReview.getRating() == null) {
            return new ResResult(2, null);
        } else if (userRepository.findUser(uid, reviewerId) == null) {
            return new ResResult(3, null);
        } else if (userRepository.findUser(rateReview.getReviewedId()) == null) {
            return new ResResult(4, null);
        } else {
            rateReview.setReviewerId(reviewerId);
            return new ResResult(5, repository.insertReview(rateReview));
        }
    }

    public ResResult editReview(
            String rateReviewId, String reviewerId,
            Integer rating, String review) {

        // TODO:handle rating 1-5 here
        if (review == null && rating == null) {
            return new ResResult(2, null);
        }

        RateReview rateReview = repository.findReview(reviewerId, rateReviewId);
        if (rateReview == null) {
            return new ResResult(4, null);
        } else {
            if (review != null && !review.equals(rateReview.getReview())) {
                rateReview.setReview(review);
            }
            if (rating != null && !rating.equals(rateReview.getRating())) {
                rateReview.setRating(rating);
            }
            return new ResResult(5, repository.saveReview(rateReview));
        }

    }

}
