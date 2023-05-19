package com.indower.ans.services;

import java.security.Key;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.annotation.Nullable;
import javax.crypto.Cipher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Value;
import com.indower.ans.models.docModels.ANS;
import com.indower.models.responseModels.TextPrediction;
import com.indower.services.RedisMongoService;
import com.indower.user.models.documentModels.UserDoc;
import com.indower.utils.AppConstants;
import com.indower.utils.EnvironmentSetup;

import static javax.crypto.Cipher.ENCRYPT_MODE;
import javax.crypto.spec.SecretKeySpec;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class ANSServices extends RedisMongoService {

    @Autowired
    private EnvironmentSetup setup;

    // if has data then page else null
    @Nullable
    public Page<ANS> getAnsTexts(String uid, String userId, Integer page) {

        Pageable paging = PageRequest.of(page, AppConstants.PAGE_SIZE);
        // restrict for ans very private data
        if (!isValidUser(uid, userId))
            return null;

        Page<ANS> ans = ansRepository.findByToWhomId(userId, paging);
        if (ans.getContent() != null && !ans.getContent().isEmpty())
            return ans;
        else
            return null;
    }

    private boolean isAbusiveText(String ansText) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = AppConstants.TEXT_PREDICTION_URL;
            ArrayList<String> textToCheck = new ArrayList<>();
            textToCheck.add(ansText);
            HashMap<String, Object> imageData = new HashMap<>();
            imageData.put("data", textToCheck);
            // own based on the API's requirements
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody;
            requestBody = objectMapper.writeValueAsString(imageData);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<TextPrediction> response = restTemplate.postForEntity(apiUrl, requestEntity,
                    TextPrediction.class);

            return response.getBody().getData().get(0).toLowerCase().contains("not");
        } catch (JsonProcessingException e) {
            return true;
        }
    }

    public Object postAnsText(String uid, String doerId, ANS ans) {
        if (ans.getToWhomId().equals(doerId)) {
            return "You can QR yourself";
        } else if (ans.getText() == null || (ans.getText() != null && ans.getText().isEmpty())) {
            return "Please enter some test to QR";
        } else if (!isValidUser(uid, doerId)) {
            return "No data found for Reviewer";
        }
        UserDoc user = userRepository.findUser(ans.getToWhomId());
        if (user == null) {
            return "No user found to QR";
        } else if (ansRepository.countOfDoer(doerId, ans.getToWhomId()) == (long) user.userAnsLimit()) {
            return "QR limit Exceeded for you";
        } else {
            ans.setDoerId(doerId);
            Boolean isNotAbusive = isAbusiveText(ans.getText());
            ans.setPredictionStatus(isNotAbusive ? 1 : 0);
            ans.setIsShow(ans.getPredictionStatus() == 1);
            Date currentDate = new Date();
            String eKey = setup.geteKey();
            String eCountry = setup.geteCountry();
            // always insert with current global variables

            if (eKey != null && eCountry != null) {
                String passString = AppConstants.makePasswordToEncodeDecode(currentDate, eKey, eCountry);
                String text = ans.getText();
                String encryptedText = encrypt(text, passString);
                // if encryption happens set values
                System.out.println(passString);
                if (encryptedText != null) {
                    ans.setEncryptionKey(eKey);
                    ans.setEncryptionCountry(eCountry);
                    ans.setText(encryptedText);
                }
            }
            ANS nAns = ansRepository.insertText(ans, currentDate);
            setCountAns(ans.getToWhomId());
            setDoAnsCount(ans.getDoerId());
            return nAns;
        }
    }

    public Object editANSText(
            String uid,
            String ansId,
            String userId,
            Boolean isAbusive,
            Boolean isHelpful,
            Boolean isShow) {

        if (!isValidUser(uid, userId)) {
            return "Not a valid user";
        } else {
            ANS pANS = ansRepository.getAnsText(userId, ansId);
            if (pANS == null)
                return "QR not found";

            if (isAbusive != null && !pANS.getIsAbusive().equals(isAbusive)) {
                pANS.setIsAbusive(isAbusive);
                if (isAbusive && pANS.getUserReaction() != -1) {
                    pANS.setUserReaction(-1);
                }
            }
            // handling helpful for now
            if (isHelpful != null) {
                Boolean oldHelpful = pANS.getUserReaction() == 1;
                if (!oldHelpful.equals(isHelpful)) {
                    pANS.setUserReaction(isHelpful ? 1 : -1);
                    if (isHelpful && pANS.getIsAbusive()) {
                        pANS.setIsAbusive(false);
                    }
                }
            }
            if (isShow != null && !pANS.getIsShow().equals(isShow)) {
                pANS.setIsShow(isShow);
            }
            ANS eAnsText = ansRepository.saveText(pANS);
            eAnsText.getEmptyTextANS();
            return eAnsText;
        }

    }

    private String encrypt(String text, String pass) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            Key key = new SecretKeySpec(messageDigest.digest(pass.getBytes(UTF_8)), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(ENCRYPT_MODE, key);

            byte[] encrypted = cipher.doFinal(text.getBytes(UTF_8));
            byte[] encoded = Base64.getEncoder().encode(encrypted);
            return new String(encoded, UTF_8);

        } catch (Throwable e) {
            return null;
        }

    }

}
