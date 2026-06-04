package com.gloriane.smartwash.service;

import com.gloriane.smartwash.model.SensorReading;
import com.gloriane.smartwash.repository.SensorReadingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorReadingService {

    private final SensorReadingRepository repository; // Repository for database operations

    // Spring automatically injects the repository here
    public SensorReadingService(SensorReadingRepository repository) {
        this.repository = repository;
    }

    // Save a new sensor reading
    public SensorReading saveReading(SensorReading reading) {
        if (reading.getTimestamp() == null) {
            reading.setTimestamp(LocalDateTime.now());
        }
        return repository.save(reading);
    }

    // Get all readings for a specific site
    public List<SensorReading> getReadingsForSite(String siteName) {
        return repository.findBySiteNameOrderByTimestampDesc(siteName);
    }

    // Get the 10 most recent readings across all sites
    public List<SensorReading> getRecentReadings() {
        return repository.findTop10ByOrderByTimestampDesc();
    }

    // Get all sites with critically low water level
    public List<SensorReading> getCriticalReadings() {
        return repository.findByWaterLevelLessThan(25.0);
    }

    // Get all readings
    public List<SensorReading> getAllReadings() {
        return repository.findAll();
    }

    // Get Critical readings for a specific site
    public List<SensorReading> getCriticalReadingsForSite(String siteName) {
        return repository.findBySiteNameAndWaterLevelLessThan(siteName, 25.0);
    }
}
