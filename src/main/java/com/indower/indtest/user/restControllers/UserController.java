package com.indower.indtest.user.restControllers;

import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.indower.indtest.models.ResponseStatus;
import com.indower.indtest.user.models.User;
import com.indower.indtest.user.repository.UserRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "user", method = RequestMethod.GET)
    @ResponseBody
    public List<User> requestMethodName() {
        return userRepository.findAll();
    }

    @RequestMapping(value = "user/{id}", method = RequestMethod.GET)
    @ResponseBody
    public User getUser(@PathVariable("id") String id) {
        return userRepository.findUser(id);
    }

    @RequestMapping(value = "user", method = RequestMethod.POST)
    @ResponseBody
    public ResponseStatus getUser(@RequestBody User user) {

        ResponseStatus getUser = null;
        if(user.getMobile()!=null 
            && (getUser = userRepository.findMobileUser(user.getMobile()))!=null){
            getUser = new ResponseStatus();
            getUser.setMessage("User already Exist");
            getUser.setStatus(false);
            return getUser;
        }else if(user.getMobile()==null || user.getName()==null){
            getUser = new ResponseStatus();
            getUser.setMessage("All fields are mandetory");
            getUser.setStatus(false);
            return getUser;
        }
        user.setStatus(true);
        user.setMessage("User Inserted");
        return userRepository.insert(user);
    }

}
