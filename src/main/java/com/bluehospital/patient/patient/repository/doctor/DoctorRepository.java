package com.bluehospital.patient.patient.repository.doctor;

import com.bluehospital.patient.patient.model.doctor.Doctor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends MongoRepository<Doctor, ObjectId> {

    public Optional<Doctor> findDoctorByName(String name);
    public Optional<Doctor> findDoctorById(ObjectId id);
    public Optional<Doctor> findDoctorByEmail(String Email);
    public List<Doctor> findDoctorsByHospitalId(String id);
}
