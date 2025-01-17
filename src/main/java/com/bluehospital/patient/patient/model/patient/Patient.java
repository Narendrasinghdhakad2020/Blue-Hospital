package com.bluehospital.patient.patient.model.patient;

import com.bluehospital.patient.patient.model.User;
import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Document(collection = "patient")
public class Patient extends User implements UserDetails {

    @Id
    protected String Id;

    @NonNull
    private String name;

    @NonNull
    private String phone;

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
