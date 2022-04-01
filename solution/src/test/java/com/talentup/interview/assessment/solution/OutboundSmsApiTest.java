package com.talentup.interview.assessment.solution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.talentup.interview.assessment.solution.service.SmsService;
import org.json.JSONException;
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
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class OutboundSmsApiTest {
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
    public void unsupportedHttpMethodRequestToOutboundSmsApiShouldReturnHttp405(){
        int statusCodeValue = restTemplate.getForEntity("http://localhost:"+port+"/outbound/sms/",
                String.class).getStatusCodeValue();
        Assertions.assertEquals(405, statusCodeValue);
    }

    @Test
    public void outboundSmsApiWrongAuthenticationShouldReturnHttp403() throws JsonProcessingException {
        Map<String, Object> request = util.buildRequest("968663934","3839285848","Hello World");
        ResponseEntity<String> response = util.sendPost(request,"http://localhost:"+port+"/outbound/sms/","user","password");
        Assertions.assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    public void outboundSmsApifromParameterlessthanSixCharacters() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("968663934","848","Hello World");
        String response = util.sendPost(request,"http://localhost:"+port+"/outbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("from is invalid", res.getString("error"));
    }

    @Test
    public void outboundSmsApifromParameterMorethanSixteenCharacters() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("968663934","8489999999999999999999999992222222222","Hello World");
        String response = util.sendPost(request,"http://localhost:"+port+"/outbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("from is invalid", res.getString("error"));
    }

    @Test
    public void outboundSmsApiToParameterlessthanSixCharacters() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("9999","8499999998","Hello World");
        String response = util.sendPost(request,"http://localhost:"+port+"/outbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("to is invalid", res.getString("error"));
    }

    @Test
    public void outboundSmsApiToParameterMorethanSixteenCharacters() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("9399994443333334987653322288888888888834","8993322228448222","Hello World");
        String response = util.sendPost(request,"http://localhost:"+port+"/outbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("to is invalid", res.getString("error"));
    }

    @Test
    public void outboundSmsApiTextParameterlessthanOneCharacters() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("998899999999","8499999998","");
        String response = util.sendPost(request,"http://localhost:"+port+"/outbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("text is invalid", res.getString("error"));
    }

    @Test
    public void outboundSmsApiTextParameterMorethan120Characters() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("76588888834","8993322228448222",
                "Hello world Hello worldHello worldHello worldHello worldHello " +
                        "worldHello worldHello worldHello worldHello worldHello world" +
                        "Hello worldHello worldHello worldHello worldHello worldHello worldHello worldHello worldHello world"
        );
        String response = util.sendPost(request,"http://localhost:"+port+"/outbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("text is invalid", res.getString("error"));
    }

    @Test
    public void outboundSmsApiTextParameterIsMissing() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("99988484849","8499999998",null);
        String response = util.sendPost(request,"http://localhost:"+port+"/outbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("text is missing", res.getString("error"));
    }

    @Test
    public void outboundSmsApiToParameterIsMissing() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest(null,"8499999998","Hello World");
        String response = util.sendPost(request,"http://localhost:"+port+"/outbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("to is missing", res.getString("error"));
    }

    @Test
    public void outboundSmsApiFromParameterIsMissing() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("99988484849",null,"Hello World");
        String response = util.sendPost(request,"http://localhost:"+port+"/outbound/sms/","user","password").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("from is missing", res.getString("error"));
    }

    @Test
    public void outboundSmsApiFromParameterNotFoundForAccountAuthenicated() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("99988484849","858476355363","Hello Brother");
        String response = util.sendPost(request,"http://localhost:"+port+"/outbound/sms/","azr1","20S0KPNOIM").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("from parameter not found", res.getString("error"));
    }

    @Test
    public void outboundSmSApiBlockRequest() throws JsonProcessingException, JSONException{
        String from = "441224459426";
        String to = "99988484849";
        String key = smsService.joinFromAndToPhoneNumber(from,to);
        redisTemplate.opsForValue().set(key,"STOP");
        redisTemplate.expire(key,4L, TimeUnit.HOURS);
        Map<String, Object> request = util.buildRequest(to,from,"Hello Brother");
        String response = util.sendPost(request,"http://localhost:"+port+"/outbound/sms/","azr1","20S0KPNOIM").getBody();
        JSONObject res = new JSONObject(response);
        String errorMsg = String.format("sms from %s to %s blocked by STOP request",from,to);
        Assertions.assertEquals(errorMsg, res.getString("error"));
    }

    @Test
    public void outboundSmsShouldLimitRateFromFromPhoneNumberTo50RequestsPerDay() throws JsonProcessingException, org.json.JSONException {
        String to ="85847622363";
        String from = "3253280312";
        Map<String, Object> request = util.buildRequest(to,from,"Hello Brother");
        String response = null;
        for (int i = 0; i < 60; i++) {
           response = util.sendPost(request,"http://localhost:"+port+"/outbound/sms/","azr1","20S0KPNOIM").getBody();
        }
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals(String.format("limit reached for from %s",from), res.getString("error"));
    }

    @Test
    public void outboundSmsApiSuccessfulInboundSms() throws JsonProcessingException, org.json.JSONException {
        Map<String, Object> request = util.buildRequest("858476355363","3253280311","Hello Brother");
        String response = util.sendPost(request,"http://localhost:"+port+"/outbound/sms/","azr1","20S0KPNOIM").getBody();
        JSONObject res = new JSONObject(response);
        Assertions.assertEquals("outbound sms ok", res.getString("message"));
    }
}
