package com.indower.indtest.user.services;

import java.util.ArrayList;

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

    public User getUser(String uid, String _id){
        return userRepository.findUser(uid, _id);
    }

    public User checkUser(String email, String uid){
        User uidUser = userRepository.checkAuthUser(uid);
        // it means uid of user not exist in documents
        if( uidUser == null ){
            User emailUser = userRepository.checkUser(email);
            // user not exist in document - create new user - set onBoarded false
            if(emailUser == null){
                ArrayList<String> oAuthIDs = new ArrayList<>();
                User newUser = new User();
                newUser.setEmail(email);
                oAuthIDs.add(uid);
                newUser.setoAuthIDs(oAuthIDs);
                newUser.setOnBoarded(false);
                return userRepository.insert(newUser);
            }else {
                // update uid array to particular _id
                if(emailUser.getoAuthIDs()!=null){
                    emailUser.getoAuthIDs().add(uid);
                }else{
                    ArrayList<String> oAuthIDs = new ArrayList<>();
                    oAuthIDs.add(uid);
                    emailUser.setoAuthIDs(oAuthIDs);
                }
                userRepository.save(emailUser);
                return emailUser;
            }
        }

        return uidUser;
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
