package com.bluehospital.patient.patient.controller.slot.Public;

import com.bluehospital.patient.patient.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/public/slots")
public class SlotHealthCheck {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse> healthCheck(){
        ApiResponse<Map<String,String>> response= new ApiResponse<>(
                HttpStatus.OK.value(),
                "Welcome to slots route of Blue Hospital!",
                "/api/v1/public/slots",
                null

        );
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
