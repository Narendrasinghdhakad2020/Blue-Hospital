package com.bluehospital.patient.patient.controller.patient.Public;

import com.bluehospital.patient.patient.dto.common.ApiResponse;
import com.bluehospital.patient.patient.dto.common.LoginRequest;
import com.bluehospital.patient.patient.dto.patient.PatientSignupRequest;
import com.bluehospital.patient.patient.dto.common.VerificationRequest;
import com.bluehospital.patient.patient.model.patient.Patient;
import com.bluehospital.patient.patient.service.common.EmailService;
import com.bluehospital.patient.patient.service.patient.PatientService;
import com.bluehospital.patient.patient.service.common.TokenBlacklistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.*;

@RestController
@RequestMapping("/api/v1/public/patient")
public class PatientAuth {

    private static final Logger logger = LoggerFactory.getLogger(PatientAuth.class);

    private final AuthenticationManager authenticationManager;
    private final PatientService patientService;
    private final EmailService emailService;
    private final TokenBlacklistService tokenBlacklistService;
    private final PasswordEncoder passwordEncoder;

    private static String accessToken;
    private static String refreshToken;

    public PatientAuth(AuthenticationManager authenticationManager, PatientService patientService, EmailService emailService, TokenBlacklistService tokenBlacklistService, PasswordEncoder passwordEncoder
    ){
        this.authenticationManager=authenticationManager;
        this.patientService=patientService;
        this.emailService=emailService;
        this.tokenBlacklistService=tokenBlacklistService;
        this.passwordEncoder=passwordEncoder;
    }

    //method to generate code
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> addPatient(@RequestBody PatientSignupRequest request){

        logger.info("Signup-Controller: adding new patient to DB!");
        boolean isPatientExist=patientService.isPatientExistByUsername(request.getUsername());//To check patient already exist
        Patient newPatient=new Patient();
        if(!isPatientExist){
            String encodedPassword=passwordEncoder.encode(request.getPassword());//encoding user password for security reason and saving it to DB
            newPatient.setName(request.getName());
            newPatient.setUsername(request.getUsername());
            newPatient.setPassword(encodedPassword);
            newPatient.setRole(request.getRole());
            newPatient.setPhone(request.getPhone());
            newPatient.setGender(request.getGender());
            newPatient.setCreatedAt(new Date());
            patientService.savePatient(newPatient);
            logger.info("Signup-Controller: Patient Successfully saved to DB!");
            ApiResponse<Patient> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Patient Successfully registerd",
                    "/api/v1/public/patient/signup",
                    newPatient
            );
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        else{
            logger.info("User already exist");
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Patient Already Exist!",
                    "api/v1/public/patient/signup",
                    ""
            );
            return new ResponseEntity<>( response,HttpStatus.BAD_REQUEST);

        }

    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String,String>>> loginPatient(@RequestBody LoginRequest request){
            System.out.println("Controller: Login patient");
            Patient patient = patientService.loadPatientByUsername(request.getUsername());
            if(patient!=null) {
                accessToken = patientService.authLoginCredentialAndGenerateAccessToken(request, authenticationManager);
                if (accessToken != null) {
                    // Generate refresh token
                    refreshToken = patientService.generateRefreshToken(request.getUsername());

                    //check if patient is Verified
                    if (!patient.isVerified()) {
                        String code = generateVerificationCode();
                        System.out.println("Code to save: " + code);
                        patient.setVerificationCode(code);
                        patient.setUpdatedAt(new Date());
                        patientService.savePatient(patient);

                            try{

                            emailService.sendVerificationEmail(patient.getUsername(), code);
                            //sending email response to the server that email has successfully sended

                                ApiResponse<Map<String,String>> response= new ApiResponse<>(
                                        HttpStatus.NO_CONTENT.value(),
                                        "Redirecting to Verify page",
                                        "/api/v1/public/patient/login",
                                        null
                                );
                                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
                            }catch (Exception ex){
                                logger.error("Error in sending email");
                                ApiResponse<Map<String,String>> response= new ApiResponse<>(
                                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                        "Error in sending Email for verification",
                                        "/api/v1/public/patient/login",
                                        null
                                );
                                return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                    }

                    //creating a response for login request
                    Map<String, String> tokens=new HashMap<>();
                    tokens.put("accessToken",accessToken);
                    tokens.put("refreshToken",refreshToken);

                    ApiResponse<Map<String,String>> response = new ApiResponse<>(
                            HttpStatus.OK.value(),
                            "Login Successful",
                            "/api/v1/public/patient/login",
                            tokens

                    );
                    return new ResponseEntity<>(response, HttpStatus.OK);//when patient is successfully login

                } else {
                    logger.error("Patient credential are not Correct");
                    ApiResponse<Map<String,String>> response = new ApiResponse<>(
                            HttpStatus.UNAUTHORIZED.value(),
                            "Incorrect patientName or Password",
                            "/api/v1/public/patient/login",
                            null
                    );
                    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
                }
            }
            else{
                logger.error("Patient Not Found in DB please check your email ID");
                ApiResponse<Map<String,String>> response = new ApiResponse<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Wrong email or password",
                        "/api/v1/public/patient/login",
                        null
                );
                return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
            }
    }

