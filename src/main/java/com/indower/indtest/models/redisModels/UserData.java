package com.indower.indtest.models.redisModels;

import java.io.Serializable;
import java.util.ArrayList;

import org.springframework.data.redis.core.RedisHash;

@RedisHash("UserData")
public class UserData implements Serializable {
    
    String id;

    ArrayList<String> langList;
    Integer accountType;

    public ArrayList<String> getLangList() {
        return langList;
    }

    public void setLangList(ArrayList<String> langList) {
        this.langList = langList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }
    

}
