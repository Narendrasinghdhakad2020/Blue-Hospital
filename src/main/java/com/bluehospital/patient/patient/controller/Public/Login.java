package com.bluehospital.patient.patient.controller.Public;

import com.bluehospital.patient.patient.dto.LoginRequest;
import com.bluehospital.patient.patient.dto.VerificationRequest;
import com.bluehospital.patient.patient.model.Patient;
import com.bluehospital.patient.patient.service.EmailService;
import com.bluehospital.patient.patient.service.PatientService;
import jakarta.mail.MessagingException;
import org.springframework.data.mongodb.core.mapping.ExplicitEncrypted;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/public/patient")
public class Login {

    private final AuthenticationManager authenticationManager;
    private final PatientService patientService;
    private final EmailService emailService;

    private static String token;

    public Login(AuthenticationManager authenticationManager, PatientService patientService,EmailService emailService
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
    public ResponseEntity<?> loginPatient(@RequestBody LoginRequest request){
        try{
            System.out.println("Controller: Login patient");
            Patient patient = patientService.loadPatientByUsername(request.getUsername());
             token=patientService.loginAndGenerateToken(request,authenticationManager);
            if(token!=null){
                //check if patient is Verified
                if(!patient.isVerified()){
                    String code=generateVerificationCode();
                    patient.setVerificationCode(code);
                    patient.setUpdatedAt(new Date());
                    patientService.savePatient(patient);

                    try{
                            emailService.sendVerificationEmail(patient.getUsername(),code);
                    }catch (Exception e){
                        System.out.println("Error"+e.getMessage());
                        return new ResponseEntity<>("Failed to send Verification Code!",HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    return new ResponseEntity<>("Redirect to verification page",HttpStatus.UNAUTHORIZED);
                }
                return new ResponseEntity<>("Token: "+token,HttpStatus.OK);

            }
            else{
                System.out.println("Patient credential are not Correct");
                return new ResponseEntity<>("Incorrect username or Password!",HttpStatus.BAD_REQUEST);
            }

        }catch(Exception e){
            System.out.println(" Controller: Error in login Patient "+ e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }


    }

    //making controller for verifying email
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPatientEmail(@RequestBody VerificationRequest request){

        try{
            Patient patient=patientService.loadPatientByUsername(request.getUsername());
            if(patient.getVerificationCode().equals(request.getVerificationCode())){
                patientService.updateEmailVerificationStatus(request.getUsername(),true);
                return new ResponseEntity<>("Token: "+token,HttpStatus.OK);
            }
            return new ResponseEntity<>("Invalid Verification Code",HttpStatus.UNAUTHORIZED);

        }catch (Exception e){
            System.out.println("Error in verifying email"+e.getMessage());
            return new ResponseEntity<>("Internal Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
        }



    }

}