    //method to logout patient
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Map<String,String>>> logoutPatient(@RequestBody Map<String,String> request){
        try{
            String accessToken=request.get("accessToken");
            String refreshToken = request.get("refreshToken");

            System.out.println("Hello i am in Logout Controller");

            if(patientService.logoutPatient(accessToken,refreshToken)){
                ApiResponse<Map<String,String>> response = new ApiResponse<>(
                        HttpStatus.OK.value(),
                        "SuccessFully logout patient",
                        "/api/v1/public/patient/logout",
                        null
                );
                return new ResponseEntity<>(response,HttpStatus.OK);
            }
            else{
                ApiResponse<Map<String,String>> response = new ApiResponse<>(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Error in logout in patient service",
                        "/api/v1/public/patient/logout",
                        null
                );
                return  new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

            }

        }catch (Exception ex){
            logger.error("Error in logouting the patient");
            ApiResponse<Map<String,String>> response= new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error in logout",
                    "/api/v1/public/patient/logout",
                    null
            );
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //making controller to refresh token
    @PostMapping("refresh-token")
    public ResponseEntity<ApiResponse<Map<String,String>>> refreshAccessToken(@RequestBody Map<String,String> request){

        try {
            String refreshToken=request.get("refreshToken");
            String accessToken=request.get("accessToken");
            if(tokenBlacklistService.isTokenBlacklisted(accessToken)){
                logger.info("AccessToken is already blacklisted please login again");
                ApiResponse<Map<String,String>> response = new ApiResponse<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Please login again",
                        "/api/v1/public/patient/refresh-token",
                        null
                );
                return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
            }
            ApiResponse<Map<String,String>> response = patientService.validateRefreshTokenAndGenerateNewAccessToken(refreshToken,accessToken);

            int statusCode=response.getStatusCode();


            if(statusCode == 200){
                return new ResponseEntity<>(response,HttpStatus.OK);
            }

            return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);

        }catch (Exception ex){
            logger.error("Error refreshing token", ex);
            ApiResponse<Map<String, String>> response = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error refreshing token",
                    "/api/v1/public/patient/refresh-token",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    //making controller for verifying email
    @PostMapping("/verify")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String,String>>> verifyPatientEmail(@RequestBody VerificationRequest request){

        try{
            Patient patient=patientService.loadPatientByUsername(request.getUsername());
            if(patient!=null) {
                logger.info("Code is: " + patient.getVerificationCode());
                if ( patient.getVerificationCode().equals(request.getVerificationCode())) {
                    patientService.updateEmailVerificationStatus(request.getUsername(), true);
                    //creating map to store access and refresh token
                    Map<String,String> tokens=new HashMap<>();
                    tokens.put("accessToken",accessToken);
                    tokens.put("refreshToken",refreshToken);
                    ApiResponse<Map<String,String>> response = new ApiResponse<>(
                            HttpStatus.OK.value(),
                            "Email Successfully verified!",
                            "/api/v1/public/patient/verify",
                            tokens
                    );
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                ApiResponse<Map<String,String>> response = new ApiResponse<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Invalid Verification Code",
                        "/api/v1/public/patient/verify",
                        null
                );
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            else{
                ApiResponse<Map<String,String> > response = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                        "Invalid Email enter the same as entered on login page",
                        "/api/v1/public/patient/verify",
                        null
                        );
                return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception e){
            logger.info("Error in verifying email"+e.getMessage());
            ApiResponse<Map<String,String> > response = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Error in verify email",
                    "/api/v1/public/patient/verify",
                    null
            );
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }



    }

}
