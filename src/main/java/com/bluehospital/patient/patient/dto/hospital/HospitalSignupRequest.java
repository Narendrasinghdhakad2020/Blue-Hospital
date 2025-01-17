package com.bluehospital.patient.patient.dto.hospital;

import java.util.ArrayList;

public class HospitalSignupRequest {

    private String name;
    private String username;
    private String password;
    private String role;
    private String phone;
    private String address;
    private ArrayList<String> specialities;
    //getters for above

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public ArrayList<String> getSpecialities() {
        return specialities;
    }
}
