package com.website.api.mosi.helper;

import java.time.LocalDateTime;

public class CsvRecord {

        private LocalDateTime dateTime;
        private String function;
        private String json;

        public CsvRecord(LocalDateTime dateTime, String function, String json) {
            this.dateTime = dateTime;
            this.function = function;
            this.json = json;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public String getFunction() {
            return function;
        }

        public String getJson() {
            return json;
        }

        @Override
        public String toString() {
            return "CsvRecord{" +
                    "dateTime=" + dateTime +
                    ", function='" + function + '\'' +
                    ", json='" + json + '\'' +
                    '}';
        }

}
