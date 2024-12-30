package com.bluehospital.patient.patient.dto;

public class VerificationRequest {

    private String username;
    private String verificationCode;

    //getter for verificationCode
    public String getVerificationCode() {
        return verificationCode;
    }
    public void setVerificationCode(String verificationCode){
        this.verificationCode=verificationCode;
    }
    public void setUsername(String username){
        this.username=username;
    }
    public String getUsername(){
        return username;
    }
}
