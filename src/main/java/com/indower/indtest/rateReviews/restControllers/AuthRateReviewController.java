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
import com.indower.indtest.utils.AppConstants;
import com.indower.indtest.utils.MutableHttpServletRequest;
import com.indower.indtest.utils.MyResponseUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth/rate-review")
public class AuthRateReviewController {

    @Autowired
    private RateReviewServices services;

    // get personal ratings
    @GetMapping(value = { "/", "" }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getMyReviews(
            MutableHttpServletRequest request, @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo) {
        Page<RateReview> page = services.getReviews(request.getUserId(), pageNo - 1);
        // index starts with 0
        if (page == null)
            return MyResponseUtils.noDataFound();
        return MyResponseUtils.successfulPageWithCache(page, AppConstants.ONE_HOUR);
    }

    // post review for others
    @PostMapping(value = "/", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> postReview(
            MutableHttpServletRequest request,
            @RequestBody @Valid RateReview rateReview) throws CredentialsRequired {
        MyResponseUtils.checkCredentials(request.getUserId());
        Object result = services.postReview(request.getUid(), request.getUserId(), rateReview);
        if (result instanceof String)
            return MyResponseUtils.badRequest((String) result);
        else
            return MyResponseUtils.successWithData((RateReview) result);

    }

    // edit review for others
    @PatchMapping(value = "/", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> editReview(
            MutableHttpServletRequest request,
            @RequestBody Map<String, Object> requestParams) throws CredentialsRequired {
        String reviewedId = (String) requestParams.get("_id");
        if (reviewedId == null)
            return MyResponseUtils.badRequest("Reviewed Id required!!!");
        String review = (String) requestParams.get("review");
        Integer rating = (Integer) requestParams.get("rating");
        if (rating != null && rating < 0 && rating > 5) {
            return MyResponseUtils.badRequest("Wrong rating range!!! Must be (0-5)");
        }
        MyResponseUtils.checkCredentials(request.getUserId());
        Object result = services.editReview(reviewedId, request.getUserId(), rating, review);
        if (result instanceof String)
            return MyResponseUtils.badRequest((String) result);
        else
            return MyResponseUtils.successWithData((RateReview) result);
    }
}
