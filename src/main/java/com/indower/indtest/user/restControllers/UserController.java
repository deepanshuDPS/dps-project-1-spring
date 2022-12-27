package com.indower.indtest.user.restControllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.indower.indtest.user.models.User;
import com.indower.indtest.user.services.UserServices;

import jakarta.servlet.http.HttpServletRequest;

import java.util.IllegalFormatException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// try to use HATEOAS for linking endpoints

@RestController
@RequestMapping("/auth/user")
public class UserController {

    private String getUID(HttpServletRequest request){
        return request.getHeader("uid");
    }

    @Autowired
    UserServices userServices;

    @GetMapping(value = "/", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<User> getUser(HttpServletRequest request) {
        // used to stay same response for 30 seconds.
        CacheControl cacheControl = CacheControl.maxAge(30, TimeUnit.SECONDS);
        return ResponseEntity.ok().cacheControl(cacheControl).body(userServices.getUser(getUID(request)));
    }

    @PostMapping(value = "/signin", produces = { MediaType.APPLICATION_JSON_VALUE })
    public Map<String, Boolean> userSignIn(
            @RequestParam Map<String, String> allRequestParams) {
        userServices.authenticateUser(new User());
        return null;
    }

    // put used for updating almost every field in an object
    @PutMapping(value = "/edit", produces = { MediaType.APPLICATION_JSON_VALUE })
    public Map<String, Boolean> editUser(
            @RequestBody User user) {
        userServices.editUser(new User());
        return null;
    }

    // put used for updating almost every field in an object
    @PostMapping(value = "/uploadUserImage")
    public Map<String, Boolean> uploadUserImage(
            @RequestParam("image") MultipartFile image) {
        userServices.storeFile(image);
        return null;
    }

    // patch used for only some field edit in an object
    @PatchMapping(value = "/editDesc", produces = { MediaType.APPLICATION_JSON_VALUE })
    public Map<String, Boolean> editDesc(@RequestParam("description") String description) {
        userServices.editDescription(description);
        return null;
    }

    // we can handle different types of exceptions with this custom reponse
    @ExceptionHandler({ NullPointerException.class, IllegalFormatException.class })
    public ResponseEntity<Object> handleTwoExceptions(HttpServletRequest request, Exception exception) {
        return new ResponseEntity<Object>(request, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    // @RequestMapping(value = "user", method = RequestMethod.POST)
    // @ResponseBody
    // public ResponseStatus getUser(@RequestBody User user) {

    // ResponseStatus getUser = null;
    // if(user.getMobile()!=null
    // && (getUser = userRepository.findMobileUser(user.getMobile()))!=null){
    // getUser = new ResponseStatus();
    // getUser.setMessage("User already Exist");
    // getUser.setStatus(false);
    // return getUser;
    // }else if(user.getMobile()==null || user.getName()==null){
    // getUser = new ResponseStatus();
    // getUser.setMessage("All fields are mandetory");
    // getUser.setStatus(false);
    // return getUser;
    // }
    // user.setStatus(true);
    // user.setMessage("User Inserted");
    // return userRepository.insert(user);
    // }

}
