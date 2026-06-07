package com.gloriane.smartwash.controller;

import com.gloriane.smartwash.model.SensorReading;
import com.gloriane.smartwash.service.SensorReadingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final SensorReadingService service;

    public DashboardController(SensorReadingService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Get recent readings for initial page load
        List<SensorReading> recent = service.getRecentReadings();
        List<SensorReading> critical = service.getCriticalReadings();

        model.addAttribute("readings", recent);
        model.addAttribute("criticalCount", critical.size());
        model.addAttribute("totalReadings",
                service.getAllReadings().size());

        return "dashboard"; // loads dashboard.html from templates
    }
}