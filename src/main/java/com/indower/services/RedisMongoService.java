package com.indower.services;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.redis.core.RedisTemplate;

import com.indower.ans.repository.ANSRepository;
import com.indower.models.AverageResult;
import com.indower.rateReviews.repository.RateReviewRepository;
import com.indower.user.models.documentModels.UserDoc;
import com.indower.user.repository.UserRepository;
import com.indower.utils.AppConstants;

public class RedisMongoService {

    @Autowired
    protected MongoTemplate mongoTemplate;

    // inject the actual template
    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    // one day expiry
    private Duration oneDayExpiry = Duration.ofDays(1);

    // one day expiry
    private Duration oneWeekExpiry = Duration.ofDays(7);

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ANSRepository ansRepository;

    @Autowired
    protected RateReviewRepository rateReviewRepository;

    protected Boolean isValidUser(String uid, String userId) {
        return userRepository.findUser(uid, userId) != null;
    }

    protected Boolean isProfessional(String userId) {
        String redisKey = AppConstants.USER_TYPE + userId;
        Integer type = (Integer) redisTemplate.opsForValue().get(redisKey);
        if (type != null) {
            return type == 1;
        } else {
            UserDoc user = userRepository.findUser(userId);
            redisTemplate.opsForValue().set(redisKey, user.getAccountType(), oneWeekExpiry);
            return user.getAccountType() == 1;
        }
    }

    protected void setProfessionToRedis(String userId, Integer accountType) {
        String redisKey = AppConstants.USER_TYPE + userId;
        Integer type = (Integer) redisTemplate.opsForValue().get(redisKey);
        if (type != null && type == accountType)
            return;
        redisTemplate.opsForValue().set(redisKey, accountType, oneWeekExpiry);
    }

    protected AverageResult getRatingAvg(String userId) {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("reviewedId").is(userId)),
                Aggregation.match(Criteria.where("rating").ne(null)),
                Aggregation.match(Criteria.where("rating").ne(0)),
                Aggregation.group("rating")
                        .avg("rating").as("average")
                        .count().as("totalCount"));

        List<AverageResult> results = mongoTemplate.aggregate(aggregation, "rate-review",
                AverageResult.class).getMappedResults();

        if (results.isEmpty()) {
            return new AverageResult();
        }
        return results.get(0);
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
            int newCount = totalCount + 1;
            avgValue = ((avgValue * totalCount) + rating) / newCount;
            setNewRatingAvg(userId, avgValue, newCount);
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
                int newCount = totalCount - 1;
                avgValue = ((avgValue * totalCount) - prevRating) / newCount;
                setNewRatingAvg(userId, avgValue, newCount);
            } else {
                avgValue = ((avgValue * totalCount) + rating - prevRating) / totalCount;
                setNewRatingAvg(userId, avgValue, totalCount);
            }
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

    // not using because will show day wise
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
