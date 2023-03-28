package com.indower.indtest.ans.services;

import javax.annotation.Nullable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.indower.indtest.ans.models.docModels.ANS;
import com.indower.indtest.services.RedisMongoService;
import com.indower.indtest.utils.AppConstants;

@Service
public class ANSServices extends RedisMongoService {

    // if has data then page else null
    @Nullable
    public Page<ANS> getAnsTexts(String uid, String userId, Integer page) {

        Pageable paging = PageRequest.of(page, AppConstants.PAGE_SIZE);
        // restrict for ans very private data
        if (userRepository.findUser(uid, userId) == null)
            return null;

        Page<ANS> ans = ansRepository.findByToWhomId(userId, paging);
        if (ans.getContent() != null && !ans.getContent().isEmpty())
            return ans;
        else
            return null;
    }

    public Object postAnsText(String uid, String doerId, ANS ans) {

        if (ans.getDoerId().equals(doerId)) {
            return "You can QR yourself";
        } else if (ans.getText() == null) {
            return "Please enter some test to QR";
        } else if (userRepository.findUser(uid, doerId) == null) {
            return "No data found for Reviewer";
        } else if (userRepository.findUser(ans.getToWhomId()) == null) {
            return "No user found to QR";
        } else if (ansRepository.countOfDoer(doerId, ans.getToWhomId()) == AppConstants.ANS_LIMIT) {
            return "QR limit Exceeded for you";
        } else {
            ans.setDoerId(doerId);
            ANS nAns = ansRepository.insertText(ans);
            setCountAns(ans.getToWhomId());
            setDoAnsCount(ans.getDoerId());
            return nAns;
        }
    }

}
