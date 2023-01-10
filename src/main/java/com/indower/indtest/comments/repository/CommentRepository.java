package com.indower.indtest.comments.repository;

import java.util.Date;

import org.springframework.data.domain.*;
import org.springframework.data.mongodb.repository.*;

import com.indower.indtest.comments.models.docModels.Comment;

public interface CommentRepository extends MongoRepository<Comment, String> {

    Page<Comment> findByCommentedId(String commentedId, Pageable pageable);

    @Query(value = "{commentorId: '?0' commentedId:'?1'}", count = true)
    Long countOfCommenter(String commentorId, String commentedId);

    default Comment insertComment(Comment comment) {
        comment.setCreatedAt(new Date());
        return insert(comment);
    }

    default Comment saveComment(Comment comment) {
        comment.setUpdatedAt(new Date());
        return save(comment);
    }
}
