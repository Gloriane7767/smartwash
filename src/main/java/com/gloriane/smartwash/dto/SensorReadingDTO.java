package com.gloriane.smartwash.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SensorReadingDTO {
    @JsonProperty("site_name")
    private String siteName;

    @JsonProperty("water_level")
    private Double waterLevel;

    @JsonProperty("battery_level")
    private Double batteryLevel;

    @JsonProperty("water_quality")
    private String waterQuality;

    // Default constructor - required for JSON parsing
    public SensorReadingDTO() {}

    public SensorReadingDTO(String siteName, Double waterLevel,
                            Double batteryLevel, String waterQuality) {
        this.siteName = siteName;
        this.waterLevel = waterLevel;
        this.batteryLevel = batteryLevel;
        this.waterQuality = waterQuality;
    }

    // Getters and Setters
    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Double getWaterLevel() { return waterLevel; }
    public void setWaterLevel(Double waterLevel) {
        this.waterLevel = waterLevel;
    }

    public Double getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(Double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public String getWaterQuality() { return waterQuality; }
    public void setWaterQuality(String waterQuality) {
        this.waterQuality = waterQuality;
    }

    @Override
    public String toString() {
        return "SensorReadingDTO{" +
                "siteName='" + siteName + '\'' +
                ", waterLevel=" + waterLevel +
                ", batteryLevel=" + batteryLevel +
                ", waterQuality='" + waterQuality + '\'' +
                '}';
    }

}
