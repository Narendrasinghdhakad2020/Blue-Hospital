package com.bluehospital.patient.patient.service.doctor;

import com.bluehospital.patient.patient.dto.doctor.AddDoctorDTO;
import com.bluehospital.patient.patient.model.doctor.Doctor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DoctorService {

    public Doctor addDoctor(String hospitalId, AddDoctorDTO doctorDTO); //service to add new doctors to hospital
    public List<Doctor> getDoctorsByHospital(String hospitalId); //service to find doctors of particular hospital
}
