package com.talentup.interview.assessment.solution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

@Component
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Util {
    @Autowired
    private TestRestTemplate restTemplate;

    public Map<String, Object> buildRequest(String to, String from, String text){
        Map<String, Object> request = new HashMap<>();
        if(to != null)
            request.put("to",to);
        if(from != null)
            request.put("from",from);
        if(text != null)
            request.put("text",text);
        return request;
    }

    public ResponseEntity<String> sendPost(Map<String, Object> json, String url, String username, String password) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(username != null && password != null){
            headers.setBasicAuth(username,password);
        }
        String jsonString = new ObjectMapper().writeValueAsString(json);
        HttpEntity<String> entity = new HttpEntity<>(jsonString, headers);
        return restTemplate.postForEntity(    url, entity, String.class);
    }
}
