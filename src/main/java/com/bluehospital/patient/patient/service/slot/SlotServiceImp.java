package com.bluehospital.patient.patient.service.slot;

import com.bluehospital.patient.patient.model.TimeFormat;
import com.bluehospital.patient.patient.model.doctor.Doctor;
import com.bluehospital.patient.patient.model.slot.Slot;
import com.bluehospital.patient.patient.repository.doctor.DoctorRepository;
import com.bluehospital.patient.patient.repository.slot.SlotRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class SlotServiceImp implements SlotService{

    private static final Logger logger= LoggerFactory.getLogger(SlotServiceImp.class);

    @JsonIgnore
    private static final DateTimeFormatter TIME_FORMATTER=DateTimeFormatter.ofPattern("HH:mm");

    private final SlotRepository slotRepository;
    private final DoctorRepository doctorRepository;

    public SlotServiceImp(SlotRepository slotRepository,DoctorRepository doctorRepository){
        this.slotRepository=slotRepository;
        this.doctorRepository=doctorRepository;
    }

    //services

    //service to retrive slots for doctor
    @Override
    public Slot getSlotsForDoctor(ObjectId doctorId){
        return slotRepository.findSlotsById(doctorId);
    }

    //service to delete slots for particular doctor
    @Override
    public void deleteSlotsForDoctor(ObjectId doctorId){
        slotRepository.deleteSlotsById(doctorId);
    }

    //service to create slots for doctor
    @Override
    public Slot createSlotsForDoctor(ObjectId doctorId){
        Doctor doctor=doctorRepository.findDoctorById(doctorId).orElseThrow(()->new RuntimeException("Doctor not found!"));
        LocalTime startTime=doctor.getAvailableTiming().getStartTimeAsLocalTime();
        LocalTime endTime=doctor.getAvailableTiming().getEndTimeAsLocalTime();

        //in case slots are already created
        if(doctor.getSlots()!=null){
            return null;
        }

        Slot slot=new Slot();
        List<TimeFormat> slotsTiming=new ArrayList<>();
        while(startTime.isBefore(endTime)){
            LocalTime slotEnd=startTime.plusMinutes(15);
            if(slotEnd.isAfter(endTime)){
                break;
            }
            TimeFormat time=new TimeFormat(startTime,slotEnd);
            slotsTiming.add(time);
            startTime=slotEnd;
        }
        slot.setDoctorId(doctor.getId());
        slot.setSlotsTiming(slotsTiming);
        slot.onCreate();
        slotRepository.save(slot);//first save the slots
        doctor.setSlots(slot);
        doctorRepository.save(doctor);//create the slots for doctor and update the doctor


        return slot;
    }

}
