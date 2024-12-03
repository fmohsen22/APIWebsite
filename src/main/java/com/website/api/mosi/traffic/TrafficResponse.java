package com.website.api.mosi.traffic;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TrafficResponse {

    @JsonProperty("flowSegmentData")
    private FlowSegmentData flowSegmentData;

    public FlowSegmentData getFlowSegmentData() {
        return flowSegmentData;
    }

    public static class FlowSegmentData {

        @JsonProperty("frc")
        private String frc;

        @JsonProperty("currentSpeed")
        private int currentSpeed;

        @JsonProperty("freeFlowSpeed")
        private int freeFlowSpeed;

        @JsonProperty("currentTravelTime")
        private int currentTravelTime;

        @JsonProperty("freeFlowTravelTime")
        private int freeFlowTravelTime;

        @JsonProperty("confidence")
        private double confidence;

        @JsonProperty("roadClosure")
        private boolean roadClosure;

        @JsonProperty("coordinates")
        private Coordinates coordinates;

        @JsonProperty("@version")
        private String version;

        public String getFrc() {
            return frc;
        }

        public int getCurrentSpeed() {
            return currentSpeed;
        }

        public int getFreeFlowSpeed() {
            return freeFlowSpeed;
        }

        public int getCurrentTravelTime() {
            return currentTravelTime;
        }

        public int getFreeFlowTravelTime() {
            return freeFlowTravelTime;
        }

        public double getConfidence() {
            return confidence;
        }

        public boolean isRoadClosure() {
            return roadClosure;
        }

        public Coordinates getCoordinates() {
            return coordinates;
        }

        public String getVersion() {
            return version;
        }

        public static class Coordinates {

            @JsonProperty("coordinate")
            private List<Coordinate> coordinate;

            public List<Coordinate> getCoordinate() {
                return coordinate;
            }

            public static class Coordinate {

                @JsonProperty("latitude")
                private double latitude;

                @JsonProperty("longitude")
                private double longitude;

                public double getLatitude() {
                    return latitude;
                }

                public double getLongitude() {
                    return longitude;
                }
            }
        }
    }
}