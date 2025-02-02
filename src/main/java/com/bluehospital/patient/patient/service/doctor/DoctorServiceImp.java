package com.bluehospital.patient.patient.service.doctor;

import com.bluehospital.patient.patient.dto.doctor.AddDoctorDTO;
import com.bluehospital.patient.patient.model.doctor.Doctor;
import com.bluehospital.patient.patient.repository.doctor.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DoctorServiceImp implements DoctorService{

    private DoctorRepository doctorRepository;

    public DoctorServiceImp(DoctorRepository doctorRepository){
        this.doctorRepository=doctorRepository;
    }

    //service to add new doctors to hospital
    @Override
    public Doctor addDoctor(String hospitalId, AddDoctorDTO doctorDTO) {
        Doctor doctor = new Doctor();
        doctor.setHospitalId(hospitalId);
        doctor.setName(doctorDTO.getName());
        doctor.setEmail(doctorDTO.getEmail());
        doctor.setGender(doctorDTO.getGender());
        doctor.setPhone(doctorDTO.getPhone());
        doctor.setAddress(doctorDTO.getAddress());
        doctor.setAvailableTiming(doctorDTO.getAvailableTiming());
        doctor.setSpecialty(doctorDTO.getSpecialty());
        doctor.setCreatedAt(new Date());
        return doctorRepository.save(doctor);
    }

    //service to get all doctors of hospital
    @Override
    public List<Doctor> getDoctorsByHospital(String hospitalId) {
        return doctorRepository.findDoctorsByHospitalId(hospitalId);
    }

    //service to check existing doctor by email id
    @Override
    public Boolean checkDoctorExistByEmail(String email){
        Doctor doctor=doctorRepository.findDoctorByEmail(email).orElse(null);
        if(doctor==null){
            return false;
        }
        return true;
    }
}
