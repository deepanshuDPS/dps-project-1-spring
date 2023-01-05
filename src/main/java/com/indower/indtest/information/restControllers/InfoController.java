package com.indower.indtest.information.restControllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.indower.indtest.utils.MyResponseUtils;

@RestController
@RequestMapping("/info")
public class InfoController {
    
    @GetMapping("/terms-and-conditions")
    public ResponseEntity<Map<String, Object>> termsAndConditions(){
        return MyResponseUtils.successWithData("Here are terms and conditions");
    }

    @GetMapping("/faq")
    public ResponseEntity<Map<String, Object>> faq(){
        return MyResponseUtils.successWithData("Here are faqs");
    }
}
