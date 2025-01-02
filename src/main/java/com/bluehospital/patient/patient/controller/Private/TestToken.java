package com.bluehospital.patient.patient.controller.Private;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/private/patient")
public class TestToken {


    @GetMapping("/test")
    public ResponseEntity<?> testToken(){
        return null;
    }
}
