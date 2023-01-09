package com.indower.indtest.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.indower.indtest.customExceptions.CredentialsRequired;
import com.indower.indtest.models.ApiError;

public class MyResponseUtils {
    
    public static ResponseEntity<Map<String,Object>> successfulResponse(){
        return setSuccessResponse("Successful",true);
    }

    public static ResponseEntity<Map<String,Object>> setSuccessResponse(String message, boolean status){
        HashMap<String,Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", status);
        return ResponseEntity.ok().body(response);
    }

    public static ResponseEntity<Map<String,Object>> createdResponse(String message){
        HashMap<String,Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", true);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public static ResponseEntity<Map<String,Object>> alreadyExist(String message){
        HashMap<String,Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", true);
        return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(response);
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

    public static ResponseEntity<Map<String,Object>> noDataFound(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public static ResponseEntity<Object> badRequest(ApiError apiError){
        return ResponseEntity.badRequest().body(apiError);
    }

    public static ResponseEntity<Map<String,Object>> badRequest(String apiError){
        HashMap<String,Object> response = new HashMap<>();
        response.put("message", apiError);
        response.put("status", false);
        return ResponseEntity.badRequest().body(response);
    }

    public static ResponseEntity<Map<String,Object>> successfulPage(Page<?> page){
        HashMap<String,Object> response = new HashMap<>();
        response.put("list", page.getContent());
        response.put("currentPage", page.getNumber()+1); // starts with 0 index
        response.put("totalItems", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    public static ResponseEntity<Map<String,Object>> forbidden(String apiError){
        HashMap<String,Object> response = new HashMap<>();
        response.put("message", apiError);
        response.put("status", false);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    public static void checkCredentials(String ...credentials) throws CredentialsRequired{
        for(String x:credentials){
            if(x == null) throw new CredentialsRequired("Credentials Required");
        }
    }
}
