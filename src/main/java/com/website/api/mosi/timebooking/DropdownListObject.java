package com.website.api.mosi.timebooking;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields during serialization
public class DropdownListObject {

    private List<Treatments> treatmentDetail;
    private String topic;

    // Default no-argument constructor (required for Jackson)
    public DropdownListObject() {}

    // Constructor with arguments
    public DropdownListObject(List<Treatments> treatmentDetail, String topic) {
        this.treatmentDetail = treatmentDetail;
        this.topic = topic;
    }

    public List<Treatments> getTreatmentDetail() {
        return treatmentDetail;
    }

    public void setTreatmentDetail(List<Treatments> treatmentDetail) {
        this.treatmentDetail = treatmentDetail;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "DropdownListObject{" +
                "treatmentDetail=" + treatmentDetail +
                ", topic='" + topic + '\'' +
                '}';
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields during serialization
class Treatments {

    private String treatment;
    private double price;

    @JsonProperty("descripton") // Map JSON key "descripton" to this field
    private String description; // Correctly spelled field name

    private int duration;

    // Default no-argument constructor (required for Jackson)
    public Treatments() {}

    // Getters and setters
    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() { // Correct spelling
        return description;
    }

    public void setDescription(String description) { // Correct spelling
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Treatments{" +
                "treatment='" + treatment + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                '}';
    }
}