package com.indower.indtest.user.restControllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.indower.indtest.customExceptions.CredentialsRequired;
import com.indower.indtest.user.services.UserServices;
import com.indower.indtest.utils.MutableHttpServletRequest;
import com.indower.indtest.utils.MyResponseUtils;

@RestController
@RequestMapping("/anon/user")
public class AnonUserController {

    @Autowired
    UserServices userServices;

    @GetMapping(value = "/reviewerEmail", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> uploadUserImage(MutableHttpServletRequest request)
            throws CredentialsRequired {
        MyResponseUtils.checkCredentials(request.getEmail());
        String token = userServices.makeAnonymousUser(request.getEmail());
        if (token != null) {
            HashMap<String, Object> response = new HashMap<>();
            response.put("guest_token", token);
            return MyResponseUtils.createdResponse("Anon user authentication", response);
        } else {
            return MyResponseUtils.forbidden("Not valid email or user");
        }
    }

}
