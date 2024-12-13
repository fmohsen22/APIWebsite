package com.website.api.mosi.timebooking;

public class AppointmentRequest {
    private String fullName;
    private String treatment;
    private int duration;
    private String date;
    private String timeSlot;
    private String email;
    private String phone;

    // Getters and setters

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "AppointmentRequest{" +
                "fullName='" + fullName + '\'' +
                ", treatment='" + treatment + '\'' +
                ", duration='" + duration + '\'' +
                ", date='" + date + '\'' +
                ", timeSlot='" + timeSlot + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
