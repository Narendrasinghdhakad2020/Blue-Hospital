package com.bluehospital.patient.patient.service;

import com.bluehospital.patient.patient.dto.LoginRequest;
import com.bluehospital.patient.patient.model.Patient;
import com.bluehospital.patient.patient.repository.PatientRepository;
import com.bluehospital.patient.patient.utils.JwtUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final JwtUtils jwtUtils;
    private final MongoTemplate mongoTemplate;


    //constructor injection
    public PatientService(PatientRepository patientRepository, JwtUtils jwtUtils,MongoTemplate mongoTemplate){
        this.patientRepository=patientRepository;
        this.jwtUtils=jwtUtils;
        this.mongoTemplate=mongoTemplate;
    }

    //loading patient from database using patient email/username
    public Patient loadPatientByUsername(String username){
        System.out.println("Patient-Service: Loading patient using patient name/email from DB!");
        return patientRepository.findPatientByUsername(username);
    }
    //loading patient from database usingn patient id
    public Patient loadPatientById(ObjectId id){
        System.out.println("Patient-Service: Loading  patient using Id from DB!");
        return patientRepository.findPatientById(id);
    }


    //saving patient in database
    public void savePatient(Patient patient){
        System.out.println("Patient-Service: Saving patient to DB!");
        patientRepository.save(patient);
    }

    //check patient already exist or not using username
    public boolean isPatientExistByUsername(String username){
        System.out.println("Patient-Service: check for patient existence using username in DB!");
        return patientRepository.existsPatientByUsername(username);
    }

    //check patient already exists or not using id of patient
    public boolean isPatientExistsById(ObjectId id){
        System.out.println("Patient-Service: check for patient existence using id in DB!");
        return patientRepository.existsPatientById(id);
    }

    //method to login and generate jwt token
    public String loginAndGenerateToken(LoginRequest request, AuthenticationManager authenticationManager){

        System.out.println("Patient-Service: login patient and generate token!");
        UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword());
        System.out.println("Patient-Service: Auth token for requested patient is: "+authToken);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
        System.out.println("Patient-Service: Successfully authenticated! the requested patient");
        Patient patient=patientRepository.findPatientByUsername(request.getUsername());

        return jwtUtils.generateToken(patient.getUsername());
    }

    //method to update the VerificationStatus of Patient
    public void updateEmailVerificationStatus(String username,boolean isVerified){

        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));

        Update update = new Update();
        update.set("isVerified",isVerified);//updating the isVerified field of patient
        update.set("verificationCode",null);

        mongoTemplate.updateFirst(query,update,Patient.class);



    }







}
