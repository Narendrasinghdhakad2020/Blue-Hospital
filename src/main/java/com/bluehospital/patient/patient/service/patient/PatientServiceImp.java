package com.bluehospital.patient.patient.service.patient;

import com.bluehospital.patient.patient.dto.common.ApiResponse;
import com.bluehospital.patient.patient.dto.common.LoginRequest;
import com.bluehospital.patient.patient.model.patient.Patient;
import com.bluehospital.patient.patient.repository.patient.PatientRepository;
import com.bluehospital.patient.patient.service.common.TokenBlacklistService;
import com.bluehospital.patient.patient.utils.JwtUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PatientServiceImp implements PatientService {

    private static final Logger logger= LoggerFactory.getLogger(PatientServiceImp.class);

    private final PatientRepository patientRepository;
    private final JwtUtils jwtUtils;
    private final MongoTemplate mongoTemplate;
    private final TokenBlacklistService tokenBlacklistService;


    //constructor injection
    public PatientServiceImp(PatientRepository patientRepository, JwtUtils jwtUtils, MongoTemplate mongoTemplate,TokenBlacklistService tokenBlacklistService){
        this.patientRepository=patientRepository;
        this.jwtUtils=jwtUtils;
        this.mongoTemplate=mongoTemplate;
        this.tokenBlacklistService=tokenBlacklistService;
    }

    //loading patient from database using patient email/username
    @Override
    public Patient loadPatientByUsername(String username){
        //try catch is globally handled by GlobalExceptionHandler
            logger.info("Patient-Service: Loading patient using patient name/email from DB!");
            Patient patient = patientRepository.findPatientByUsername(username).orElse(null);
            if(patient!=null){
                return patient;
            }
            return null;


    }
    //loading patient from database usingn patient id
    @Override
    public Patient loadPatientById(ObjectId id){
        logger.info("Patient-Service: Loading  patient using Id from DB!");
        return patientRepository.findPatientById(id).orElse(null);
    }


    //saving patient in database
    @Override
    public void savePatient(Patient patient){
        logger.info("Patient-Service: Saving patient to DB!");
        patientRepository.save(patient);
    }

    //check patient already exist or not using username
    @Override
    public boolean isPatientExistByUsername(String username){
        logger.info("Patient-Service: check for patient existence using username in DB!");
        return patientRepository.existsPatientByUsername(username);
    }

    //check patient already exists or not using id of patient
    @Override
    public boolean isPatientExistsById(ObjectId id){
        logger.info("Patient-Service: check for patient existence using id in DB!");
        return patientRepository.existsPatientById(id);
    }

    //method to authenticate login credential  and generate access jwt token
    @Override
    public String authLoginCredentialAndGenerateAccessToken(LoginRequest request, AuthenticationManager authenticationManager){

        logger.info("Patient-Service: login patient and generate token!");
        UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword());
        logger.info("Patient-Service: Temporary auth token for given patient and password is created Successfully!");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
        logger.info("Patient-Service: Successfully authenticated! the requested patient");
        Patient patient=patientRepository.findPatientByUsername(request.getUsername()).orElse(null);

        if(!patient.getUsername().isEmpty()){

        return jwtUtils.generateAccessToken(patient.getUsername());
        }
        else {
            return "Error in generating token to request patient because the username for given patient is Null";
        }
    }

    //method to generate refresh token
    @Override
    public String generateRefreshToken(String username){
        logger.info("Patient-Service: Generating refresh token for the patient!");
        return jwtUtils.generateRefreshToken(username);
    }

    //method to validate refresh token and generate new access token
    @Override
    public ApiResponse<Map<String,String>> validateRefreshTokenAndGenerateNewAccessToken(String refreshToken,String accessToken){
        String username=jwtUtils.extractUsername(refreshToken);
        if(jwtUtils.validateToken(refreshToken,username)){

            String newAccessToken= jwtUtils.generateAccessToken(username);//NEW ACCESS TOKEN

            Map<String,String> tokens= new HashMap<>();
            tokens.put("accessToken",newAccessToken);
            tokens.put("refreshToken",refreshToken);

            //Now blacklist the old access token
            tokenBlacklistService.blacklistToken(accessToken,jwtUtils.extractExpiration(refreshToken).getTime());


            //api response
            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Access token refreshed successfully",
                    "/api/v1/public/patient/refresh-token",
                    tokens
            );


        }
        else{

            return new  ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid or expired refresh token",
                    "/api/v1/public/patient/refresh-token",
                    null
            );


        }
    }

    //method to update the VerificationStatus of Patient
    @Override
    public void updateEmailVerificationStatus(String username,boolean isVerified){

        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));

        Update update = new Update();
        update.set("isVerified",isVerified);//updating the isVerified field of patient
        update.set("verificationCode",null);

        mongoTemplate.updateFirst(query,update,Patient.class);

    }

    //method to logout patient
    @Override
    public boolean logoutPatient(String accessToken,String refreshToken){
        System.out.println("Hello we are here in patient service");
        Long refreshTokenExpiry=jwtUtils.extractExpiration(refreshToken).getTime();
        //Now blacklist the access token
        tokenBlacklistService.blacklistToken(accessToken,refreshTokenExpiry);
        return true;
    }







}
