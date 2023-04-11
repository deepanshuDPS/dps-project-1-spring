package com.indower.indtest.user.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.indower.indtest.rateReviews.models.docModels.RateReview;
import com.indower.indtest.services.RedisMongoService;
import com.indower.indtest.user.models.UserData;
import com.indower.indtest.user.models.documentModels.UserDoc;

// write all bussiness logic here to retrieve user
@Service
public class UserServices extends RedisMongoService {

    public UserData getUser(String userId, String loginedUserId) {
        UserData fetchedUser = new UserData();
        UserDoc userDoc = userRepository.findUser(userId);
        BeanUtils.copyProperties(userDoc, fetchedUser);
        if (fetchedUser.get_id() != null) {
            fetchedUser.setAnTextCount(getCountAns(userId));
            fetchedUser.setReviewsAvg(getReviewsAvg(userId));
            // to show user review
            if (loginedUserId != null) {
                List<RateReview> rateReviews = rateReviewRepository.findReviewForUser(loginedUserId, userId);
                if (rateReviews != null && rateReviews.size() > 0)
                    fetchedUser.setYourRating(rateReviews.get(0));
            }
            return fetchedUser;
        }
        return null;
    }

    public Map<String, Object> makeAnonymousUser(String email) {
        UserDoc emailUser = userRepository.checkUser(email);
        String uid = null;
        if (emailUser == null) {
            StringBuilder uidBuilder = new StringBuilder();
            for (String i : email.split("@")) {
                uidBuilder.append("*");
                uidBuilder.append(i);
                uidBuilder.append("*");
            }
            String createGuestUid = uidBuilder.toString().replaceAll("[*]+", "*");
            ArrayList<String> oAuthIDs = new ArrayList<>();
            UserDoc newUser = new UserDoc();
            newUser.setEmail(email);
            oAuthIDs.add(createGuestUid);
            newUser.setoAuthIDs(oAuthIDs);
            newUser.setName(email.split("@")[0]);
            newUser.setImageUrl("https://cdn.pixabay.com/photo/2017/07/31/23/45/minion-2562071__340.png");
            newUser.setAccountType(0); // reviewer
            newUser.setOnBoarded(false);
            newUser.setAnonymous(true);
            emailUser = newUser;
            userRepository.insertUser(emailUser);
        } else {
            uid = emailUser.gSecretAuthIds().get(0);
        }

        try {
            Map<String, Object> responseData = new HashMap<String, Object>();
            responseData.put("userId", emailUser.get_id());
            responseData.put("name", emailUser.getName());
            responseData.put("guest_token", FirebaseAuth.getInstance()
                    .createCustomToken(uid));
            return responseData;
        } catch (FirebaseAuthException e) {
            return null;
        }

    }

    public void updateDocument(String id, Map<String, Object> valuesToUpdate) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(id));
        Update update = new Update();
        for (Entry<String, Object> entry : valuesToUpdate.entrySet()) {
            update.set(entry.getKey(), entry.getValue());
        }
        update.set("updatedAt", new Date());
        mongoTemplate.update(UserDoc.class).matching(query).apply(update).first();
    }

}
