package com.bluehospital.patient.patient.dto.doctor;

import com.bluehospital.patient.patient.model.AvailableTiming;
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
    private AvailableTiming availableTiming;

    //getters

    public @NotBlank(message = "Name is required") String getName() {
        return name;
    }

    public @Email(message = "Invalid email format") String getEmail() {
        return email;
    }

    public @NotBlank(message = "Phone number is required") @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format") String getPhone() {
        return phone;
    }

    public Gender getGender() {
        return gender;
    }

    public @NotBlank(message = "Address is required") String getAddress() {
        return address;
    }

    public @NotBlank(message = "Specialty is required") String getSpecialty() {
        return specialty;
    }

    public @NotNull(message = "Available timing is required") AvailableTiming getAvailableTiming() {
        return availableTiming;
    }
}
