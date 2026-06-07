package com.gloriane.smartwash.controller;

import com.gloriane.smartwash.model.SensorReading;
import com.gloriane.smartwash.service.SensorReadingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readings")
public class SensorReadingController {

    private final SensorReadingService service;

    public SensorReadingController(SensorReadingService service) {
        this.service = service;
    }

    // POST /api/readings — receive a new sensor reading
    @PostMapping
    public ResponseEntity<SensorReading> addReading(
            @RequestBody SensorReading reading) {
        SensorReading saved = service.saveReading(reading);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // GET /api/readings — get all readings
    @GetMapping
    public ResponseEntity<List<SensorReading>> getAllReadings() {
        return ResponseEntity.ok(service.getAllReadings());
    }

    // GET /api/readings/recent — get 10 most recent
    @GetMapping("/recent")
    public ResponseEntity<List<SensorReading>> getRecentReadings() {
        return ResponseEntity.ok(service.getRecentReadings());
    }

    // GET /api/readings/site/{siteName} — get readings for one site
    @GetMapping("/site/{siteName}")
    public ResponseEntity<List<SensorReading>> getReadingsForSite(
            @PathVariable String siteName) {
        return ResponseEntity.ok(service.getReadingsForSite(siteName));
    }

    // GET /api/readings/critical — get all critical low water alerts
    @GetMapping("/critical")
    public ResponseEntity<List<SensorReading>> getCriticalReadings() {
        return ResponseEntity.ok(service.getCriticalReadings());
    }

    // GET /api/readings/site/{siteName}/critical — get critical low water alerts for one site
    @GetMapping("/site/{siteName}/critical")
    public ResponseEntity<List<SensorReading>> getCriticalReadingsForSite(
            @PathVariable String siteName) {
        return ResponseEntity.ok(service.getCriticalReadingsForSite(siteName));
    }

    // GET /api/readings/count — get total reading count
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalCount() {
        return ResponseEntity.ok((long) service.getAllReadings().size());
    }
}
