package com.bluehospital.patient.patient.controller.Public;

import com.bluehospital.patient.patient.dto.SignupRequest;
import com.bluehospital.patient.patient.model.Patient;
import com.bluehospital.patient.patient.service.PatientService;
import org.apache.coyote.Response;
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

    private final PatientService patientService;
    private final PasswordEncoder passwordEncoder;

    public Signup(PatientService patientService, PasswordEncoder passwordEncoder){
        this.patientService=patientService;
        this.passwordEncoder=passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> addPatient(@RequestBody SignupRequest request){
        try{
            System.out.println("Signup-Controller: adding new patient to DB!");
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
                System.out.println("Signup-Controller: Patient Successfully saved to DB!");
                return new ResponseEntity<>(newPatient,HttpStatus.OK);
            }
            else{
                System.out.println("User already exist");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            }

        }catch(Exception e){
            System.out.println("Error during Signup");
            System.out.println("Error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

}
