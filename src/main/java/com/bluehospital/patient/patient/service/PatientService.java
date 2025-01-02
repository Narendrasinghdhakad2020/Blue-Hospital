package com.bluehospital.patient.patient.service;

import com.bluehospital.patient.patient.dto.LoginRequest;
import com.bluehospital.patient.patient.model.Patient;
import org.bson.types.ObjectId;
import org.springframework.security.authentication.AuthenticationManager;

public interface PatientService {
    Patient loadPatientByUsername(String username);
    Patient loadPatientById(ObjectId id);
    void savePatient(Patient patient);
    boolean isPatientExistByUsername(String username);
    boolean isPatientExistsById(ObjectId id);
    String loginAndGenerateToken(LoginRequest request, AuthenticationManager authenticationManager);
    void updateEmailVerificationStatus(String username,boolean isVerified);
}
