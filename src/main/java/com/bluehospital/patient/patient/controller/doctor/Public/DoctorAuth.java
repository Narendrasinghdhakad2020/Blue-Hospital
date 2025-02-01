package com.bluehospital.patient.patient.controller.doctor.Public;

import com.bluehospital.patient.patient.dto.common.ApiResponse;
import com.bluehospital.patient.patient.dto.doctor.AddDoctorDTO;
import com.bluehospital.patient.patient.model.doctor.Doctor;
import com.bluehospital.patient.patient.service.doctor.DoctorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/doctor")
public class DoctorAuth {

    private static final Logger logger= LoggerFactory.getLogger(DoctorAuth.class);

    private final DoctorService doctorService;

    public DoctorAuth(DoctorService doctorService){
        this.doctorService=doctorService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Doctor>> addDoctor(
            @RequestParam String hospitalId,
            @RequestParam String role,
            @Valid @RequestBody AddDoctorDTO doctorDTO) {

        // Ensure only hospital roles can add doctors
        if (!"hospital".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body(null); // Forbidden
        }

        Doctor doctor = doctorService.addDoctor(hospitalId, doctorDTO);
        ApiResponse<Doctor> response=new ApiResponse<>(
                HttpStatus.OK.value(),
                "Successfully added doctor",
                "/api/v1/public/doctor/add",
                doctor
        );
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    //controller to get all doctors of hospital
    @GetMapping("/get/{hospitalId}")
    public ResponseEntity<ApiResponse<List<Doctor>>> getDoctorsByHospital(@PathVariable String hospitalId) {
        List<Doctor> doctors = doctorService.getDoctorsByHospital(hospitalId);

        ApiResponse<List<Doctor>> response=new ApiResponse<>(
                HttpStatus.OK.value(),
                "Successfully retrived doctors detail from DB for given Hospital",
                "/api/v1/public/doctor/get/hospitalId",
                doctors
        );
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


}
