package com.bluehospital.patient.patient.controller.Public;

import com.bluehospital.patient.patient.dto.ApiResponse;
import com.bluehospital.patient.patient.dto.LoginRequest;
import com.bluehospital.patient.patient.dto.VerificationRequest;
import com.bluehospital.patient.patient.model.Patient;
import com.bluehospital.patient.patient.service.EmailService;
import com.bluehospital.patient.patient.service.PatientService;
import com.bluehospital.patient.patient.service.PatientServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/public/patient")
public class Login {

    private static final Logger logger = LoggerFactory.getLogger(Login.class);

    private final AuthenticationManager authenticationManager;
    private final PatientService patientService;
    private final EmailService emailService;

    private static String token;

    public Login(AuthenticationManager authenticationManager, PatientService patientService, EmailService emailService
    ){
        this.authenticationManager=authenticationManager;
        this.patientService=patientService;
        this.emailService=emailService;
    }

    //method to generate code
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> loginPatient(@RequestBody LoginRequest request){
            System.out.println("Controller: Login patient");
            Patient patient = patientService.loadPatientByUsername(request.getUsername());
            if(patient!=null) {
                token = patientService.loginAndGenerateToken(request, authenticationManager);
                if (token != null) {
                    //check if patient is Verified
                    if (!patient.isVerified()) {
                        String code = generateVerificationCode();
                        System.out.println("Code to save: " + code);
                        patient.setVerificationCode(code);
                        patient.setUpdatedAt(new Date());
                        patientService.savePatient(patient);

                            try{

                            emailService.sendVerificationEmail(patient.getUsername(), code);
                            }catch (Exception ex){
                                logger.error("Error in sending email");
                                ApiResponse<String> response= new ApiResponse<>(
                                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                        "Error in sending Email for verification",
                                        "/api/v1/public/patient/login",
                                        ""
                                );
                                return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
                            }

                        ApiResponse<String> response= new ApiResponse<>(
                                HttpStatus.NO_CONTENT.value(),
                                "Redirecting to Verify page",
                                "/api/v1/public/patient/login",
                                ""
                        );
                        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
                    }

                    //creating a response for request
                    ApiResponse<String> response = new ApiResponse<>(
                            HttpStatus.OK.value(),
                            "Login Successful",
                            "/api/v1/public/patient/login",
                            token

                    );
                    return new ResponseEntity<>(response, HttpStatus.OK);//when patient is successfully login

                } else {
                    logger.error("Patient credential are not Correct");
                    ApiResponse<String> response = new ApiResponse<>(
                            HttpStatus.UNAUTHORIZED.value(),
                            "Incorrect patientName or Password",
                            "/api/v1/public/patient/login",
                            ""
                    );
                    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
                }
            }
            else{
                logger.error("Patient Not Found in DB please check your email ID");
                ApiResponse<String> response = new ApiResponse<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Wrong email or password",
                        "/api/v1/public/patient/login",
                        ""
                );
                return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
            }
    }

    //making controller for verifying email
    @PostMapping("/verify")
    @Transactional
    public ResponseEntity<ApiResponse<String>> verifyPatientEmail(@RequestBody VerificationRequest request){

        try{
            Patient patient=patientService.loadPatientByUsername(request.getUsername());
            if(patient!=null) {
                logger.info("Code is: " + patient.getVerificationCode());
                if ( patient.getVerificationCode().equals(request.getVerificationCode())) {
                    patientService.updateEmailVerificationStatus(request.getUsername(), true);
                    ApiResponse<String > response = new ApiResponse<>(
                            HttpStatus.OK.value(),
                            "Email Successfully verified!",
                            "/api/v1/public/patient/verify",
                            token
                    );
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                ApiResponse<String> response = new ApiResponse<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Invalid Verification Code",
                        "/api/v1/public/patient/verify",
                        ""
                );
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            else{
                ApiResponse<String > response = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                        "Invalid Email enter the same as entered on login page",
                        "/api/v1/public/patient/verify",
                        ""
                        );
                return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception e){
            logger.info("Error in verifying email"+e.getMessage());
            ApiResponse<String > response = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Error in verify email",
                    "/api/v1/public/patient/verify",
                    ""
            );
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }



    }

}
