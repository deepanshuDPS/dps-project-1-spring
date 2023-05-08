package com.indower.indtest.ans.restControllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.indower.indtest.ans.models.docModels.ANS;
import com.indower.indtest.ans.services.ANSServices;
import com.indower.indtest.customExceptions.CustomErrorException;
import com.indower.indtest.utils.AppConstants;
import com.indower.indtest.utils.MutableHttpServletRequest;
import com.indower.indtest.utils.MyResponseUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth/ans")
public class AuthANSController {

    @Autowired
    private ANSServices services;

    // get personal ratings
    @GetMapping(value = { "/", "" }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getMyAnsTexts(
            MutableHttpServletRequest request,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo) throws CustomErrorException {
        MyResponseUtils.checkCredentials(request.getUserId());
        Page<ANS> page = services.getAnsTexts(request.getUid(), request.getUserId(), pageNo - 1);
        // index starts with 0
        if (page == null)
            return MyResponseUtils.noDataFound();
        return MyResponseUtils.successfulPageWithCache(page, AppConstants.ONE_HOUR);
    }

    // post review for others
    @PostMapping(value = { "/", "" }, produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> postAnsText(
            MutableHttpServletRequest request,
            @RequestBody @Valid ANS ans) throws CustomErrorException {
        MyResponseUtils.checkReqAndCredentials(request, request.getUserId());
        Object result = services.postAnsText(request.getUid(), request.getUserId(), ans);
        if (result instanceof ANS) {
            return MyResponseUtils.setSuccessResponse("Successfully Sent", true);
        } else {
            return MyResponseUtils.badRequest((String) result);
        }
    }

    // editAnsStatus
    @PatchMapping(value = { "/", "" }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> editAns(
            MutableHttpServletRequest request,
            @RequestBody Map<String, Object> requestParams) throws CustomErrorException {
        MyResponseUtils.checkReqAndCredentials(request);
        String ansTextId = (String) requestParams.get("_id");
        if (ansTextId == null)
            return MyResponseUtils.badRequest("QR Id required!!!");
        Boolean isAbusive = (Boolean) requestParams.get("isAbusive");
        Boolean isHelpful = (Boolean) requestParams.get("isHelpful");
        Boolean isShow = (Boolean) requestParams.get("isShow");
        MyResponseUtils.checkCredentials(request.getUserId(), request.getUid());
        Object result = services.editANSText(
                request.getUid(), ansTextId, request.getUserId(),
                isAbusive, isHelpful, isShow);
        if (result instanceof String)
            return MyResponseUtils.badRequest((String) result);
        else
            return MyResponseUtils.successWithData((ANS) result);
    }
}
