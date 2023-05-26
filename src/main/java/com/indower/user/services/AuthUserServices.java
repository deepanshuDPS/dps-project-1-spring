package com.indower.user.services;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.annotations.Nullable;
import com.indower.models.responseModels.ConfidenceData;
import com.indower.models.responseModels.Data;
import com.indower.models.responseModels.ImagePrediction;
import com.indower.services.RedisMongoService;
import com.indower.user.models.User;
import com.indower.user.models.UserData;
import com.indower.user.models.documentModels.UserDoc;
import com.indower.utils.AppConstants;
import com.indower.utils.EnvironmentSetup;

// write all bussiness logic here to retrieve user
@Service
public class AuthUserServices extends RedisMongoService {

    @Value("${spring.s3.bucketname}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private EnvironmentSetup setup;

    private String getFolderName() {
        return setup.isProd() ? "/" : "-dev/";
    }

    public UserData getUser(String uid, String userId) {
        UserData fetchedUser = null;
        try {
            UserDoc userDoc = userRepository.findUser(uid, userId);
            if (userDoc != null && userDoc.get_id() != null) {
                fetchedUser = new UserData();
                BeanUtils.copyProperties(userDoc, fetchedUser);
                fetchedUser.setAnTextCount(getCountAns(userId));
                fetchedUser.setReviewsAvg(getReviewsAvg(userId));
                fetchedUser.setReviewsAsDoer(getReviewerDid(userId));
                fetchedUser.setAnTextAsDoer(getAnsDid(userId));
                return fetchedUser;
            }
            return null;
        } catch (Exception e) {
            // if some exception happens either it return copied value or it returns null
            return fetchedUser;
        }
    }

    public UserDoc checkEmailUser(String email, String uid) {
        UserDoc uidUser = userRepository.checkAuthUser(uid);
        // it means uid of user not exist in documents
        if (uidUser == null) {
            return checkByEmail(email, uid, "email");
        }
        return uidUser;
    }

    private UserDoc checkByEmail(String email, String uid, String signedType) {
        UserDoc emailUser = userRepository.checkUser(email);
        // user not exist in document - create new user - set onBoarded false
        if (emailUser == null) {
            ArrayList<String> oAuthIDs = new ArrayList<>();
            ArrayList<String> signedTypes = new ArrayList<>();
            UserDoc newUser = new UserDoc();
            newUser.setEmail(email);
            oAuthIDs.add(uid);
            signedTypes.add(signedType);
            newUser.setoAuthIDs(oAuthIDs);
            newUser.setSignedTypes(signedTypes);
            newUser.setOnBoarded(false);
            return userRepository.insertUser(newUser);
        } else {
            // update uid array to particular _id
            if (emailUser.gSecretAuthIds() != null &&
                    !emailUser.gSecretAuthIds().contains(uid)) {
                emailUser.gSecretAuthIds().add(uid);
            } else {
                ArrayList<String> oAuthIDs = new ArrayList<>();
                oAuthIDs.add(uid);
                emailUser.setoAuthIDs(oAuthIDs);
            }
            if (emailUser.gSecretSignedTypes() != null &&
                    !emailUser.gSecretSignedTypes().contains(signedType)) {
                emailUser.gSecretAuthIds().add(signedType);
            } else {
                ArrayList<String> signedTypes = new ArrayList<>();
                signedTypes.add(signedType);
                emailUser.setSignedTypes(signedTypes);
            }
            if (emailUser.isAnonymous()) {
                emailUser.setAnonymous(false);
                emailUser.setOnBoarded(true);
            }
            userRepository.saveUser(emailUser);
            return emailUser;
        }
    }

    public UserDoc checkGoogleUser(String email, String uid) {
        UserDoc uidUser = userRepository.checkAuthUser(uid);
        // it means uid of user not exist in documents
        if (uidUser == null) {
            return checkByEmail(email, uid, "gmail");
        }

        return uidUser;
    }

    public UserDoc checkForFbUser(String email, String uid) {
        UserDoc uidUser = userRepository.checkAuthUser(uid);
        // it means uid of user not exist in documents
        if (uidUser == null) {
            return checkByEmail(email, uid, "fb");
        }
        return uidUser;
    }

    public UserDoc checkForMsUser(String email, String uid) {
        UserDoc uidUser = userRepository.checkAuthUser(uid);
        // it means uid of user not exist in documents
        if (uidUser == null) {
            return checkByEmail(email, uid, "ms");
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
            setProfessionToRedis(userId, user.getAccountType());
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
            setProfessionToRedis(userId, user.getAccountType());
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
        UserDoc uidUser = userRepository.checkAuthUserForReview(uid);
        // it means uid of user not exist in documents
        if (uidUser == null) {
            return checkRevEmailUser(email, uid, "gmail");
        }

        return uidUser;
    }

    private UserDoc checkRevEmailUser(String email, String uid, String signedType) {
        UserDoc emailUser = userRepository.checkUser(email);
        // user not exist in document - create new user - set onBoarded false
        if (emailUser == null) {
            ArrayList<String> oAuthIDs = new ArrayList<>();
            ArrayList<String> signedTypes = new ArrayList<>();
            UserDoc newUser = new UserDoc();
            newUser.setEmail(email);
            oAuthIDs.add(uid);
            signedTypes.add(signedType);
            newUser.setoAuthIDs(oAuthIDs);
            newUser.setName(email.split("@")[0]);
            newUser.setAccountType(0); // reviewer
            newUser.setOnBoarded(true);
            return userRepository.insertUser(newUser);
        } else {
            // update uid array to particular _id
            if (emailUser.gSecretAuthIds() != null &&
                    !emailUser.gSecretAuthIds().contains(uid)) {
                emailUser.gSecretAuthIds().add(uid);
            } else {
                ArrayList<String> oAuthIDs = new ArrayList<>();
                oAuthIDs.add(uid);
                emailUser.setoAuthIDs(oAuthIDs);
            }
            if (emailUser.gSecretSignedTypes() != null &&
                    !emailUser.gSecretSignedTypes().contains(signedType)) {
                emailUser.gSecretAuthIds().add(signedType);
            } else {
                ArrayList<String> signedTypes = new ArrayList<>();
                signedTypes.add(signedType);
                emailUser.setSignedTypes(signedTypes);
            }
            userRepository.saveUser(emailUser);
            return emailUser;
        }
    }

    @Nullable
    public UserDoc reviewerFromFacebook(String uid, String email) {
        UserDoc uidUser = userRepository.checkAuthUserForReview(uid);
        // it means uid of user not exist in documents
        if (uidUser == null) {
            return checkRevEmailUser(email, uid, "fb");
        }
        return uidUser;
    }

    /*
     * if it's returns user then boarded with true
     * null-> not found,
     */
    // @Nullable
    // public UserDoc reviewerFromEmail(String uid, String email) {
    // UserDoc uidUser = userRepository.checkAuthUserForReview(uid);
    // // it means uid of user not exist in documents
    // if (uidUser == null) {
    // return checkRevEmailUser(email, uid, "email");
    // }
    // return uidUser;
    // }

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
                ObjectMapper mapper = new ObjectMapper();
                for (ConfidenceData data : (mapper.convertValue(prediction.getData().get(0), Data.class)).getConfidences()) {
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
            String userFilePath = "profile" + getFolderName() + userId + ".jpeg";
            if (isSafeToUse) {
                s3Client.putObject(new PutObjectRequest(bucketName, userFilePath, inputStream, metadata));
            } else {
                String nsFilePath = "ns-files" + getFolderName() + userId + ".jpeg";
                s3Client.putObject(new PutObjectRequest(bucketName, nsFilePath, inputStream, metadata));
                // set blurry image
                byte[] blurryImageData = java.util.Base64.getDecoder().decode(AppConstants.BLURRY_IMAGE_BASE64_STRING);
                ObjectMetadata blurryMetaData = new ObjectMetadata();
                blurryMetaData.setContentType("image/jpeg");
                blurryMetaData.setContentLength(blurryImageData.length);
                ByteArrayInputStream blurryInputStream = new ByteArrayInputStream(blurryImageData);
                s3Client.putObject(new PutObjectRequest(bucketName, userFilePath, blurryInputStream, blurryMetaData));
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
