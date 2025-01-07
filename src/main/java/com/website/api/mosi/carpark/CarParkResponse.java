package com.website.api.mosi.carpark;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CarParkResponse {

    private String tsn;
    private String time;
    private int spots;
    private List<Zone> zones;
    private int parkID;
    private Location location;
    private Occupancy occupancy;
    @JsonProperty("MessageDate")
    private String messageDate;
    @JsonProperty("facility_id")
    private String facilityId;
    @JsonProperty("facility_name")
    private String facilityName;
    @JsonProperty("tfnsw_facility_id")
    private String tfnswFacilityId;

    // Getters and setters
    public String getTsn() {
        return tsn;
    }

    public void setTsn(String tsn) {
        this.tsn = tsn;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSpots() {
        return spots;
    }

    public void setSpots(int spots) {
        this.spots = spots;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }

    public int getParkID() {
        return parkID;
    }

    @JsonProperty("ParkID")
    public void setParkID(int parkID) {
        this.parkID = parkID;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Occupancy getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(Occupancy occupancy) {
        this.occupancy = occupancy;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public String getFacilityId() {
        return facilityId;
    }

    @JsonProperty("facility_id")
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    @JsonProperty("facility_name")
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getTfnswFacilityId() {
        return tfnswFacilityId;
    }

    @JsonProperty("tfnsw_facility_id")
    public void setTfnswFacilityId(String tfnswFacilityId) {
        this.tfnswFacilityId = tfnswFacilityId;
    }

    // Nested classes for zones, occupancy, and location
    public static class Zone {
        private int spots;
        private String zoneId;
        private Occupancy occupancy;
        private String zoneName;
        private String parentZoneId;

        // Getters and setters
        public int getSpots() {
            return spots;
        }

        public void setSpots(int spots) {
            this.spots = spots;
        }

        public String getZoneId() {
            return zoneId;
        }

        @JsonProperty("zone_id")
        public void setZoneId(String zoneId) {
            this.zoneId = zoneId;
        }

        public Occupancy getOccupancy() {
            return occupancy;
        }

        public void setOccupancy(Occupancy occupancy) {
            this.occupancy = occupancy;
        }

        public String getZoneName() {
            return zoneName;
        }

        @JsonProperty("zone_name")
        public void setZoneName(String zoneName) {
            this.zoneName = zoneName;
        }

        public String getParentZoneId() {
            return parentZoneId;
        }

        @JsonProperty("parent_zone_id")
        public void setParentZoneId(String parentZoneId) {
            this.parentZoneId = parentZoneId;
        }
    }

    public static class Occupancy {
        private String loop;
        private Integer total;
        private String monthlies;
        private String openGate;
        private String transients;

        // Getters and setters
        public String getLoop() {
            return loop;
        }

        public void setLoop(String loop) {
            this.loop = loop;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public String getMonthlies() {
            return monthlies;
        }

        public void setMonthlies(String monthlies) {
            this.monthlies = monthlies;
        }

        public String getOpenGate() {
            return openGate;
        }

        @JsonProperty("open_gate")
        public void setOpenGate(String openGate) {
            this.openGate = openGate;
        }

        public String getTransients() {
            return transients;
        }

        public void setTransients(String transients) {
            this.transients = transients;
        }
    }

    public static class Location {
        private String suburb;
        private String address;
        private String latitude;
        private String longitude;

        // Getters and setters
        public String getSuburb() {
            return suburb;
        }

        public void setSuburb(String suburb) {
            this.suburb = suburb;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
    }
}
