package com.gloriane.smartwash.repository;

import com.gloriane.smartwash.model.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    // Spring automatically implements this method for you
    // just by reading the method name
    List<SensorReading> findBySiteNameOrderByTimestampDesc(String siteName);

    // Find the most recent readings across all sites
    List<SensorReading> findTop10ByOrderByTimestampDesc();

    // Find readings where water level is below a threshold
    List<SensorReading> findByWaterLevelLessThan(Double threshold);

    // Find critical readings for a specific site only
    List<SensorReading> findBySiteNameAndWaterLevelLessThan(
            String siteName, Double threshold);
}
