package com.indower.indtest.user.restControllers;

import org.springframework.web.bind.annotation.RestController;

import com.indower.indtest.customExceptions.CredentialsRequired;
import com.indower.indtest.user.models.User;
import com.indower.indtest.user.models.documentModels.UserDoc;
import com.indower.indtest.user.services.AuthUserServices;
import com.indower.indtest.utils.MutableHttpServletRequest;
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
public class AuthUserController {

    @Autowired
    AuthUserServices userServices;

    // get user details
    @GetMapping(value = "/", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getUser(MutableHttpServletRequest request) {
        // used to stay same response for 30 seconds.
        UserDoc user = userServices.getUser(request.getUid(), request.getUserId());
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
    public ResponseEntity<Map<String, Object>> emailUser(MutableHttpServletRequest request) throws CredentialsRequired {
        // used to stay same response for 30 seconds.
        String email = request.getEmail();
        String uid = request.getUid();
        MyResponseUtils.checkCredentials(email, uid);
        UserDoc user = userServices.checkEmailUser(email, uid);
        return signUpResponse(user, false);
    }

    // check user exist of not
    @GetMapping(value = "/googleUser", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> googleUser(MutableHttpServletRequest request)
            throws CredentialsRequired {
        // used to stay same response for 30 seconds.
        String email = request.getEmail();
        String uid = request.getUid();
        MyResponseUtils.checkCredentials(email, uid);
        UserDoc user = userServices.checkGoogleUser(email, uid);
        if (user == null) {
            return MyResponseUtils.noDataFound();
        } else {
            return MyResponseUtils.successWithData(user.toIdOrStatus());
        }
    }

    // check user exist of not for email comes from google OAuth
    @PostMapping(value = "/emailUserGAuth", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> emailUserWithGAuth(MutableHttpServletRequest request,
            @RequestBody Map<String, Object> body) throws CredentialsRequired {
        // used to stay same response for 30 seconds.
        String email = (String) body.get("email");
        String uid = request.getUid();
        MyResponseUtils.checkCredentials(email, uid);
        UserDoc user = userServices.checkGoogleUser(email, uid);
        if (user == null) {
            return MyResponseUtils.noDataFound();
        } else {
            return MyResponseUtils.successWithData(user.toIdOrStatus());
        }
    }

    public ResponseEntity<Map<String, Object>> signUpResponse(Integer result, boolean forReviewer) {
        if (result == null) {
            return MyResponseUtils.noDataFound();
        } else if (result == 1) {
            return MyResponseUtils.createdResponse("User account created" + (forReviewer ? " for review" : ""));
        } else if (result == 2) {
            return MyResponseUtils.badRequest("No profession or gender is defined");
        }
        return MyResponseUtils.alreadyExist("User already registered");
    }

    public ResponseEntity<Map<String, Object>> signUpResponse(UserDoc user, boolean forReviewer) {
        if (user == null)
            return MyResponseUtils.noDataFound();
        else
            return MyResponseUtils.successWithData(user.toIdOrStatus());

    }

    // sign up user with account type
    @PostMapping(value = "/signup", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> userSignUp(
            @RequestBody @Valid User user, MutableHttpServletRequest request) throws CredentialsRequired {
        String userId = request.getUserId();
        String uid = request.getUid();
        MyResponseUtils.checkCredentials(userId, uid);
        Integer result = userServices.signUpUser(uid, userId, user);
        return signUpResponse(result, false);
    }

    @PostMapping(value = "/reviewerGoogle", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> reviewerGoogle(MutableHttpServletRequest request)
            throws CredentialsRequired {
        MyResponseUtils.checkCredentials(request.getEmail());
        UserDoc result = userServices.reviewerFromGoogle(request.getUid(), request.getEmail());
        return signUpResponse(result, true);
    }

    @PostMapping(value = "/reviewerEmailGAuth", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> reviewerEmailWithGAuth(
            MutableHttpServletRequest request,
            @RequestBody Map<String, Object> body)
            throws CredentialsRequired {
        String email = (String) body.get("email");
        MyResponseUtils.checkCredentials(email);
        UserDoc result = userServices.reviewerFromGoogle(request.getUid(), email);
        return signUpResponse(result, true);
    }

    @PostMapping(value = "/reviewerEmail", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> uploadUserImage(MutableHttpServletRequest request)
            throws CredentialsRequired {
        MyResponseUtils.checkCredentials(request.getEmail());
        UserDoc result = userServices.reviewerFromEmail(request.getUid(), request.getEmail());
        return signUpResponse(result, true);
    }

    // put used for updating almost every field in an object
    @PutMapping(value = "/editProfile", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> editUser(
            @RequestBody @Valid User user, MutableHttpServletRequest request) throws CredentialsRequired {
        String userId = request.getUserId();
        String uid = request.getUid();
        MyResponseUtils.checkCredentials(userId, uid);
        Integer result = userServices.editUser(uid, userId, user);
        if (result == null)
            return MyResponseUtils.noDataFound();
        else if (result == 1)
            return MyResponseUtils.setSuccessResponse("User updated successfully", true);
        else if (result == 2) {
            return MyResponseUtils.badRequest("No profession or gender is defined");
        }
        return MyResponseUtils.forbidden("User not boarded");
    }

    // patch used for only some field edit in an object
    @PatchMapping(value = "/editDesc", produces = { MediaType.APPLICATION_JSON_VALUE })
    public Map<String, Boolean> editDesc(@RequestParam("description") String description) {
        userServices.editDescription(description);
        return null;
    }

    // patch used for only some field edit in an object
    @DeleteMapping(value = "/u-s-e-r-d-l-t", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> deleteUser(HttpServletRequest request) {
        userServices.deleteUser(request.getHeader("user-id"));
        return MyResponseUtils.setSuccessResponse("User Deleted", true);
    }

}
