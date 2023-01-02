package com.indower.indtest.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.indower.indtest.user.models.User;

public class MyResponseUtils {
    
    public static ResponseEntity<Map<String,Object>> successfulResponse(){
        return setSuccessResponse("Successful",true);
    }

    public static ResponseEntity<Map<String,Object>> setSuccessResponse(String message, boolean status){
        HashMap<String,Object> response = new HashMap<>();
        response.put("message", response);
        response.put("status", status);
        return ResponseEntity.ok().body(response);
    }

    public static ResponseEntity<Map<String,Object>> successWithData(Object data){
        return successResponseWithStatusAndData("successful", true, data);
    }

    public static ResponseEntity<Map<String,Object>> successResponseWithStatusAndData(String message, boolean status, Object data){
        HashMap<String,Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", status);
        response.put("data", data);
        // ObjectMapper mapper = new ObjectMapper();
        // if(!includeNull)
        //     mapper.setSerializationInclusion(Include.NON_NULL);
        // try {
        //     response.put("data", new Gson().fromJson(mapper.writeValueAsString(data),User.class));
        // } catch (JsonProcessingException e) {
        //     response.put("data", null);
        // }
        return ResponseEntity.ok().body(response);
    }
}
