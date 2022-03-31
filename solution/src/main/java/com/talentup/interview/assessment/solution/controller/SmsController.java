package com.talentup.interview.assessment.solution.controller;

import com.talentup.interview.assessment.solution.apiModels.SmsRequest;
import com.talentup.interview.assessment.solution.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class SmsController {

    @Autowired
    private SmsService smsService;

    @PostMapping("/inbound/sms/")
    public ResponseEntity inboundSms(HttpServletRequest request, @RequestBody @Valid SmsRequest smsRequest) {
        return smsService.inboundSms(request, smsRequest);
    }

    @PostMapping("/outbound/sms/")
    public ResponseEntity outboundSms(HttpServletRequest request, @RequestBody @Valid SmsRequest smsRequest) {
        return smsService.outboundSms(request, smsRequest);
    }
}
