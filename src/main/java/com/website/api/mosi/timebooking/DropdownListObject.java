package com.website.api.mosi.timebooking;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class DropdownListObject {
    List<Treatments> treatmentDetail;
    String topic;

    public DropdownListObject(List<Treatments> treatmentDetail, String topic) {
        this.treatmentDetail = treatmentDetail;
        this.topic = topic;
    }

    public List<Treatments> getTreatmentDetail() {
        return treatmentDetail;
    }

    public String getTopic() {
        return topic;
    }

    @Override
    public String toString() {
        return "DropdownListObject{" +
                "treatmentDetail=" + treatmentDetail +
                ", topic='" + topic + '\'' +
                '}';
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
class Treatments{
    String treatment;
    double price;
    String descripton;
    int duration;

    public String getTreatment() {
        return treatment;
    }

    public double getPrice() {
        return price;
    }

    public String getDescripton() {
        return descripton;
    }

    public int getDuration() {
        return duration;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescripton(String descripton) {
        this.descripton = descripton;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Treatments{" +
                "treatment='" + treatment + '\'' +
                ", price=" + price +
                ", descripton='" + descripton + '\'' +
                ", duration=" + duration +
                '}';
    }
}
