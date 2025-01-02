package com.bluehospital.patient.patient.service;

import com.bluehospital.patient.patient.dto.LoginRequest;
import com.bluehospital.patient.patient.model.Patient;
import com.bluehospital.patient.patient.repository.PatientRepository;
import com.bluehospital.patient.patient.utils.JwtUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImp implements PatientService {

    private static final Logger logger= LoggerFactory.getLogger(PatientServiceImp.class);

    private final PatientRepository patientRepository;
    private final JwtUtils jwtUtils;
    private final MongoTemplate mongoTemplate;


    //constructor injection
    public PatientServiceImp(PatientRepository patientRepository, JwtUtils jwtUtils, MongoTemplate mongoTemplate){
        this.patientRepository=patientRepository;
        this.jwtUtils=jwtUtils;
        this.mongoTemplate=mongoTemplate;
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

    //method to login and generate jwt token
    @Override
    public String loginAndGenerateToken(LoginRequest request, AuthenticationManager authenticationManager){

        logger.info("Patient-Service: login patient and generate token!");
        UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword());
        logger.info("Patient-Service: Temporary auth token for given patient and password is created Successfully!");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
        logger.info("Patient-Service: Successfully authenticated! the requested patient");
        Patient patient=patientRepository.findPatientByUsername(request.getUsername()).orElse(null);

        return jwtUtils.generateToken(patient.getUsername());
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







}
