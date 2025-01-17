package com.bluehospital.patient.patient.repository.patient;

import com.bluehospital.patient.patient.model.patient.Patient;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient,ObjectId> {

    public  Optional<Patient> findPatientByUsername(String username);
    public Optional<Patient> findPatientById(ObjectId id);
    public boolean existsPatientByUsername(String username);
    public boolean existsPatientById(ObjectId id);

}
