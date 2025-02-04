package com.bluehospital.patient.patient.service.slot;

import com.bluehospital.patient.patient.model.slot.Slot;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SlotService {


    public Slot createSlotsForDoctor(ObjectId doctorId);
    public Slot getSlotsForDoctor(ObjectId doctorId);
    public void deleteSlotsForDoctor(ObjectId doctorId);
}
