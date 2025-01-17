package com.bluehospital.patient.patient.controller.hospital.Public;

import com.bluehospital.patient.patient.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/hospital")
public class HospitalHealthCheck {

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck(){

        ApiResponse<String> response=new ApiResponse<>(
                HttpStatus.OK.value(),
                "Server is running smoothly",
                "/api/v1/public/hospital/health",
                "Your server is Up"
        );
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
