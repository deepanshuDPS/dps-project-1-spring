package com.indower.indtest.ans.repository;

import java.util.Date;

import org.springframework.data.domain.*;
import org.springframework.data.mongodb.repository.*;

import com.indower.indtest.ans.models.docModels.ANS;

public interface ANSRepository extends MongoRepository<ANS, String> {

    Page<ANS> findByToWhomId(String toWhomId, Pageable pageable);

    @Query(value = "{doerId: '?0' toWhomId:'?1'}", count = true)
    Long countOfDoer(String doerId, String toWhomId);

    @Query(value = "{toWhomId:'?0' _id:'?1'}")
    ANS getAnsText(String toWhomId, String ansTextId);

    @Query(value = "{toWhomId: '?0'}", count = true)
    Integer countOfAnText(String userId);

    @Query(value = "{doerId: '?0'}", count = true)
    Integer countOfDoer(String userId);

    default ANS insertText(ANS ans) {
        ans.setCreatedAt(new Date());
        return insert(ans);
    }

    default ANS insertText(ANS ans, Date date) {
        ans.setCreatedAt(date);
        return insert(ans);
    }

    default ANS saveText(ANS ans) {
        ans.setUpdatedAt(new Date());
        return save(ans);
    }
}
