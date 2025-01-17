package com.bluehospital.patient.patient.controller.hospital.Public;

import com.bluehospital.patient.patient.dto.common.ApiResponse;
import com.bluehospital.patient.patient.dto.common.LoginRequest;
import com.bluehospital.patient.patient.dto.common.VerificationRequest;
import com.bluehospital.patient.patient.dto.hospital.HospitalSignupRequest;
import com.bluehospital.patient.patient.dto.patient.PatientSignupRequest;
import com.bluehospital.patient.patient.model.hospital.Hospital;
import com.bluehospital.patient.patient.model.patient.Patient;
import com.bluehospital.patient.patient.service.common.EmailService;
import com.bluehospital.patient.patient.service.common.TokenBlacklistService;
import com.bluehospital.patient.patient.service.hospital.HospitalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/public/hospital")
public class HospitalAuth {

    private static final Logger logger= LoggerFactory.getLogger(HospitalAuth.class);

    private final AuthenticationManager authenticationManager;
    private final HospitalService hospitalService;
    private final EmailService emailService;
    private final TokenBlacklistService tokenBlacklistService;
    private final PasswordEncoder passwordEncoder;

    private static String accessToken;
    private static String refreshToken;

    //constructor initialize the final variables
    public HospitalAuth(AuthenticationManager authenticationManager,HospitalService hospitalService,EmailService emailService,TokenBlacklistService tokenBlacklistService,PasswordEncoder passwordEncoder){
        this.authenticationManager=authenticationManager;
        this.hospitalService=hospitalService;
        this.emailService=emailService;
        this.tokenBlacklistService=tokenBlacklistService;
        this.passwordEncoder=passwordEncoder;
    }

