package com.bluehospital.patient.patient.controller.patient.Private;

import com.bluehospital.patient.patient.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/private/patient")
public class TestToken {


    @GetMapping("/test")
    public ResponseEntity<ApiResponse<Map<String,String>>> testToken(){

        ApiResponse<Map<String,String>> response= new ApiResponse<>(
                HttpStatus.OK.value(),
                "Welcome to private test route" ,
                "/api/v1/private/patient",
                null

        );
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
