package com.bluehospital.patient.patient.repository;

import com.bluehospital.patient.patient.model.Patient;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends MongoRepository<Patient,ObjectId> {

    public Patient findPatientByUsername(String username);
    public Patient findPatientById(ObjectId id);
    public boolean existsPatientByUsername(String username);
    public boolean existsPatientById(ObjectId id);

}
