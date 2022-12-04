package com.indower.indtest.user.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.indower.indtest.user.models.User;

public interface UserRepository extends MongoRepository<User, String> {
    
    long count();

    List<User> findAll();

    @Query(value="{_id:'?0'}")
    User findUser(String id);

    @Query(value="{mobile:'?0'}")
    User findMobileUser(String mobile);


}

