package com.indower.indtest.user.restControllers;

import org.springframework.web.bind.annotation.RestController;

import com.indower.indtest.customExceptions.CredentialsRequired;
import com.indower.indtest.user.models.User;
import com.indower.indtest.user.models.UserData;
import com.indower.indtest.user.models.documentModels.UserDoc;
import com.indower.indtest.user.services.AuthUserServices;
import com.indower.indtest.utils.AppConstants;
import com.indower.indtest.utils.EnvironmentSetup;
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

    @Autowired
    private EnvironmentSetup setup;

    private String getFileUrl() {
        return setup.isProd() ? "https://s3.ap-south-1.amazonaws.com/files.dpskreations.com/"
                : "http://files.dpskreations.com/";
    }

    // get user details
    @GetMapping(value = { "/", "" }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> fetchUser(MutableHttpServletRequest request) {
        // used to stay same response for 30 seconds.
        UserData user = userServices.getUser(request.getUid(), request.getUserId());
        if (user == null) {
            return MyResponseUtils.noDataFound();
        } else {
            // cache 1 hour
            return MyResponseUtils.successWithDataAndCache(user, AppConstants.ONE_HOUR);
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
            return MyResponseUtils.successWithDataAndCache(user.toIdOrStatus(), AppConstants.THIRTY_SECS);
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
            return MyResponseUtils.successWithDataAndCache(user.toIdOrStatus(), AppConstants.THIRTY_SECS);
        }
    }

    // check user exist of not for email comes from google OAuth
    @PostMapping(value = "/emailUserFbAuth", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> emailUserWithFbAuth(MutableHttpServletRequest request,
            @RequestBody Map<String, Object> body) throws CredentialsRequired {
        // used to stay same response for 30 seconds.
        String email = (String) body.get("email");
        String uid = request.getUid();
        MyResponseUtils.checkCredentials(email, uid);
        UserDoc user = userServices.checkForFbUser(email, uid);
        if (user == null) {
            return MyResponseUtils.noDataFound();
        } else {
            return MyResponseUtils.successWithDataAndCache(user.toIdOrStatus(), AppConstants.THIRTY_SECS);
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
            return MyResponseUtils.successWithDataAndCache(forReviewer ? user : user.toIdOrStatus(),
                    AppConstants.THIRTY_SECS);

    }

    // @PostMapping("/checkImageUpload")
    // public ResponseEntity<Map<String, Object>> uploadFile(
    // @RequestParam(value = "file") MultipartFile file, MutableHttpServletRequest
    // request) {
    // if (file != null)
    // userServices.uploadfile(request.getUserId(), file);
    // HashMap<String, String> map = new HashMap<>();
    // map.put("imageUrl", "/here");
    // return MyResponseUtils.successWithData(map);
    // }

    // sign up user with account type
    @PostMapping(value = "/signup", produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> userSignUp(
            @RequestBody @Valid User user, MutableHttpServletRequest request) throws CredentialsRequired {
        // System.out.println(user.getName() + " image: " + user.getBase64Image());
        String userId = request.getUserId();
        String uid = request.getUid();
        MyResponseUtils.checkCredentials(userId, uid);
        if (!userServices.uploadfile(request.getUserId(), user.getBase64Image()))
            return MyResponseUtils.forbidden("Something went wrong with the details");
        user.setImageUrl(getFileUrl() + "profile/" + userId + ".jpeg");
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

    @PostMapping(value = "/reviewerFacebook", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> reviewerFacebook(MutableHttpServletRequest request)
            throws CredentialsRequired {
        MyResponseUtils.checkCredentials(request.getEmail());
        UserDoc result = userServices.reviewerFromFacebook(request.getUid(), request.getEmail());
        return signUpResponse(result, true);
    }

    @PostMapping(value = "/reviewerEmailFBAuth", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> reviewerEmailWithFBAuth(
            MutableHttpServletRequest request,
            @RequestBody Map<String, Object> body)
            throws CredentialsRequired {
        String email = (String) body.get("email");
        MyResponseUtils.checkCredentials(email);
        UserDoc result = userServices.reviewerFromFacebook(request.getUid(), email);
        return signUpResponse(result, true);
    }

    // @PostMapping(value = "/reviewerEmail", produces = { MediaType.APPLICATION_JSON_VALUE })
    // public ResponseEntity<Map<String, Object>> uploadUserImage(MutableHttpServletRequest request)
    //         throws CredentialsRequired {
    //     MyResponseUtils.checkCredentials(request.getEmail());
    //     UserDoc result = userServices.reviewerFromEmail(request.getUid(), request.getEmail());
    //     return signUpResponse(result, true);
    // }

    // put used for updating almost every field in an object
    @PutMapping(value = "/editProfile", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> editUser(
            @RequestBody @Valid UserDoc user, MutableHttpServletRequest request) throws CredentialsRequired {
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
    @PatchMapping(value = "/editImage", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> editDesc(@RequestBody Map<String, Object> requestBody,
            MutableHttpServletRequest request) throws CredentialsRequired {
        String userId = request.getUserId();
        String uid = request.getUid();
        String image = (String) requestBody.get("base64Image");
        MyResponseUtils.checkCredentials(userId, uid, image);
        if (!userServices.uploadfile(request.getUserId(), image))
            return MyResponseUtils.forbidden("Something went wrong with the details");
        return MyResponseUtils.successfulResponse();
    }

    // patch used for only some field edit in an object
    @DeleteMapping(value = "/u-s-e-r-d-l-t", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> deleteUser(HttpServletRequest request) {
        userServices.deleteUser(request.getHeader("user-id"));
        return MyResponseUtils.setSuccessResponse("User Deleted", true);
    }

}
