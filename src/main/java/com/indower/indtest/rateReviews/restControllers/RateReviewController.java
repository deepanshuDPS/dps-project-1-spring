package com.indower.indtest.rateReviews.restControllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.indower.indtest.rateReviews.models.docModels.RateReview;
import com.indower.indtest.rateReviews.services.RateReviewServices;
import com.indower.indtest.utils.AppConstants;
import com.indower.indtest.utils.MutableHttpServletRequest;
import com.indower.indtest.utils.MyResponseUtils;

@RestController
@RequestMapping("/rate-review")
public class RateReviewController {

    @Autowired
    private RateReviewServices services;

    // get personal ratings
    @GetMapping(value = "/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getMyReviews(
            MutableHttpServletRequest request,
            @PathVariable("userId") String userId,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo) {
        Page<RateReview> page = services.getReviews(userId, pageNo - 1);
        // index starts with 0
        if (page == null)
            return MyResponseUtils.noDataFound();
        return MyResponseUtils.successfulPageWithCache(page, AppConstants.ONE_HOUR);
    }
}
