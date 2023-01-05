package com.indower.indtest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.indower.indtest.models.redisModels.UserData;

@Repository
public interface RedisUserRepository extends CrudRepository<UserData, String> {}