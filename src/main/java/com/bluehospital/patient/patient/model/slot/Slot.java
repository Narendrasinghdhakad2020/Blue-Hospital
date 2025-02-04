package com.bluehospital.patient.patient.model.slot;

import com.bluehospital.patient.patient.model.TimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.catalina.LifecycleState;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Document(collection = "slots")
public class Slot {

    @Id
    private String id;
    private String doctorId;
    private List<TimeFormat> slotsTiming;
    private Date createdAt;
    private Date updatedAt;

    public Slot(){};


    //methods to update the time fields
    public void onCreate(){
        this.createdAt=new Date();
    }
    public void onUpdate(){
        this.updatedAt=new Date();
    }

    //getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public List<TimeFormat> getSlotsTiming() {
        return slotsTiming;
    }

    public void setSlotsTiming(List<TimeFormat> slotsTiming) {
        this.slotsTiming = slotsTiming;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
