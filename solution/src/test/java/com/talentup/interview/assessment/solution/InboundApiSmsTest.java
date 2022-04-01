package com.talentup.interview.assessment.solution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.talentup.interview.assessment.solution.service.SmsService;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class InboundApiSmsTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private SmsService smsService;
    @LocalServerPort
    private String port;
    @Autowired
    private Util util;
    @Test
    public void inboundSmsApiSuccessfulInboundSms() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("4924195509194","858476355363","Hello Brother");
        String response = util.sendPost(request,"http://localhost:"+port+"/inbound/sms/","azr1","20S0KPNOIM").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("inbound sms ok", res.getString("message"));
    }

    @Test
    public void inboundStopMessageShouldStoreFromToPeerInRedis() throws JsonProcessingException, org.json.JSONException {
        String from = "858476355363";
        String to = "4924195509194";
        Map<String, Object> request = util.buildRequest(to,from,"STOP");
        String response = util.sendPost(request,"http://localhost:"+port+"/inbound/sms/","azr1","20S0KPNOIM").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("inbound sms ok", res.getString("message"));
        String message = redisTemplate.opsForValue().get(smsService.joinFromAndToPhoneNumber(from,to)).toString();
        Assertions.assertEquals("STOP", message);
    }

    @Test
    public void unsupportedHttpMethodRequestToInboundSmsApiShouldReturnHttp405(){
        int statusCodeValue = restTemplate.getForEntity("http://localhost:"+port+"/inbound/sms/",
                String.class).getStatusCodeValue();
        Assertions.assertEquals(405, statusCodeValue);
    }

    @Test
    public void inboundSmsApiWrongAuthenticationShouldReturnHttp403() throws JsonProcessingException {
        Map<String, Object> request = util.buildRequest("968493934","87573839285848","Hello World");
        ResponseEntity<String> response = util.sendPost(request,"http://localhost:"+port+"/inbound/sms/","user","password");
        Assertions.assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    public void inboundSmsApifromParameterlessthanSixCharacters() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("968663934","848","Hello World");
        String response = util.sendPost(request,"http://localhost:"+port+"/inbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("from is invalid", res.getString("error"));
    }

    @Test
    public void inboundSmsApifromParameterMorethanSixteenCharacters() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("968663934","8489999999999999999999999992222222222","Hello World");
        String response = util.sendPost(request,"http://localhost:"+port+"/inbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("from is invalid", res.getString("error"));
    }

    @Test
    public void inboundSmsApiToParameterlessthanSixCharacters() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("9999","8499999998","Hello World");
        String response = util.sendPost(request,"http://localhost:"+port+"/inbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("to is invalid", res.getString("error"));
    }

    @Test
    public void inboundSmsApiToParameterMorethanSixteenCharacters() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("9399994443333334987653322288888888888834","8993322228448222","Hello World");
        String response = util.sendPost(request,"http://localhost:"+port+"/inbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("to is invalid", res.getString("error"));
    }

    @Test
    public void inboundSmsApiTextParameterlessthanOneCharacters() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("999999999999999","8499999998","");
        String response = util.sendPost(request,"http://localhost:"+port+"/inbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("text is invalid", res.getString("error"));
    }

    @Test
    public void inboundSmsApiTextParameterMorethan120Characters() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("7998888834","8993322228448222",
                "Hello world Hello worldHello worldHello worldHello worldHello " +
                        "worldHello worldHello worldHello worldHello worldHello world" +
                        "Hello worldHello worldHello worldHello worldHello worldHello worldHello worldHello worldHello world"
                );
        String response = util.sendPost(request,"http://localhost:"+port+"/inbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("text is invalid", res.getString("error"));
    }

    @Test
    public void inboundSmsApiTextParameterIsMissing() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("99988484849","8499999998",null);
        String response = util.sendPost(request,"http://localhost:"+port+"/inbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("text is missing", res.getString("error"));
    }

    @Test
    public void inboundSmsApiToParameterIsMissing() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest(null,"8499999998","Hello World");
        String response = util.sendPost(request,"http://localhost:"+port+"/inbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("to is missing", res.getString("error"));
    }

    @Test
    public void inboundSmsApiFromParameterIsMissing() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("99988484849",null,"Hello World");
        String response = util.sendPost(request,"http://localhost:"+port+"/inbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("from is missing", res.getString("error"));
    }

    @Test
    public void inboundSmsApiToParameterNotFoundForAccountAuthenicated() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("99988484849","858476355363","Hello Brother");
        String response = util.sendPost(request,"http://localhost:"+port+"/inbound/sms/","azr1","20S0KPNOIM").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("to parameter not found", res.getString("error"));
    }
}
