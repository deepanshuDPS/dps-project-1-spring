package com.indower.indtest.rateReviews.restControllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.indower.indtest.customExceptions.CredentialsRequired;
import com.indower.indtest.rateReviews.models.docModels.RateReview;
import com.indower.indtest.rateReviews.services.RateReviewServices;
import com.indower.indtest.rateReviews.services.RateReviewServices.ResResult;
import com.indower.indtest.utils.MutableHttpServletRequest;
import com.indower.indtest.utils.MyResponseUtils;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/auth/rate-review")
public class AuthRateReviewController {

    @Autowired
    private RateReviewServices services;

    // get personal ratings
    @GetMapping(value = "/{pageNo}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getMyReviews(
            MutableHttpServletRequest request, @PathVariable("pageNo") Integer pageNo) {
        Page<RateReview> page = services.getReviews(request.getUserId(), pageNo - 1);
        // index starts with 0
        if (page == null)
            return MyResponseUtils.noDataFound();
        return MyResponseUtils.successfulPage(page);
    }

    // post review for others
    @PostMapping(value = "/", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> postReview(
            MutableHttpServletRequest request,
            @RequestBody @Valid RateReview rateReview) throws CredentialsRequired {
        MyResponseUtils.checkCredentials(request.getUserId());
        ResResult result = services.postReview(request.getUid(), request.getUserId(), rateReview);
        if (result.getType() == 5) {
            return MyResponseUtils.successWithData(result.getRateReview());
        } else {
            return MyResponseUtils.badRequest("Something is incorrect during review");
        }
    }

    // edit review for others
    @PatchMapping(value = "/", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> editReview(
            MutableHttpServletRequest request,
            @RequestBody Map<String,Object> requestParams) throws CredentialsRequired {
        if(requestParams.get("_id") == null)
            return MyResponseUtils.badRequest("review_id required");
        String _id = (String) requestParams.get("_id");
        String review = (String) requestParams.get("review");
        Integer rating = (Integer) requestParams.get("rating");
        MyResponseUtils.checkCredentials(request.getUserId());
        ResResult result = services.editReview(_id, request.getUserId(), rating, review);
        if (result.getType() == 5) {
            return MyResponseUtils.successWithData(result.getRateReview());
        } else {
            return MyResponseUtils.badRequest("Something is incorrect during review");
        }
    }
}
