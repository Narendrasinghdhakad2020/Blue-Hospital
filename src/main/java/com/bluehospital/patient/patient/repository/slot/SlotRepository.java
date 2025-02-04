package com.bluehospital.patient.patient.repository.slot;

import com.bluehospital.patient.patient.model.slot.Slot;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlotRepository extends MongoRepository<Slot, ObjectId> {


    public Slot findSlotsById(ObjectId doctorId);
    public void deleteSlotsById(ObjectId doctorId);
}
