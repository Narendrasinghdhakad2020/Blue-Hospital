package com.bluehospital.patient.patient.repository.hospital;

import com.bluehospital.patient.patient.model.hospital.Hospital;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalRepository extends MongoRepository<Hospital, ObjectId> {

    public Optional<Hospital> findHospitalByUsername(String username);
    public Optional<Hospital> findHospitalById(ObjectId id);
    public boolean existsHospitalByUsername(String username);
    public boolean existsHospitalById(ObjectId id);
}
