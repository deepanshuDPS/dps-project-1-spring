package com.indower.indtest.user.restControllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.indower.indtest.customExceptions.CredentialsRequired;
import com.indower.indtest.user.models.User;
import com.indower.indtest.user.services.UserServices;
import com.indower.indtest.utils.MyResponseUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// try to use HATEOAS for linking endpoints

@RestController
@RequestMapping("/auth/user")
public class UserController {

    private String getUID(HttpServletRequest request) {
        return request.getHeader("uid");
    }

    private String getUserId(HttpServletRequest request) {
        return request.getHeader("user-id");
    }

    private String getEmail(HttpServletRequest request) {
        return request.getHeader("email");
    }

    @Autowired
    UserServices userServices;

    // get user details
    @GetMapping(value = "/", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getUser(HttpServletRequest request) {
        // used to stay same response for 30 seconds.
        User user = userServices.getUser(getUID(request), getUserId(request));
        if (user == null) {
            return MyResponseUtils.noDataFound();
        } else {
            // CacheControl cacheControl = CacheControl.maxAge(30, TimeUnit.SECONDS);
            // return ResponseEntity.ok().cacheControl(cacheControl).body(user);
            return MyResponseUtils.successWithData(user);
        }
    }

    // check user exist of not
    @GetMapping(value = "/emailUser", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> emailUser(HttpServletRequest request) throws CredentialsRequired {
        // used to stay same response for 30 seconds.
        String email = getEmail(request);
        String uid = getUID(request);
        MyResponseUtils.checkCredentials(email,uid);
        User user = userServices.checkEmailUser(email, uid);
        if (user == null) {
            return MyResponseUtils.noDataFound();
        } else {
            return MyResponseUtils.successWithData(user.toIdOrStatus());
        }
    }

     // check user exist of not
     @GetMapping(value = "/googleUser", produces = { MediaType.APPLICATION_JSON_VALUE })
     public ResponseEntity<Map<String, Object>> googleUser(HttpServletRequest request) throws CredentialsRequired {
         // used to stay same response for 30 seconds.
         String email = getEmail(request);
         String uid = getUID(request);
         MyResponseUtils.checkCredentials(email,uid);
         User user = userServices.checkGoogleUser(email, uid);
         if (user == null) {
             return MyResponseUtils.noDataFound();
         } else {
             return MyResponseUtils.successWithData(user.toIdOrStatus());
         }
     }
 
    
    // sign up user with account type
    @PostMapping(value = "/signup", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> userSignIn(
            @RequestBody @Valid User user, HttpServletRequest request) throws CredentialsRequired {
        String userId = getUserId(request);
        String uid = getUID(request);
        MyResponseUtils.checkCredentials(userId,uid);
        Integer result = userServices.signUpUser(uid, userId, user);
        if (result == null) {
            return MyResponseUtils.noDataFound();
        } else if (result == 1) {
            return MyResponseUtils.createdResponse("User account created");
        } else if (result == 2){
            return MyResponseUtils.badRequest("No profession or gender is defined");
        } 
        return MyResponseUtils.alreadyExist("User already registered");
        
    }

    // put used for updating almost every field in an object
    @PutMapping(value = "/editProfile", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> editUser(
        @RequestBody @Valid User user, HttpServletRequest request) throws CredentialsRequired {
        String userId = getUserId(request);
        String uid = getUID(request);
        MyResponseUtils.checkCredentials(userId,uid);
        Integer result = userServices.editUser(uid,userId,user);
        if(result == null)
            return MyResponseUtils.noDataFound();
        else if(result == 1)
            return MyResponseUtils.setSuccessResponse("User updated successfully",true);
        else if(result == 2){
            return MyResponseUtils.badRequest("No profession or gender is defined");
        }
        return MyResponseUtils.forbidden("User not boarded");
    }

    @PostMapping(value = "/reviewerGoogle")
    public Map<String, Boolean> reviewerGoogle(
            @RequestParam("image") MultipartFile image) {
        userServices.storeFile(image);
        return null;
    }

    @PostMapping(value = "/reviewerEmail")
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

}
