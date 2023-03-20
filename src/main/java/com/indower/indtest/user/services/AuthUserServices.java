package com.indower.indtest.user.services;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.annotations.Nullable;
import com.indower.indtest.ans.repository.ANSRepository;
import com.indower.indtest.models.AverageResult;
import com.indower.indtest.models.responseModels.ConfidenceData;
import com.indower.indtest.models.responseModels.ImagePrediction;
import com.indower.indtest.user.models.User;
import com.indower.indtest.user.models.UserData;
import com.indower.indtest.user.models.documentModels.UserDoc;
import com.indower.indtest.user.repository.UserRepository;
import com.indower.indtest.utils.AppConstants;

// write all bussiness logic here to retrieve user
@Service
public class AuthUserServices {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ANSRepository ansRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${spring.s3.bucketname}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    // inject the actual template
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private Duration userRedisCache = Duration.ofHours(12);

    // // inject the template as SetOperations
    // @Autowired
    // private SetOperations<String, Object> setOps;

    public UserData getUser(String uid, String userId) {
        UserData fetchedUser = new UserData();
        UserDoc userDoc = userRepository.findUser(uid, userId);
        BeanUtils.copyProperties(userDoc, fetchedUser);
        if (fetchedUser.get_id() != null) {
            fetchedUser.setAnTextCount(getCountAns(userId));
            fetchedUser.setReviewsAvg(getReviewsAvg(userId));
            return fetchedUser;
        }
        return null;
    }

    private Integer getCountAns(String userId) {
        String redisKey = AppConstants.COUNT_ANS + userId;
        Integer redisValue = (Integer) redisTemplate.opsForValue().get(redisKey);
        if (redisValue != null) {
            return redisValue;
        } else {
            Integer count = ansRepository.countOfAnText(userId);
            redisTemplate.opsForValue().set(redisKey, count, userRedisCache);
            return count;
        }
    }

    private Float getReviewsAvg(String userId) {
        String redisKey = AppConstants.AVG_RATING + userId;
        Float redisValue = (Float) redisTemplate.opsForValue().get(redisKey);
        if (redisValue != null) {
            return redisValue;
        } else {
            Float avgRating = getRatingAvg(userId);
            redisTemplate.opsForValue().set(redisKey, avgRating, userRedisCache);
            return avgRating;
        }
    }

