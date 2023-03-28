package com.indower.indtest.services;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.redis.core.RedisTemplate;

import com.indower.indtest.ans.repository.ANSRepository;
import com.indower.indtest.models.AverageResult;
import com.indower.indtest.rateReviews.repository.RateReviewRepository;
import com.indower.indtest.user.repository.UserRepository;
import com.indower.indtest.utils.AppConstants;

public class RedisMongoService {

    @Autowired
    protected MongoTemplate mongoTemplate;

    // inject the actual template
    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    // one day expiry
    private Duration oneDayExpiry = Duration.ofDays(1);

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ANSRepository ansRepository;

    @Autowired
    protected RateReviewRepository rateReviewRepository;

    protected AverageResult getRatingAvg(String userId) {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("reviewedId").is(userId)),
                Aggregation.match(Criteria.where("rating").ne(null)),
                Aggregation.match(Criteria.where("rating").ne(0)),
                Aggregation.group("rating")
                        .avg("rating").as("average")
                        .count().as("totalCount"));

        AggregationResults<AverageResult> results = mongoTemplate.aggregate(aggregation, "rate-review",
                AverageResult.class);

        if (results.getUniqueMappedResult() != null) {
            return results.getUniqueMappedResult();
        }
        return new AverageResult();
    }

    private Float rateRestoreFromDb(String userId) {
        AverageResult result = getRatingAvg(userId);
        setNewRatingAvg(userId, result.getAverage(), result.getTotalCount());
        return result.getAverage();
    }

    private Integer ansDidRestoreFromDb(String doerId) {
        String redisKey = AppConstants.ANS_DID + doerId;
        Integer count = ansRepository.countOfDoer(doerId);
        redisTemplate.opsForValue().set(redisKey, count, oneDayExpiry);
        return count;
    }

    private Integer reviewerDidRestoreFromDb(String reviewerId) {
        String redisKey = AppConstants.RATING_DID + reviewerId;
        Integer count = rateReviewRepository.findReviewerCount(reviewerId);
        redisTemplate.opsForValue().set(redisKey, count, oneDayExpiry);
        return count;
    }

    private Integer ansRestoreFromDb(String userId) {
        String redisKey = AppConstants.COUNT_ANS + userId;
        Integer count = ansRepository.countOfAnText(userId);
        redisTemplate.opsForValue().set(redisKey, count, oneDayExpiry);
        return count;
    }

    private void setNewRatingAvg(String userId, Float avgRating, Integer totalCount) {
        String avgRateKey = AppConstants.AVG_RATING + userId;
        String totalCountKey = AppConstants.TOTAL_RATING + userId;
        redisTemplate.opsForValue().set(avgRateKey, avgRating, oneDayExpiry);
        redisTemplate.opsForValue().set(totalCountKey, totalCount, oneDayExpiry);
    }

    protected void postNewRating(String userId, Integer rating) {
        if (rating == null)
            return;
        String avgRateKey = AppConstants.AVG_RATING + userId;
        String totalCountKey = AppConstants.TOTAL_RATING + userId;
        Float avgValue = (Float) redisTemplate.opsForValue().get(avgRateKey);
        if (avgValue != null) {
            Integer totalCount = (Integer) redisTemplate.opsForValue().get(totalCountKey);
            totalCount += 1;
            avgValue = (avgValue + rating) / totalCount;
            setNewRatingAvg(userId, avgValue, totalCount);
        } else {
            rateRestoreFromDb(userId);
        }

    }

    protected void editRating(String userId, Integer prevRating, Integer rating) {
        if (prevRating == rating)
            return;
        String avgRateKey = AppConstants.AVG_RATING + userId;
        String totalCountKey = AppConstants.TOTAL_RATING + userId;
        Float avgValue = (Float) redisTemplate.opsForValue().get(avgRateKey);
        if (avgValue != null) {
            Integer totalCount = (Integer) redisTemplate.opsForValue().get(totalCountKey);
            if ((rating != null && rating == 0 && prevRating > 1) || rating == null) {
                totalCount -= 1;
                avgValue = (avgValue - prevRating) / totalCount;
            } else {
                avgValue = (avgValue + rating - prevRating) / totalCount;
            }
            setNewRatingAvg(userId, avgValue, totalCount);
        } else {
            rateRestoreFromDb(userId);
        }

    }

    protected Float getReviewsAvg(String userId) {
        String avgRateKey = AppConstants.AVG_RATING + userId;
        Float redisValue = (Float) redisTemplate.opsForValue().get(avgRateKey);
        if (redisValue != null) {
            return redisValue;
        } else {
            return rateRestoreFromDb(userId);
        }
    }

    protected Integer getAnsDid(String doerId) {
        String ansDidKey = AppConstants.ANS_DID + doerId;
        Integer redisValue = (Integer) redisTemplate.opsForValue().get(ansDidKey);
        if (redisValue != null) {
            return redisValue;
        } else {
            return ansDidRestoreFromDb(doerId);
        }
    }

    protected Integer getReviewerDid(String reviewerId) {
        String revDidKey = AppConstants.RATING_DID + reviewerId;
        Integer redisValue = (Integer) redisTemplate.opsForValue().get(revDidKey);
        if (redisValue != null) {
            return redisValue;
        } else {
            return reviewerDidRestoreFromDb(reviewerId);
        }
    }

    protected Integer getCountAns(String userId) {
        String redisKey = AppConstants.COUNT_ANS + userId;
        Integer redisValue = (Integer) redisTemplate.opsForValue().get(redisKey);
        if (redisValue != null) {
            return redisValue;
        } else {
            return ansRestoreFromDb(userId);
        }
    }

    protected void setCountAns(String userId) {
        String redisKey = AppConstants.COUNT_ANS + userId;
        Integer redisValue = (Integer) redisTemplate.opsForValue().get(redisKey);
        if (redisValue != null) {
            redisTemplate.opsForValue().set(redisKey, redisValue + 1, oneDayExpiry);
        } else {
            ansRestoreFromDb(userId);
        }
    }

    protected void setDoAnsCount(String doerId) {
        String redisKey = AppConstants.ANS_DID + doerId;
        Integer redisValue = (Integer) redisTemplate.opsForValue().get(redisKey);
        if (redisValue != null) {
            redisTemplate.opsForValue().set(redisKey, redisValue + 1, oneDayExpiry);
        } else {
            ansDidRestoreFromDb(doerId);
        }
    }

    protected void setDoReviewerCount(String reviewerId) {
        String redisKey = AppConstants.RATING_DID + reviewerId;
        Integer redisValue = (Integer) redisTemplate.opsForValue().get(redisKey);
        if (redisValue != null) {
            redisTemplate.opsForValue().set(redisKey, redisValue + 1, oneDayExpiry);
        } else {
            reviewerDidRestoreFromDb(reviewerId);
        }
    }

}
