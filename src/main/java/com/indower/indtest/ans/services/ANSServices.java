package com.indower.indtest.ans.services;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.indower.indtest.ans.models.docModels.ANS;
import com.indower.indtest.ans.repository.ANSRepository;
import com.indower.indtest.user.repository.UserRepository;
import com.indower.indtest.utils.AppConstants;

@Service
public class ANSServices {

    public class ResResult {
        private int type;
        private ANS ans;

        public ResResult(int type, ANS ans) {
            this.type = type;
            this.ans = ans;
        }

        public int getType() {
            return type;
        }

        public ANS getAnsText() {
            return ans;
        }

    }

    @Autowired
    private ANSRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    // if has data then page else null
    @Nullable
    public Page<ANS> getAnsTexts(String uid, String userId, Integer page) {

        Pageable paging = PageRequest.of(page, AppConstants.PAGE_SIZE);
        // restrict for ans very private data
        if(userRepository.findUser(uid, userId) == null) return null;

        Page<ANS> ans = repository.findByToWhomId(userId, paging);
        if (ans.getContent() != null && !ans.getContent().isEmpty())
            return ans;
        else
            return null;
    }

    public ResResult postAnsText(String uid, String doerId, ANS ans) {

        if (ans.getDoerId().equals(doerId)) {
            return new ResResult(1, null);
        } else if (ans.getText() == null) {
            return new ResResult(2, null);
        } else if (userRepository.findUser(uid, doerId) == null) {
            return new ResResult(3, null);
        } else if (userRepository.findUser(ans.getToWhomId()) == null) {
            return new ResResult(4, null);
        } else if (repository.countOfDoer(doerId, ans.getToWhomId()) == AppConstants.ANS_LIMIT) {
            return new ResResult(5, null);
        } else {
            ans.setDoerId(doerId);
            return new ResResult(6, repository.insertText(ans));
        }
    }

}