    public UserDoc checkEmailUser(String email, String uid) {
        UserDoc uidUser = userRepository.checkAuthUser(uid);
        // it means uid of user not exist in documents
        if (uidUser == null) {
            UserDoc emailUser = userRepository.checkUser(email);
            // user not exist in document - create new user - set onBoarded false
            if (emailUser == null) {
                ArrayList<String> oAuthIDs = new ArrayList<>();
                UserDoc newUser = new UserDoc();
                newUser.setEmail(email);
                oAuthIDs.add(uid);
                newUser.setoAuthIDs(oAuthIDs);
                newUser.setOnBoarded(false);
                return userRepository.insertUser(newUser);
            } else {
                // update uid array to particular _id
                if (emailUser.gSecretAuthIds() != null) {
                    emailUser.gSecretAuthIds().add(uid);
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

    public UserDoc checkGoogleUser(String email, String uid) {
        UserDoc uidUser = userRepository.checkAuthUser(uid);
        // it means uid of user not exist in documents
        if (uidUser == null) {
            UserDoc emailUser = userRepository.checkUser(email);
            // user not exist in document - create new user - set onBoarded false
            if (emailUser == null) {
                ArrayList<String> oAuthIDs = new ArrayList<>();
                UserDoc newUser = new UserDoc();
                newUser.setEmail(email);
                oAuthIDs.add(uid);
                newUser.setoAuthIDs(oAuthIDs);
                newUser.setOnBoarded(false);
                return userRepository.insertUser(newUser);
            } else {
                // update uid array to particular _id
                if (emailUser.gSecretAuthIds() != null) {
                    emailUser.gSecretAuthIds().add(uid);
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


    public UserDoc checkForFbUser(String email, String uid) {
        UserDoc uidUser = userRepository.checkAuthUser(uid);
        // it means uid of user not exist in documents
        if (uidUser == null) {
            UserDoc emailUser = userRepository.checkUser(email);
            // user not exist in document - create new user - set onBoarded false
            if (emailUser == null) {
                ArrayList<String> oAuthIDs = new ArrayList<>();
                UserDoc newUser = new UserDoc();
                newUser.setEmail(email);
                oAuthIDs.add(uid);
                newUser.setoAuthIDs(oAuthIDs);
                newUser.setOnBoarded(false);
                return userRepository.insertUser(newUser);
            } else {
                // update uid array to particular _id
                if (emailUser.gSecretAuthIds() != null) {
                    emailUser.gSecretAuthIds().add(uid);
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
        UserDoc currentUser = userRepository.findUser(uid, userId);
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
            currentUser.setProfessionDesc(user.getProfessionDesc());
            currentUser.setOnBoarded(true);
            // System.out.println("lang_"+userId+" "+user.getLanguages());
            // setOps.pop("lang_"+userId);
            // for(String i:user.getLanguages()){
            // setOps.add("lang_"+userId,i);
            // }
            // System.out.println(setOps.members("lang_"+userId));
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
    public Integer editUser(String uid, String userId, UserDoc user) {
        UserDoc currentUser = userRepository.findUser(uid, userId);
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
            HashMap<String, Object> valuesToUpdate = new HashMap<>();

            if (checkCompulsoryField(currentUser.getName(), user.getName()))
                valuesToUpdate.put("name", user.getName());
            if (checkCompulsoryField(currentUser.getProfession(), user.getProfession()))
                valuesToUpdate.put("profession", user.getProfession());
            if (checkCompulsoryField(currentUser.getGender(), user.getGender()))
                valuesToUpdate.put("gender", user.getGender());
            if (checkCompulsoryField(currentUser.getAccountType(), user.getAccountType()))
                valuesToUpdate.put("accountType", user.getAccountType());
            if (checkCompulsoryField(currentUser.getProfessionDesc(), user.getProfessionDesc()))
                valuesToUpdate.put("professionDesc", user.getProfessionDesc());
            if (checkNonCompulsoryField(currentUser.getDescription(), user.getDescription()))
                valuesToUpdate.put("description", user.getDescription());
            if (checkNonCompulsoryField(currentUser.getSocialLinks(), user.getSocialLinks()))
                valuesToUpdate.put("socialLinks", user.getSocialLinks());

            updateDocument(userId, valuesToUpdate);
            return 1;
        }
    }

    /*
     * if it's returns user then boarded with true
     * null-> not found,
     */
    @Nullable
    public UserDoc reviewerFromGoogle(String uid, String email) {
        UserDoc uidUser = userRepository.checkAuthUser(uid);
        // it means uid of user not exist in documents
        if (uidUser == null) {
            UserDoc emailUser = userRepository.checkUser(email);
            // user not exist in document - create new user - set onBoarded false
            if (emailUser == null) {
                ArrayList<String> oAuthIDs = new ArrayList<>();
                UserDoc newUser = new UserDoc();
                newUser.setEmail(email);
                oAuthIDs.add(uid);
                newUser.setoAuthIDs(oAuthIDs);
                newUser.setName(email.split("@")[0]);
                newUser.setImageUrl("https://cdn.pixabay.com/photo/2017/07/31/23/45/minion-2562071__340.png");
                newUser.setAccountType(0); // reviewer
                newUser.setOnBoarded(true);
                return userRepository.insertUser(newUser);
            } else {
                // update uid array to particular _id
                if (emailUser.gSecretAuthIds() != null) {
                    emailUser.gSecretAuthIds().add(uid);
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
     * if it's returns user then boarded with true
     * null-> not found,
     */
    @Nullable
    public UserDoc reviewerFromEmail(String uid, String email) {
        UserDoc uidUser = userRepository.checkAuthUser(uid);
        // it means uid of user not exist in documents
        if (uidUser == null) {
            UserDoc emailUser = userRepository.checkUser(email);
            // user not exist in document - create new user - set onBoarded false
            if (emailUser == null) {
                ArrayList<String> oAuthIDs = new ArrayList<>();
                UserDoc newUser = new UserDoc();
                newUser.setEmail(email);
                oAuthIDs.add(uid);
                newUser.setoAuthIDs(oAuthIDs);
                newUser.setName(email.split("@")[0]);
                newUser.setImageUrl("https://cdn.pixabay.com/photo/2017/07/31/23/45/minion-2562071__340.png");
                newUser.setAccountType(0); // reviewer
                newUser.setOnBoarded(true);
                return userRepository.insertUser(newUser);
            } else {
                // update uid array to particular _id
                if (emailUser.gSecretAuthIds() != null) {
                    emailUser.gSecretAuthIds().add(uid);
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

    public String storeFile(MultipartFile image) {
        return "";
    }

    public void editDescription(String description) {

    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
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

    public Float getRatingAvg(String userId) {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("reviewedId").is(userId)),
                Aggregation.match(Criteria.where("rating").ne(null)),
                Aggregation.group("rating").avg("rating").as("average"));

        AggregationResults<AverageResult> results = mongoTemplate.aggregate(aggregation, "rate-review",
                AverageResult.class);

        if (results.getUniqueMappedResult() != null) {
            return results.getUniqueMappedResult().getAverage();
        }
        return 0f;
    }

    private ImagePrediction getPredictionForImage(String base64Image) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = AppConstants.IMAGE_PREDICTION_URL;
            ArrayList<String> imageToCheck = new ArrayList<>();
            imageToCheck.add(base64Image);
            HashMap<String, Object> imageData = new HashMap<>();
            imageData.put("data", imageToCheck);
            // own based on the API's requirements
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody;
            requestBody = objectMapper.writeValueAsString(imageData);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<ImagePrediction> response = restTemplate.postForEntity(apiUrl, requestEntity,
                    ImagePrediction.class);

            return response.getBody();
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public boolean uploadfile(String userId, String base64Image) {
        try {
            // slitting base64 type context from base64 data
            ImagePrediction prediction = getPredictionForImage(base64Image);
            boolean isSafeToUse = true;
            if (prediction != null) {
                for (ConfidenceData data : prediction.getData().get(0).getConfidences()) {
                    if (data.getLabel().equals(ConfidenceData.NSFW) && data.getConfidence() * 100 > 66 ||
                            data.getLabel().equals(ConfidenceData.CAR) && data.getConfidence() * 100 > 66) {
                        isSafeToUse = false;
                    }
                }
            } else {
                return false;
            }

            byte[] imageData = java.util.Base64.getDecoder().decode(base64Image.split("base64,")[1]);

            // Create an input stream from the image data
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);

            // Create object metadata with content type and length
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");
            metadata.setContentLength(imageData.length);
            String userFilePath = "profile/" + userId + ".jpeg";
            if (isSafeToUse) {
                s3Client.putObject(new PutObjectRequest(bucketName, userFilePath, inputStream, metadata));
            } else {
                String nsFilePath = "ns-files/" + userId + ".jpeg";
                s3Client.putObject(new PutObjectRequest(bucketName, nsFilePath, inputStream, metadata));
                // set default profile base64 to match show on web to userfile path
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // both null -> no change
    // db value null and sent value not null -> update
    // db value not null and sent value not null but not equal -> update
    // db value not null and sent value not null but equal -> no change
    private boolean checkCompulsoryField(Object dbValue, Object sentValue) {
        if (dbValue == null && sentValue == null)
            return false;
        else
            return (dbValue == null && sentValue != null) ||
                    (dbValue != null && sentValue != null
                            && !dbValue.equals(sentValue));
    }

    // both null -> no change
    // db value null and sent value not null -> update
    // db value not null and sent value null -> only for non mandetory fields
    // db value not null and sent value not null but not equal -> update
    // db value not null and sent value not null but equal -> no change
    private boolean checkNonCompulsoryField(Object dbValue, Object sentValue) {
        if (dbValue == null && sentValue == null)
            return false;
        else
            return (dbValue == null && sentValue != null) ||
                    (dbValue != null && sentValue != null
                            && !dbValue.equals(sentValue))
                    || (dbValue != null && sentValue == null);

    }

}
