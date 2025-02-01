package com.bluehospital.patient.patient.dto.patient;

import com.bluehospital.patient.patient.model.Gender;

public class PatientSignupRequest {


    private String name;
    private String username;
    private String password;
    private String role;
    private String phone;
    private Gender gender;
    //getters for above

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }

    public Gender getGender() {
        return gender;
    }
}
