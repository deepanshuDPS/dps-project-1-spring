package com.indower.user.restControllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.indower.user.models.UserData;
import com.indower.user.services.UserServices;
import com.indower.utils.AppConstants;
import com.indower.utils.MutableHttpServletRequest;
import com.indower.utils.MyResponseUtils;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServices userServices;

    @GetMapping(value = "/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getUser(MutableHttpServletRequest request, @PathVariable("userId") String userId) {
        // check request has logined user or not by getUserId();
        UserData user = userServices.getUser(userId, request.getUserId());
        if (user == null) {
            return MyResponseUtils.noDataFound();
        } else {
            // cache 1 hour
            return MyResponseUtils.successWithDataAndCache(user, AppConstants.ONE_HOUR);
        }
    }

}
