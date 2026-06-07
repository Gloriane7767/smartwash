package com.gloriane.smartwash.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_readings")
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String siteName;

    @Column(nullable = false)
    private Double waterLevel; // percentage 0-100

    @Column(nullable = false)
    private Double batteryLevel; // percentage 0-100

    @Column(nullable = false)
    private String waterQuality; // e.g., "Good", "Fair", "Poor"

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Constructor
    public SensorReading() {} // Default constructor for JPA.

    public SensorReading(String siteName, Double waterLevel,
                         Double batteryLevel, String waterQuality, LocalDateTime timestamp) {
        this.siteName = siteName;
        this.waterLevel = waterLevel;
        this.batteryLevel = batteryLevel;
        this.waterQuality = waterQuality;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }

    public Double getWaterLevel() { return waterLevel; }
    public void setWaterLevel(Double waterLevel) { this.waterLevel = waterLevel; }

    public Double getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(Double batteryLevel) { this.batteryLevel = batteryLevel; }

    public String getWaterQuality() { return waterQuality; }
    public void setWaterQuality(String waterQuality) { this.waterQuality = waterQuality; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getStatusBadge() {
        if (waterLevel < 25.0) return "Critical";
        if (waterLevel < 50.0) return "Monitor";
        return "Normal";
    }

    public String getStatusClass() {
        if (waterLevel < 25.0) return "badge badge-danger";
        if (waterLevel < 50.0) return "badge badge-warn";
        return "badge badge-ok";
    }

    @Override
    public String toString() {
        return "SensorReading{" +
                "id=" + id +
                ", siteName='" + siteName + '\'' +
                ", waterLevel=" + waterLevel +
                ", batteryLevel=" + batteryLevel +
                ", timestamp=" + timestamp +
                '}';
    }
}