package com.bluehospital.patient.patient.controller.Public;

import com.bluehospital.patient.patient.dto.ApiResponse;
import com.bluehospital.patient.patient.dto.SignupRequest;
import com.bluehospital.patient.patient.model.Patient;
import com.bluehospital.patient.patient.service.PatientService;
import com.bluehospital.patient.patient.service.PatientServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/public/patient")
public class Signup {

    private static final Logger logger = LoggerFactory.getLogger(Signup.class);

    private final PatientService patientService;
    private final PasswordEncoder passwordEncoder;

    public Signup(PatientService patientService, PasswordEncoder passwordEncoder){
        this.patientService=patientService;
        this.passwordEncoder=passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> addPatient(@RequestBody SignupRequest request){

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

}
