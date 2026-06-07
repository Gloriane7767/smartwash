package com.gloriane.smartwash.service;

import com.gloriane.smartwash.model.SensorReading;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class DashboardWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public DashboardWebSocketService(
            SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Push a new reading to all connected browsers
    public void broadcastReading(SensorReading reading) {
        messagingTemplate.convertAndSend(
                "/topic/readings", reading);
        System.out.println("📺 Broadcast to dashboard: "
                + reading.getSiteName());
    }

    // Push an alert to all connected browsers
    public void broadcastAlert(String siteName, Double waterLevel) {
        String alert = "🚨 CRITICAL: " + siteName
                + " water level at " + waterLevel + "%";
        messagingTemplate.convertAndSend("/topic/alerts", alert);
        System.out.println("🚨 Alert broadcast: " + alert);
    }
}
