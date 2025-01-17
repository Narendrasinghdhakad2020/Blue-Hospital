package com.bluehospital.patient.patient.service.hospital;

import com.bluehospital.patient.patient.dto.common.ApiResponse;
import com.bluehospital.patient.patient.dto.common.LoginRequest;
import com.bluehospital.patient.patient.model.hospital.Hospital;
import org.bson.types.ObjectId;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface HospitalService {
    public Hospital loadHospitalByUsername(String username);
    public Hospital loadHospitalById(ObjectId id);
    public void saveHospital(Hospital hospital);
    public boolean isHospitalExistByUsername(String username);
    public boolean isHospitalExistById(ObjectId id);
    public String authLoginCredentialAndGenerateAccessToken(LoginRequest request, AuthenticationManager authenticationManager);
    public String generateRefreshToken(String username);
    public ApiResponse<Map<String,String>> validateRefreshTokenAndGenerateNewAccessToken(String refreshToken, String accessToken);
    public void updateEmailVerificationStatus(String username,boolean isVerified);
    public boolean logoutHospital(String accessToken,String refreshToken);
}
