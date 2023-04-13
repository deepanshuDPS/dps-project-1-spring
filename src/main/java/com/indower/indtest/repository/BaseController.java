package com.indower.indtest.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.indower.indtest.user.services.UserServices;

@RestController
@RequestMapping("/base")
public class BaseController {

    @Autowired
    UserServices userServices;

    @GetMapping(value = { "/", "" }, produces = { MediaType.TEXT_HTML_VALUE })
    public String getBase() {
        return "<h1>This is base path</h1>";
    }

}
