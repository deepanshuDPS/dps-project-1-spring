package com.indower.indtest.user.restControllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.indower.indtest.customExceptions.CustomErrorException;
import com.indower.indtest.user.services.UserServices;
import com.indower.indtest.utils.MutableHttpServletRequest;
import com.indower.indtest.utils.MyResponseUtils;

@RestController
@RequestMapping("/anon/user")
public class AnonUserController {

    @Autowired
    UserServices userServices;

    @PostMapping(value = "/reviewerEmail", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> reviewerEmail(MutableHttpServletRequest request,
            @RequestBody Map<String, Object> body)
            throws CustomErrorException {
        String email = (String) body.get("email");
        MyResponseUtils.checkReqAndCredentials(request, email);
        Object tokenResponse = userServices.makeAnonymousUser(email);
        if (tokenResponse != null) {
            HashMap<String, Object> response = new HashMap<>();
            response.put("data", tokenResponse);
            return MyResponseUtils.createdResponse("Anon user authentication", response);
        } else {
            return MyResponseUtils.forbidden("Not valid email or user");
        }
    }

}
