package com.indower.indtest.ans.services;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

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
import com.indower.indtest.ans.models.docModels.ANS;
import com.indower.indtest.models.responseModels.TextPrediction;
import com.indower.indtest.services.RedisMongoService;
import com.indower.indtest.user.models.documentModels.UserDoc;
import com.indower.indtest.utils.AppConstants;

@Service
public class ANSServices extends RedisMongoService {

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
            ans.setIsAbusive(!isNotAbusive);
            ans.setIsShow(isNotAbusive);
            ANS nAns = ansRepository.insertText(ans);
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
                if (isAbusive && pANS.getIsHelpful()) {
                    pANS.setIsHelpful(false);
                }
            }
            if (isHelpful != null && !pANS.getIsHelpful().equals(isHelpful)) {
                pANS.setIsHelpful(isHelpful);
                if (isHelpful && pANS.getIsAbusive()) {
                    pANS.setIsAbusive(false);
                }
            }
            if (isShow != null && !pANS.getIsShow().equals(isShow)) {
                pANS.setIsShow(isShow);
            }
            ANS eAnsText = ansRepository.saveText(pANS);
            return eAnsText;
        }

    }

}
