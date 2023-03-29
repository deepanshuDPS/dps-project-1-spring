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
import com.indower.indtest.services.RedisMongoService;
import com.indower.indtest.user.repository.UserRepository;
import com.indower.indtest.utils.AppConstants;

@Service
public class RateReviewServices extends RedisMongoService {

    // if has data then page else null
    @Nullable
    public Page<RateReview> getReviews(String userId, Integer page) {

        Pageable paging = PageRequest.of(page, AppConstants.PAGE_SIZE);
        Page<RateReview> reviews = rateReviewRepository.findByReviewedId(userId, paging);
        if (reviews.getContent() != null && !reviews.getContent().isEmpty())
            return reviews;
        else
            return null;
    }

    public Object postReview(String uid, String reviewerId, RateReview rateReview) {

        if (rateReview.getReviewedId().equals(reviewerId)) {
            return "You can review to your own profile";
        } else if (rateReview.getReview() == null && rateReview.getRating() == null) {
            return "Data for review is not entered";
        } else if (!isValidUser(uid, reviewerId)) {
            return "Reviewer not found";
        } else if (userRepository.findUser(rateReview.getReviewedId()) == null) {
            return "User not found to review";
        } else {
            rateReview.setReviewerId(reviewerId);
            RateReview nRateReview = rateReviewRepository.insertReview(rateReview);
            // post rating to redis
            postNewRating(rateReview.getReviewedId(), rateReview.getRating());
            setDoReviewerCount(reviewerId);
            return nRateReview;
        }
    }

    public Object editReview(
            String rateReviewId, String reviewerId,
            Integer rating, String review) {

        if (review == null && rating == null) {
            return "One field is Necessary in Review or Rating";
        }

        RateReview pRateReview = rateReviewRepository.findReview(reviewerId, rateReviewId);
        if (pRateReview == null) {
            return "No rate-review exist for this user to edit";
        } else {
            if (review != null && !review.equals(pRateReview.getReview())) {
                pRateReview.setReview(review);
            }
            if (rating != null && !rating.equals(pRateReview.getRating())) {
                pRateReview.setRating(rating);
            }

            RateReview eRateReview = rateReviewRepository.saveReview(pRateReview);
            editRating(pRateReview.getReviewedId(), pRateReview.getRating(), rating);
            return eRateReview;
        }

    }

}
