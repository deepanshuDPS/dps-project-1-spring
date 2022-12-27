package com.indower.indtest.user.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.indower.indtest.user.models.User;
import com.indower.indtest.user.repository.UserRepository;


// write all bussiness logic here to retrieve user
@Service
public class UserServices {
    
    @Autowired
    UserRepository userRepository;

    public User getUser(String userId){
        return userRepository.findUser(userId);
    }

    public void authenticateUser(User user){
        
    }

    public void editUser(User user){
        
    }

    public String storeFile(MultipartFile image){
        return "";
    }

    public void editDescription(String description){
        
    }

}
