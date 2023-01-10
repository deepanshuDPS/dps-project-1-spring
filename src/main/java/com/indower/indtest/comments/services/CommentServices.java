package com.indower.indtest.comments.services;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.indower.indtest.comments.models.docModels.Comment;
import com.indower.indtest.comments.repository.CommentRepository;
import com.indower.indtest.user.repository.UserRepository;
import com.indower.indtest.utils.AppConstants;

@Service
public class CommentServices {

    public class ResResult {
        private int type;
        private Comment comment;

        public ResResult(int type, Comment comment) {
            this.type = type;
            this.comment = comment;
        }

        public int getType() {
            return type;
        }

        public Comment getComment() {
            return comment;
        }

    }

    @Autowired
    private CommentRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    // if has data then page else null
    @Nullable
    public Page<Comment> getComments(String uid, String userId, Integer page) {

        Pageable paging = PageRequest.of(page, AppConstants.PAGE_SIZE);
        // restrict for comments very private data
        if(userRepository.findUser(uid, userId) == null) return null;

        Page<Comment> comments = repository.findByCommentedId(userId, paging);
        if (comments.getContent() != null && !comments.getContent().isEmpty())
            return comments;
        else
            return null;
    }

    public ResResult postComment(String uid, String commentorId, Comment comment) {

        if (comment.getCommentedId().equals(commentorId)) {
            return new ResResult(1, null);
        } else if (comment.getComment() == null) {
            return new ResResult(2, null);
        } else if (userRepository.findUser(uid, commentorId) == null) {
            return new ResResult(3, null);
        } else if (userRepository.findUser(comment.getCommentedId()) == null) {
            return new ResResult(4, null);
        } else if (repository.countOfCommenter(commentorId, comment.getCommentedId()) == AppConstants.COMMENT_LIMIT) {
            return new ResResult(5, null);
        } else {
            comment.setCommentorId(commentorId);
            return new ResResult(6, repository.insertComment(comment));
        }
    }

}
