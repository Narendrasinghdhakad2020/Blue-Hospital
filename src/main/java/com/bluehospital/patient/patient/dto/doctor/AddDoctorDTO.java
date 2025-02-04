package com.bluehospital.patient.patient.dto.doctor;

import com.bluehospital.patient.patient.model.TimeFormat;
import com.bluehospital.patient.patient.model.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AddDoctorDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phone;

    private Gender gender;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Specialty is required")
    private String specialty;

    @NotNull(message = "Available timing is required")
    private TimeFormat availableTiming;

    // ✅ Constructor
    public AddDoctorDTO() {}

    public AddDoctorDTO(String name, String email, String phone, Gender gender, String address, String specialty, TimeFormat availableTiming) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.address = address;
        this.specialty = specialty;
        this.availableTiming = availableTiming;
    }

    // ✅ Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public TimeFormat getAvailableTiming() {
        return availableTiming;
    }

    public void setAvailableTiming(TimeFormat availableTiming) {
        this.availableTiming = availableTiming;
    }

}
