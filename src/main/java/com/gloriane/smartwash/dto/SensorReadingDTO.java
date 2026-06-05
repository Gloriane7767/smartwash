package com.gloriane.smartwash.dto;

public class SensorReadingDTO {
    private String siteName;
    private Double waterLevel;
    private Double batteryLevel;
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
