package com.bluehospital.patient.patient.model.hospital;

import com.mongodb.lang.NonNull;
import com.bluehospital.patient.patient.model.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Document(collection = "hospital")
public class Hospital extends User implements UserDetails {

    @Id
    protected String Id;

    @NonNull
    private String name;

    @NonNull
    private String phone;

    @NonNull
    private String address;

    @NonNull
    private ArrayList<String> specialities;


    //getters and setters


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }


    @NonNull
    public String getPhone() {
        return phone;
    }

    public void setPhone(@NonNull String phone) {
        this.phone = phone;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    @NonNull
    public ArrayList<String> getSpecialities() {
        return specialities;
    }

    public void setSpecialities(@NonNull ArrayList<String> specialities) {
        this.specialities = specialities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(role.split(",")).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    @NonNull
    public String getUsername(){return username;}

    @Override
    @NonNull
    public String getPassword(){return password;}
}
