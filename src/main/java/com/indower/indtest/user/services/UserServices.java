package com.indower.indtest.user.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
            if (loginedUserId != null)
                fetchedUser.setYourRating(rateReviewRepository.findReviewForUser(loginedUserId, userId));
            return fetchedUser;
        }
        return null;
    }

    public String makeAnonymousUser(String email) {
        UserDoc emailUser = userRepository.checkUser(email);
        StringBuilder uidBuilder = new StringBuilder();
        for (String i : email.split("@")) {
            uidBuilder.append("*");
            uidBuilder.append(i);
            uidBuilder.append("*");
        }
        String uid = uidBuilder.toString().replaceAll("[*]+", "*");
        if (emailUser == null) {
            ArrayList<String> oAuthIDs = new ArrayList<>();
            UserDoc newUser = new UserDoc();
            newUser.setEmail(email);
            oAuthIDs.add(uid);
            newUser.setoAuthIDs(oAuthIDs);
            newUser.setName(email.split("@")[0]);
            newUser.setImageUrl("https://cdn.pixabay.com/photo/2017/07/31/23/45/minion-2562071__340.png");
            newUser.setAccountType(0); // reviewer
            newUser.setOnBoarded(false);
            newUser.setAnonymous(true);
            emailUser = newUser;
            userRepository.insertUser(emailUser);
        } else if (false) {
            return null;
        }

        try {
            Map<String, Object> additionalParams = new HashMap<String, Object>();
            additionalParams.put("guestAccount", true);
            additionalParams.put("email", email);
            String customToken = FirebaseAuth.getInstance()
                    .createCustomToken(uid, additionalParams);
            return customToken;
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
