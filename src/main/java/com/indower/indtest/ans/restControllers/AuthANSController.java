package com.indower.indtest.ans.restControllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.indower.indtest.ans.models.docModels.ANS;
import com.indower.indtest.ans.services.ANSServices;
import com.indower.indtest.customExceptions.CredentialsRequired;
import com.indower.indtest.utils.MutableHttpServletRequest;
import com.indower.indtest.utils.MyResponseUtils;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/auth/ans")
public class AuthANSController {

    @Autowired
    private ANSServices services;

    // get personal ratings
    @GetMapping(value = "/{pageNo}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getMyAnsTexts(
            MutableHttpServletRequest request,
            @PathVariable("pageNo") Integer pageNo) throws CredentialsRequired {
        MyResponseUtils.checkCredentials(request.getUserId());
        Page<ANS> page = services.getAnsTexts(request.getUid(),request.getUserId(), pageNo - 1);
        // index starts with 0
        if (page == null)
            return MyResponseUtils.noDataFound();
        return MyResponseUtils.successfulPage(page);
    }

    // post review for others
    @PostMapping(value = "/", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> postAnsText(
            MutableHttpServletRequest request,
            @RequestBody @Valid ANS ans) throws CredentialsRequired {
        MyResponseUtils.checkCredentials(request.getUserId());
        Object result = services.postAnsText(request.getUid(), request.getUserId(), ans);
        if (result instanceof ANS) {
            return MyResponseUtils.successWithData(((ANS)result).getText());
        } else {
            return MyResponseUtils.badRequest((String) result);
        }
    }

    // editCommentStatus
    @PatchMapping(value = "/", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> editAns(
            MutableHttpServletRequest request,
            @RequestBody Map<String,Object> requestParams) throws CredentialsRequired {
        return null;
    }
}
