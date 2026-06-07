package com.gloriane.smartwash.config;

import com.gloriane.smartwash.model.SensorReading;
import com.gloriane.smartwash.repository.SensorReadingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final SensorReadingRepository repository;

    public DataLoader(SensorReadingRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        // Simulate sensor readings from 5 Cameroon water sites
        repository.save(new SensorReading(
                "Bamenda Well", 18.5, 72.0, "Good",
                LocalDateTime.now().minusMinutes(10)));

        repository.save(new SensorReading(
                "Buea Borehole", 76.2, 88.0, "Good",
                LocalDateTime.now().minusMinutes(8)));

        repository.save(new SensorReading(
                "Kumba Spring", 54.1, 61.0, "Fair",
                LocalDateTime.now().minusMinutes(6)));

        repository.save(new SensorReading(
                "Limbe Tank", 82.7, 45.0, "Good",
                LocalDateTime.now().minusMinutes(4)));

        repository.save(new SensorReading(
                "Mamfe River", 69.3, 91.0, "Poor",
                LocalDateTime.now().minusMinutes(2)));

        log.info("SmartWASH: Sample sensor data loaded successfully");
    }
}