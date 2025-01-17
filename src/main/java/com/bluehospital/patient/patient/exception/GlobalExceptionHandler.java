package com.bluehospital.patient.patient.exception;

import com.bluehospital.patient.patient.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAllException(Exception ex, WebRequest request){

        ApiResponse<Object> response=new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=",""),
                null
        );
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
