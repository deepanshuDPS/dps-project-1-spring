package com.indower.rateReviews.services;

import java.util.List;

import javax.annotation.Nullable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.indower.rateReviews.models.docModels.RateReview;
import com.indower.services.RedisMongoService;
import com.indower.utils.AppConstants;

@Service
public class RateReviewServices extends RedisMongoService {

    // if has data then page else null
    @Nullable
    public Page<RateReview> getReviews(String userId, Integer page) {
        if (!isProfessional(userId))
            return null;
        Sort sort = Sort.by(Sort.Order.desc("updatedAt"));
        Pageable paging = PageRequest.of(page, AppConstants.PAGE_SIZE, sort);
        Page<RateReview> reviews = rateReviewRepository.findByReviewedId(userId, paging);
        if (reviews.getContent() != null && !reviews.getContent().isEmpty())
            return reviews;
        else
            return null;
    }

    public Object postReview(String uid, String reviewerId, RateReview rateReview) {
        if (!isProfessional(rateReview.getReviewedId()))
            return "This account is not a professional account or Account not exist.";
        else if (rateReview.getReviewedId().equals(reviewerId))
            return "You can review to your own profile";
        else if (rateReview.getReview() == null && rateReview.getRating() == null)
            return "Data for review is not entered";
        else if (!isProfessional(rateReview.getReviewedId()))
            return "This account is not a professional account or Account not exist.";
        else if (!isValidUser(uid, reviewerId))
            return "Reviewer not found";
        else {
            List<RateReview> rateReviews = rateReviewRepository.findReviewForUser(reviewerId,
                    rateReview.getReviewedId());
            // update previous if exist
            if (rateReviews != null && rateReviews.size() > 0) {
                RateReview pRateReview = rateReviews.get(0);
                pRateReview.setRating(rateReview.getRating());
                pRateReview.setReview(rateReview.getReview());
                pRateReview = rateReviewRepository.saveReview(pRateReview);
                editRating(pRateReview.getReviewedId(), pRateReview.getRating(), rateReview.getRating());
                return pRateReview;
            } else {
                rateReview.setReviewerId(reviewerId);
                RateReview nRateReview = rateReviewRepository.insertReview(rateReview);
                // post rating to redis
                postNewRating(rateReview.getReviewedId(), rateReview.getRating());
                setDoReviewerCount(reviewerId);
                return nRateReview;
            }

        }
    }

    public Object editReview(
            String rateReviewId, String reviewerId,
            Integer rating, String review, String name) {

        if (review == null && rating == null) {
            return "One field is Necessary in Review or Rating";
        }

        RateReview pRateReview = rateReviewRepository.findReview(reviewerId, rateReviewId);
        if (pRateReview == null) {
            return "No rate-review exist for this user to edit";
        } else {
            Integer pRating = pRateReview.getRating();
            if (review != null && !review.equals(pRateReview.getReview())) {
                pRateReview.setReview(review);
            }
            if (rating != null && !rating.equals(pRateReview.getRating())) {
                pRateReview.setRating(rating);
            }
            if (name != null) {
                pRateReview.setRevName(name);
            }

            RateReview eRateReview = rateReviewRepository.saveReview(pRateReview);
            editRating(pRateReview.getReviewedId(), pRating, rating);
            return eRateReview;
        }

    }

}