    //method to generate code
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    //controller to signup hospital
    @PostMapping("/signup")
    public ResponseEntity<?> addHospital(@RequestBody HospitalSignupRequest request){

        logger.info("Signup-Controller: adding new hospital to DB!");
        boolean isHospitalExist=hospitalService.isHospitalExistByUsername(request.getUsername());//To check hospital already exist
        Hospital newHospital=new Hospital();
        if(!isHospitalExist){
            String encodedPassword=passwordEncoder.encode(request.getPassword());//encoding user password for security reason and saving it to DB
            newHospital.setName(request.getName());
            newHospital.setUsername(request.getUsername());
            newHospital.setPassword(encodedPassword);
            newHospital.setRole(request.getRole());
            newHospital.setPhone(request.getPhone());
            newHospital.setCreatedAt(new Date());
            hospitalService.saveHospital(newHospital);
            logger.info("Signup-Controller: Hospital Successfully saved to DB!");
            ApiResponse<Hospital> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Hospital Successfully registerd",
                    "/api/v1/public/hospital/signup",
                    newHospital
            );
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        else{
            logger.info("User already exist");
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Hospital Already Exist!",
                    "api/v1/public/hospital/signup",
                    ""
            );
            return new ResponseEntity<>( response,HttpStatus.BAD_REQUEST);

        }

    }


    //login controller for hospital
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String,String>>> loginHospital(@RequestBody LoginRequest request){
       logger.info("Controller: Login Hospital");
        Hospital hospital = hospitalService.loadHospitalByUsername(request.getUsername());
        if(hospital!=null) {
            accessToken = hospitalService.authLoginCredentialAndGenerateAccessToken(request, authenticationManager);
            if (accessToken != null) {
                // Generate refresh token
                refreshToken = hospitalService.generateRefreshToken(request.getUsername());

                //check if hospital is Verified
                if (!hospital.isVerified()) {
                    String code = generateVerificationCode();
                   logger.info("Code to save: " + code);
                    hospital.setVerificationCode(code);
                    hospital.setUpdatedAt(new Date());
                    hospitalService.saveHospital(hospital);

                    try{

                        emailService.sendVerificationEmail(hospital.getUsername(), code);
                        //sending email response to the server that email has successfully sended

                        ApiResponse<Map<String,String>> response= new ApiResponse<>(
                                HttpStatus.NO_CONTENT.value(),
                                "Redirecting to Verify page",
                                "/api/v1/public/hospital/login",
                                null
                        );
                        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
                    }catch (Exception ex){
                        logger.error("Error in sending email");
                        ApiResponse<Map<String,String>> response= new ApiResponse<>(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Error in sending Email for verification",
                                "/api/v1/public/hospital/login",
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
                        "/api/v1/public/hospital/login",
                        tokens

                );
                return new ResponseEntity<>(response, HttpStatus.OK);//when hospital is successfully login

            } else {
                logger.error("Hospital credential are not Correct");
                ApiResponse<Map<String,String>> response = new ApiResponse<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Incorrect hospitalName or Password",
                        "/api/v1/public/hospital/login",
                        null
                );
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        }
        else{
            logger.error("Hospital Not Found in DB please check your email ID");
            ApiResponse<Map<String,String>> response = new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Wrong email or password",
                    "/api/v1/public/hospital/login",
                    null
            );
            return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
        }
    }

    //method to logout hospital
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Map<String,String>>> logoutHospital(@RequestBody Map<String,String> request){
        try{
            String accessToken=request.get("accessToken");
            String refreshToken = request.get("refreshToken");

            System.out.println("Hello i am in Logout Controller");
            boolean flag=hospitalService.logoutHospital(accessToken,refreshToken);
            System.out.println("Value is: "+flag);
            System.out.println("Hello i am here");
            if(hospitalService.logoutHospital(accessToken,refreshToken)){
                ApiResponse<Map<String,String>> response = new ApiResponse<>(
                        HttpStatus.OK.value(),
                        "SuccessFully logout hospital",
                        "/api/v1/public/hospital/logout",
                        null
                );
                return new ResponseEntity<>(response,HttpStatus.OK);
            }
            else{
                ApiResponse<Map<String,String>> response = new ApiResponse<>(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Error in logout in hospital service",
                        "/api/v1/public/hospital/logout",
                        null
                );
                return  new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

            }

        }catch (Exception ex){
            logger.error("Error in logouting the hospital");
            ApiResponse<Map<String,String>> response= new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error in logout",
                    "/api/v1/public/hospital/logout",
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
                        "/api/v1/public/hospital/refresh-token",
                        null
                );
                return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
            }
            ApiResponse<Map<String,String>> response = hospitalService.validateRefreshTokenAndGenerateNewAccessToken(refreshToken,accessToken);

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
                    "/api/v1/public/hospital/refresh-token",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    //making controller for verifying email
    @PostMapping("/verify")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String,String>>> verifyHospitalEmail(@RequestBody VerificationRequest request){

        try{
            Hospital hospital=hospitalService.loadHospitalByUsername(request.getUsername());
            if(hospital!=null) {
                logger.info("Code is: " + hospital.getVerificationCode());
                if ( hospital.getVerificationCode().equals(request.getVerificationCode())) {
                    hospitalService.updateEmailVerificationStatus(request.getUsername(), true);
                    //creating map to store access and refresh token
                    Map<String,String> tokens=new HashMap<>();
                    tokens.put("accessToken",accessToken);
                    tokens.put("refreshToken",refreshToken);
                    ApiResponse<Map<String,String>> response = new ApiResponse<>(
                            HttpStatus.OK.value(),
                            "Email Successfully verified!",
                            "/api/v1/public/hospital/verify",
                            tokens
                    );
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                ApiResponse<Map<String,String>> response = new ApiResponse<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Invalid Verification Code",
                        "/api/v1/public/hospital/verify",
                        null
                );
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            else{
                ApiResponse<Map<String,String> > response = new ApiResponse<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Invalid Email enter the same as entered on login page",
                        "/api/v1/public/hospital/verify",
                        null
                );
                return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception e){
            logger.info("Error in verifying email"+e.getMessage());
            ApiResponse<Map<String,String> > response = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Error in verify email",
                    "/api/v1/public/hospital/verify",
                    null
            );
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





}
