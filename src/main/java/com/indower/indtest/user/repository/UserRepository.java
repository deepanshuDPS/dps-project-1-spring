package com.indower.indtest.user.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.indower.indtest.user.models.User;

public interface UserRepository extends MongoRepository<User, String> {
    
    long count();

    List<User> findAll();

    @Query(value="{oAuthIDs: {$all : [?0] } _id:'?1'}")
    User findUser(String uid ,String _id);

    @Query(value="{oAuthIDs: {$all : [?0] }}", fields = "{'_id':1, 'onBoarded':1}")
    User checkAuthUser(String id);

    // returns whole object
    @Query(value="{email:'?0'}")
    User checkUser(String email);

    @Query(value="{mobile:'?0'}")
    User findMobileUser(String mobile);

    default User insertUser(User user){
        user.setCreatedAt(new Date());
        return insert(user);
    }

    default User saveUser(User user){
        user.setUpdatedAt(new Date());
        return save(user);
    }

}

