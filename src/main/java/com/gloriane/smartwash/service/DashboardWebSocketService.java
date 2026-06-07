package com.gloriane.smartwash.service;

import com.gloriane.smartwash.model.SensorReading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class DashboardWebSocketService {

    private static final Logger log = LoggerFactory.getLogger(DashboardWebSocketService.class);

    private final SimpMessagingTemplate messagingTemplate;

    public DashboardWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastReading(SensorReading reading) {
        messagingTemplate.convertAndSend("/topic/readings", reading);
        log.info("📺 Broadcast to dashboard: {}", reading.getSiteName());
    }

    public void broadcastAlert(String siteName, Double waterLevel) {
        String alert = "CRITICAL: " + siteName + " water level at " + waterLevel + "%";
        messagingTemplate.convertAndSend("/topic/alerts", alert);
        log.warn("Alert broadcast: {}", alert);
    }
}
