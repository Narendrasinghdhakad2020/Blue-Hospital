package com.bluehospital.patient.patient.service.hospital;

import com.bluehospital.patient.patient.dto.common.ApiResponse;
import com.bluehospital.patient.patient.dto.common.LoginRequest;
import com.bluehospital.patient.patient.model.hospital.Hospital;
import com.bluehospital.patient.patient.model.patient.Patient;
import com.bluehospital.patient.patient.repository.hospital.HospitalRepository;
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
public class HospitalServiceImp implements HospitalService {

    private static final Logger logger= LoggerFactory.getLogger(HospitalServiceImp.class);

    private final HospitalRepository hospitalRepository;
    private final JwtUtils jwtUtils;
    private final MongoTemplate mongoTemplate;
    private final TokenBlacklistService tokenBlacklistService;

    //constructor to initialize the final variable values
    public HospitalServiceImp(HospitalRepository hospitalRepository,JwtUtils jwtUtils,MongoTemplate mongoTemplate,TokenBlacklistService tokenBlacklistService){
        this.hospitalRepository=hospitalRepository;
        this.jwtUtils=jwtUtils;
        this.mongoTemplate=mongoTemplate;
        this.tokenBlacklistService=tokenBlacklistService;
    }

    //loading hospital from database using hospital email/username
    @Override
    public Hospital loadHospitalByUsername(String username){
        //try catch is globally handled by GlobalExceptionHandler
        logger.info("Hospital-Service: Loading Hospital using hospital name/email from DB!");
        Hospital hospital = hospitalRepository.findHospitalByUsername(username).orElse(null);
        if(hospital!=null){
            return hospital;
        }
        return null;
    }

    //loading hospital from database using hospital id
    @Override
    public Hospital loadHospitalById(ObjectId id){
        logger.info("Hospital-Service: Loading  hospital using Id from DB!");
        return hospitalRepository.findHospitalById(id).orElse(null);
    }

    //saving hospital in database
    @Override
    public void saveHospital(Hospital hospital){
        logger.info("Hospital-Service: Saving hospital to DB!");
        hospitalRepository.save(hospital);
    }

    //check hospital already exist or not using username
    @Override
    public boolean isHospitalExistByUsername(String username){
        logger.info("Hospital-Service: check for hospital existence using username in DB!");
        return hospitalRepository.existsHospitalByUsername(username);
    }

    //check hospital already exist or not using id
    @Override
    public boolean isHospitalExistById(ObjectId id){
        logger.info("Hospital-Service: check for hospital existence using id in Db!");
        return hospitalRepository.existsHospitalById(id);
    }

    //method to authenticate login credential  and generate access jwt token
    @Override
    public String authLoginCredentialAndGenerateAccessToken(LoginRequest request, AuthenticationManager authenticationManager){

        logger.info("Hospital-Service: login hospital and generate token!");
        UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword());
        logger.info("Hospital-Service: Temporary auth token for given hospital and password is created Successfully!");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
        logger.info("Hospital-Service: Successfully authenticated! the requested hospital");
        Hospital hospital=hospitalRepository.findHospitalByUsername(request.getUsername()).orElse(null);

        if(!hospital.getUsername().isEmpty()){

        return jwtUtils.generateAccessToken(hospital.getUsername());
        }
        else{
            return "Error in generating token for hospital because the username for given hospital is Null";
        }
    }

    //method to generate refresh token
    @Override
    public String generateRefreshToken(String username){
        logger.info("Hospital-Service: Generating refresh token for the hospital!");
        return jwtUtils.generateRefreshToken(username);
    }

    //method to validate refresh token and generate new access token
    @Override
    public ApiResponse<Map<String,String>> validateRefreshTokenAndGenerateNewAccessToken(String refreshToken, String accessToken){
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
                    "/api/v1/public/hospital/refresh-token",
                    tokens
            );
        }
        else{
            return new  ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid or expired refresh token",
                    "/api/v1/public/hospital/refresh-token",
                    null
            );
        }
    }


    //method to update the VerificationStatus of Hospital
    @Override
    public void updateEmailVerificationStatus(String username,boolean isVerified){

        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));

        Update update = new Update();
        update.set("isVerified",isVerified);//updating the isVerified field of hospital
        update.set("verificationCode",null);

        mongoTemplate.updateFirst(query,update,Hospital.class);

    }

    //method to logout hospital
    @Override
    public boolean logoutHospital(String accessToken,String refreshToken){
        logger.info("Hello we are here in Hospital service");
        Long refreshTokenExpiry=jwtUtils.extractExpiration(refreshToken).getTime();
        //Now blacklist the access token
        tokenBlacklistService.blacklistToken(accessToken,refreshTokenExpiry);
        return true;
    }


}
