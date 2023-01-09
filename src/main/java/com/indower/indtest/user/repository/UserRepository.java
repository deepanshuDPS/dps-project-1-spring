package com.indower.indtest.user.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.indower.indtest.user.models.documentModels.UserDoc;

public interface UserRepository extends MongoRepository<UserDoc, String> {
    
    long count();

    List<UserDoc> findAll();

    @Query(value="{oAuthIDs: {$all : [?0] } _id:'?1'}")
    UserDoc findUser(String uid ,String _id);

    @Query(value="{_id:'?0'}")
    UserDoc findUser(String _id);


    @Query(value="{oAuthIDs: {$all : [?0] }}", fields = "{'_id':1, 'onBoarded':1}")
    UserDoc checkAuthUser(String id);

    // returns whole object
    @Query(value="{email:'?0'}")
    UserDoc checkUser(String email);

    @Query(value="{mobile:'?0'}")
    UserDoc findMobileUser(String mobile);

    default UserDoc insertUser(UserDoc user){
        user.setCreatedAt(new Date());
        return insert(user);
    }

    default UserDoc saveUser(UserDoc user){
        user.setUpdatedAt(new Date());
        return save(user);
    }

}

