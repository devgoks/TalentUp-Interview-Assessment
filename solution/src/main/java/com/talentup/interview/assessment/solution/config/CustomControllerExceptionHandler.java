package com.talentup.interview.assessment.solution.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomControllerExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<Object> handle(MethodArgumentNotValidException exception) {
        String errorMessage = new ArrayList<>(exception.getAllErrors()).get(0).getDefaultMessage();
        Map<String,Object> map = new HashMap<>();
        map.put("error",errorMessage);
        map.put("message","");
        return new ResponseEntity<>(map, null, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity handle(HttpRequestMethodNotSupportedException exception) {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

//    @ExceptionHandler
//    public ResponseEntity<Object> handle(Exception exception) {
//        Map<String,Object> map = new HashMap<>();
//        map.put("error","unknown failure");
//        map.put("message","");
//        return new ResponseEntity<>(map, null, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
