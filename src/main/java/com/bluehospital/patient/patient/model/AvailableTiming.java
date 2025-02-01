package com.bluehospital.patient.patient.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public class AvailableTiming {

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    public void validate() {
        if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
    }
}
