package com.bluehospital.patient.patient.controller.patient.Public;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/patient")
public class PatientHealthCheck {

    //method to check the health of api
    @GetMapping("/healthcheck")
    public ResponseEntity<?> healthcheck(){
        return new ResponseEntity<>("Ok", HttpStatus.OK);
    }
}
