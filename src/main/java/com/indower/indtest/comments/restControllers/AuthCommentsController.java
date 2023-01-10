package com.indower.indtest.comments.restControllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.indower.indtest.comments.models.docModels.Comment;
import com.indower.indtest.comments.services.CommentServices;
import com.indower.indtest.comments.services.CommentServices.ResResult;
import com.indower.indtest.customExceptions.CredentialsRequired;
import com.indower.indtest.utils.MutableHttpServletRequest;
import com.indower.indtest.utils.MyResponseUtils;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/auth/comment")
public class AuthCommentsController {

    @Autowired
    private CommentServices services;

    // get personal ratings
    @GetMapping(value = "/{pageNo}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getMyReviews(
            MutableHttpServletRequest request,
            @PathVariable("pageNo") Integer pageNo) throws CredentialsRequired {
        MyResponseUtils.checkCredentials(request.getUserId());
        Page<Comment> page = services.getComments(request.getUid(),request.getUserId(), pageNo - 1);
        // index starts with 0
        if (page == null)
            return MyResponseUtils.noDataFound();
        return MyResponseUtils.successfulPage(page);
    }

    // post review for others
    @PostMapping(value = "/", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> postReview(
            MutableHttpServletRequest request,
            @RequestBody @Valid Comment comment) throws CredentialsRequired {
        MyResponseUtils.checkCredentials(request.getUserId());
        ResResult result = services.postComment(request.getUid(), request.getUserId(), comment);
        if (result.getType() == 6) {
            return MyResponseUtils.successWithData(result.getComment());
        } else {
            return MyResponseUtils.badRequest("Something is incorrect during comment");
        }
    }

    // editCommentStatus
    @PatchMapping(value = "/", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> editReview(
            MutableHttpServletRequest request,
            @RequestBody Map<String,Object> requestParams) throws CredentialsRequired {
        return null;
    }
}
