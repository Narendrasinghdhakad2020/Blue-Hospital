package com.bluehospital.patient.patient.dto;

public class SignupRequest {

    private String name;
    private String username;
    private String password;
    private String role;
    private String phone;
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
}
