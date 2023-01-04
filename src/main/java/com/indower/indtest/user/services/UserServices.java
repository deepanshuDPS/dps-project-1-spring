package com.indower.indtest.user.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.firebase.database.annotations.Nullable;
import com.indower.indtest.user.models.User;
import com.indower.indtest.user.repository.UserRepository;

// write all bussiness logic here to retrieve user
@Service
public class UserServices {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public User getUser(String uid, String _id) {
        return userRepository.findUser(uid, _id);
    }

    public User checkEmailUser(String email, String uid) {
        User uidUser = userRepository.checkAuthUser(uid);
        // it means uid of user not exist in documents
        if (uidUser == null) {
            User emailUser = userRepository.checkUser(email);
            // user not exist in document - create new user - set onBoarded false
            if (emailUser == null) {
                ArrayList<String> oAuthIDs = new ArrayList<>();
                User newUser = new User();
                newUser.setEmail(email);
                oAuthIDs.add(uid);
                newUser.setoAuthIDs(oAuthIDs);
                newUser.setOnBoarded(false);
                return userRepository.insertUser(newUser);
            } else {
                // update uid array to particular _id
                if (emailUser.getoAuthIDs() != null) {
                    emailUser.getoAuthIDs().add(uid);
                } else {
                    ArrayList<String> oAuthIDs = new ArrayList<>();
                    oAuthIDs.add(uid);
                    emailUser.setoAuthIDs(oAuthIDs);
                }
                userRepository.saveUser(emailUser);
                return emailUser;
            }
        }

        return uidUser;
    }

    public User checkGoogleUser(String email, String uid) {
        User uidUser = userRepository.checkAuthUser(uid);
        // it means uid of user not exist in documents
        if (uidUser == null) {
            User emailUser = userRepository.checkUser(email);
            // user not exist in document - create new user - set onBoarded false
            if (emailUser == null) {
                ArrayList<String> oAuthIDs = new ArrayList<>();
                User newUser = new User();
                newUser.setEmail(email);
                oAuthIDs.add(uid);
                newUser.setoAuthIDs(oAuthIDs);
                newUser.setOnBoarded(false);
                return userRepository.insertUser(newUser);
            } else {
                // update uid array to particular _id
                if (emailUser.getoAuthIDs() != null) {
                    emailUser.getoAuthIDs().add(uid);
                } else {
                    ArrayList<String> oAuthIDs = new ArrayList<>();
                    oAuthIDs.add(uid);
                    emailUser.setoAuthIDs(oAuthIDs);
                }
                userRepository.saveUser(emailUser);
                return emailUser;
            }
        }

        return uidUser;
    }

    /*
     * if it's 1 then user signed up
     * 0 means exist,
     * null-> not found,
     * 2 -> profession not defined
     */
    @Nullable
    public Integer signUpUser(String uid, String userId, User user) {
        User currentUser = userRepository.findUser(uid, userId);
        if (currentUser == null)
            return null;
        else if (currentUser.getAccountType() != null
                && currentUser.getOnBoarded()) {
            return 0;
        } else {
            if (user.getAccountType() != 0
                    && user.getProfession() == null
                    && user.getGender() == null) {
                // means it's not a reviewer
                return 2;
            }
            currentUser.setName(user.getName());
            currentUser.setImageUrl(user.getImageUrl());
            currentUser.setProfession(user.getProfession());
            currentUser.setGender(user.getGender());
            currentUser.setAccountType(user.getAccountType());
            currentUser.setDescription(user.getDescription());
            currentUser.setSocialLinks(user.getSocialLinks());
            currentUser.setOnBoarded(true);
            userRepository.saveUser(currentUser);
            return 1;
        }
    }

    /*
     * if it's 1 then user edited
     * 0 means user not boarded,
     * null-> not found,
     * 2 -> profession and gender not defined
     */
    @Nullable
    public Integer editUser(String uid, String userId, User user) {
        User currentUser = userRepository.findUser(uid, userId);
        if (currentUser == null)
            return null;
        else if (currentUser.getAccountType() == null
                || !currentUser.getOnBoarded()) {
            // user not boarded
            return 0;
        } else {
            if (user.getAccountType() != 0
                    && user.getProfession() == null
                    && user.getGender() == null) {
                // means it's not a reviewer
                return 2;
            }
            HashMap<String,Object> valuesToUpdate = new HashMap<>();
            valuesToUpdate.put("name", user.getName());
            valuesToUpdate.put("imageUrl", user.getImageUrl());

            if(!currentUser.getProfession().equals(user.getProfession())){
                valuesToUpdate.put("profession", user.getProfession());
            }
            if(!currentUser.getGender().equals(user.getGender())){
                valuesToUpdate.put("gender", user.getGender());
            }

            if(!currentUser.getAccountType().equals(user.getAccountType())){
                valuesToUpdate.put("accountType", user.getAccountType());
            }
            
            valuesToUpdate.put("description", user.getDescription());
            valuesToUpdate.put("socialLinks", user.getSocialLinks());
            updateDocument(userId, valuesToUpdate);
            return 1;
        }
    }

    public String storeFile(MultipartFile image) {
        return "";
    }

    public void editDescription(String description) {

    }

    public void updateDocument(String id, Map<String,Object> valuesToUpdate) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(id));
        Update update = new Update();
        for (Entry<String, Object> entry : valuesToUpdate.entrySet()){
            update.set(entry.getKey(),entry.getValue());
        }        
        update.set("updatedAt", new Date());
        mongoTemplate.update(User.class).matching(query).apply(update).first();
    }

}
