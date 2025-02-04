package com.bluehospital.patient.patient.controller.slot.Public;

import com.bluehospital.patient.patient.dto.common.ApiResponse;
import com.bluehospital.patient.patient.model.slot.Slot;
import com.bluehospital.patient.patient.service.slot.SlotService;
import com.bluehospital.patient.patient.service.slot.SlotServiceImp;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/slots")
public class SlotController {

    private static final Logger logger= LoggerFactory.getLogger(SlotController.class);

    private SlotService slotService;

    public SlotController(SlotService slotService){
        this.slotService=slotService;
    }

    @GetMapping("/get/{doctorId}")
    public ResponseEntity<ApiResponse<Slot>> getSlotsById(@PathVariable ObjectId doctorId){
        logger.info("We are in getSlotsByDoctorId in slot Controller ");

        Slot slot=slotService.getSlotsForDoctor(doctorId);
        ApiResponse<Slot> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Successfully retrived Slots for doctorId",
                "/api/v1/public/slots/get/doctorId",
                slot
        );

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping("/delete/{doctorId}")
    public ResponseEntity<ApiResponse<String>> deleteSlotsForDoctor(@PathVariable ObjectId doctorId){
        logger.info("We are in deleteAllSlots in slot Controller ");

        slotService.deleteSlotsForDoctor(doctorId);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Successfully deleted all Slots for given DoctorId",
                "/api/v1/public/slots/get/doctorId",
                "all slots for given doctorId are deleted Successfully"
        );

        return new ResponseEntity<>(response,HttpStatus.OK);
    }



    @PostMapping("/generate/{doctorId}")
    public ResponseEntity<ApiResponse<Slot>> createSLotsForDoctor(@PathVariable ObjectId doctorId){
        logger.info("Welcome to generate route of slotController");

        Slot generatedSlots=slotService.createSlotsForDoctor(doctorId);
        if(generatedSlots==null){
            ApiResponse<Slot> response = new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Slots are already creaded",
                    "/api/v1/public/slots/generate/{doctorId}",
                    null
            );
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        ApiResponse<Slot> response=new ApiResponse<>(
                HttpStatus.OK.value(),
                "Successfully generated the slots for the Doctor",
                "/api/v1/public/slots/generate/{doctorId}",
                generatedSlots
        );

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
