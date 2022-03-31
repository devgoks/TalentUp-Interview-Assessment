package com.talentup.interview.assessment.solution.service;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.talentup.interview.assessment.solution.apiModels.SmsRequest;
import com.talentup.interview.assessment.solution.model.Account;
import com.talentup.interview.assessment.solution.repository.AccountRepository;
import com.talentup.interview.assessment.solution.repository.PhoneNumberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SmsService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PhoneNumberRepository phoneNumberRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String FROM_PHONE_NUMBER_COUNTER_REDIS_PREFIX = "fromPhoneNumberCounter";

    private static final int MAXIMUM_OUTBOUND_API_REQUESTS = 50;

    public ResponseEntity inboundSms(HttpServletRequest request,SmsRequest smsRequest){
        Account account = checkAuthUsernameAndPassword(request);

        if (account == null) return new ResponseEntity(HttpStatus.FORBIDDEN);

        if (!phoneNumberRepository.existsByNumberAndAccountId(smsRequest.getTo(),account.getId())) {
            return composeResponse("", "to parameter not found", HttpStatus.BAD_REQUEST);
        }

        Pattern pattern = Pattern.compile("STOP|STOP\\n|STOP\\r|STOP\\r\\n");
        Matcher matcher = pattern.matcher(smsRequest.getText());
        if(matcher.matches()){
            String key = joinFromAndToPhoneNumber(smsRequest.getFrom(),smsRequest.getTo());
            redisTemplate.opsForValue().set(key,smsRequest.getText());
            redisTemplate.expire(key,4L, TimeUnit.HOURS);
        }
        return composeResponse("inbound sms ok","",HttpStatus.OK);
    }

    public ResponseEntity outboundSms(HttpServletRequest request,SmsRequest smsRequest){

        Account account = checkAuthUsernameAndPassword(request);

        if (account == null) return new ResponseEntity(HttpStatus.FORBIDDEN);

        String key = joinFromAndToPhoneNumber(smsRequest.getFrom(),smsRequest.getTo());
        if (redisTemplate.hasKey(key)){
            String errMsg =String.format("sms from %s to %s blocked by STOP request",smsRequest.getFrom(),smsRequest.getTo());
            return composeResponse("",errMsg,HttpStatus.BAD_REQUEST);
        }

        if (!phoneNumberRepository.existsByNumberAndAccountId(smsRequest.getFrom(),account.getId())) {
            return composeResponse("", "from parameter not found", HttpStatus.BAD_REQUEST);
        }
        String fromPhoneNumberCounterKey = FROM_PHONE_NUMBER_COUNTER_REDIS_PREFIX.concat(smsRequest.getFrom());
        if (redisTemplate.hasKey(fromPhoneNumberCounterKey)){
            String fromPhoneNumberCounter = redisTemplate.opsForValue().get(fromPhoneNumberCounterKey).toString();
            if(Integer.valueOf(fromPhoneNumberCounter) >= MAXIMUM_OUTBOUND_API_REQUESTS){
                return composeResponse("", String.format("limit reached for from %s",smsRequest.getFrom()), HttpStatus.BAD_REQUEST);
            }
            redisTemplate.opsForValue().increment(fromPhoneNumberCounterKey,1);
        }else{
            redisTemplate.opsForValue().increment(fromPhoneNumberCounterKey,1);
            redisTemplate.expire(fromPhoneNumberCounterKey,1L, TimeUnit.DAYS);
        }
        return composeResponse("outbound sms ok","",HttpStatus.OK);
    }

    private Account checkAuthUsernameAndPassword(HttpServletRequest request) {
        final String authHead = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHead == null)  return null;

        byte[] e = Base64.decode(authHead.substring(6));
        String usernameAndPassword = new String(e);
        String username = usernameAndPassword.substring(0, usernameAndPassword.indexOf(":"));
        String password = usernameAndPassword.substring(usernameAndPassword.indexOf(":") + 1);

        return accountRepository.findByUsernameAndAuthId(username,password);
    }

    private ResponseEntity composeResponse(String message, String error, HttpStatus httpStatus){
        Map<String,String> response = new HashMap<>();
        response.put("error",error);
        response.put("message",message);
        return new ResponseEntity<>(response, null, httpStatus);
    }

    private String joinFromAndToPhoneNumber(String from, String to){
        return from.concat("_").concat(to);
    }
}
