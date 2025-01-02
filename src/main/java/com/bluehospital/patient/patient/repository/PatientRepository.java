package com.bluehospital.patient.patient.repository;

import com.bluehospital.patient.patient.model.Patient;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient,ObjectId> {

    public  Optional<Patient> findPatientByUsername(String user);
    public Optional<Patient> findPatientById(ObjectId id);
    public boolean existsPatientByUsername(String username);
    public boolean existsPatientById(ObjectId id);

}
