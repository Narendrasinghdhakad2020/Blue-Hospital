package com.bluehospital.patient.patient.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AvailableTiming {

    private String startTime; // Store as String instead of LocalTime
    private String endTime;

    @JsonIgnore
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public AvailableTiming() {}

    public AvailableTiming(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime.format(TIME_FORMATTER);
        this.endTime = endTime.format(TIME_FORMATTER);
    }

    // ✅ Getters and Setters
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    // ✅ Convert String to LocalTime (if needed)
    @JsonIgnore
    public LocalTime getStartTimeAsLocalTime() {
        return LocalTime.parse(startTime, TIME_FORMATTER);
    }

    @JsonIgnore
    public LocalTime getEndTimeAsLocalTime() {
        return LocalTime.parse(endTime, TIME_FORMATTER);
    }

    // ✅ Validation method
    public void validate() {
        if (startTime != null && endTime != null &&
                LocalTime.parse(endTime, TIME_FORMATTER).isBefore(LocalTime.parse(startTime, TIME_FORMATTER))) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
    }
}
