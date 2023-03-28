package com.indower.indtest.user.restControllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.indower.indtest.user.models.UserData;
import com.indower.indtest.user.services.UserServices;
import com.indower.indtest.utils.AppConstants;
import com.indower.indtest.utils.MyResponseUtils;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServices userServices;

    @GetMapping(value = "/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable("userId") String userId) {
        UserData user = userServices.getUser(userId);
        if (user == null) {
            return MyResponseUtils.noDataFound();
        } else {
            // cache 1 hour
            return MyResponseUtils.successWithDataAndCache(user, AppConstants.ONE_HOUR);
        }
    }

}
