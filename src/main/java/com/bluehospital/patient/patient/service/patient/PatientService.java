package com.bluehospital.patient.patient.service.patient;

import com.bluehospital.patient.patient.dto.common.ApiResponse;
import com.bluehospital.patient.patient.dto.common.LoginRequest;
import com.bluehospital.patient.patient.model.patient.Patient;
import org.bson.types.ObjectId;
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
