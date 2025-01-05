package com.bluehospital.patient.patient.service;

import com.bluehospital.patient.patient.dto.ApiResponse;
import com.bluehospital.patient.patient.dto.LoginRequest;
import com.bluehospital.patient.patient.model.Patient;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Map;

public interface PatientService {
    Patient loadPatientByUsername(String username);
    Patient loadPatientById(ObjectId id);
    void savePatient(Patient patient);
    boolean isPatientExistByUsername(String username);
    boolean isPatientExistsById(ObjectId id);
    String authLoginCredentialAndGenerateAccessToken(LoginRequest request, AuthenticationManager authenticationManager);
    String generateRefreshToken(String username);
    ApiResponse<Map<String,String>> validateRefreshTokenAndGenerateNewAccessToken(String refreshToken,String accessToken);
    void updateEmailVerificationStatus(String username,boolean isVerified);
    public boolean logoutPatient(String accessToken,String refreshToken);//to logout patient and blacklist the access token
}
