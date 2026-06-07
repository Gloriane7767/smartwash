package com.gloriane.smartwash.service;

import com.africastalking.AfricasTalking;
import com.africastalking.SmsService;
import com.africastalking.sms.Recipient;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SmsAlertService {
    // Track last alert time per site to prevent SMS flooding
    private final java.util.Map<String, java.time.LocalDateTime>
            lastAlertTime = new java.util.HashMap<>();

    private static final int ALERT_COOLDOWN_MINUTES = 60;

    private static final Logger log = LoggerFactory.getLogger(SmsAlertService.class);

    @Value("${africastalking.username}")
    private String username;

    @Value("${africastalking.apikey}")
    private String apiKey;

    @Value("${africastalking.senderid}")
    private String senderId;

    @Value("${smartwash.alert.phonenumbers}")
    private String phoneNumbers;

    private SmsService smsService;

    @PostConstruct
    public void initialize() {
        try {
            AfricasTalking.initialize(username, apiKey);
            smsService = AfricasTalking.getService(AfricasTalking.SERVICE_SMS);
            log.info("SmartWASH: SMS service initialized");
        } catch (Exception e) {
            log.error("SMS service initialization failed: {}", e.getMessage(), e);
        }
    }

    public void sendCriticalWaterAlert(
            String siteName, Double waterLevel) {
        try {
            // Check cooldown — only alert once per hour per site
            java.time.LocalDateTime now =
                    java.time.LocalDateTime.now();
            java.time.LocalDateTime lastAlert =
                    lastAlertTime.get(siteName);

            if (lastAlert != null && lastAlert.plusMinutes(
                    ALERT_COOLDOWN_MINUTES).isAfter(now)) {
                log.info("⏱️ SMS cooldown active for {} — skipping", siteName);
                return;
            }

            // Update last alert time
            lastAlertTime.put(siteName, now);

            String message = buildAlertMessage(siteName, waterLevel);
            String[] recipients = phoneNumbers.split(",");

            List<Recipient> response = smsService.send(
                    message, recipients, Boolean.parseBoolean(senderId));

            log.info("📱 SMS alert sent for {} to {} recipients", siteName, recipients.length);

            for (Recipient recipient : response) {
                log.info("📱 SMS status: {} to {}", recipient.status, recipient.number);
            }

        } catch (Exception e) {
            System.out.println(
                    "❌ SMS sending failed: " + e.getMessage());
        }
    }

    private String buildAlertMessage(String siteName, Double waterLevel) {
        String urgency = waterLevel < 10 ? "EMERGENCY" : "ALERT";
        return "SMARTWASH " + urgency + "\n" +
                "Site: " + siteName + "\n" +
                "Water level: " + waterLevel + "%\n" +
                "Status: CRITICAL - Below safe threshold\n" +
                "Action: Dispatch maintenance team immediately\n" +
                "— SmartWASH System";
    }

    private String sanitize(String input) {
        if (input == null) return "null";
        return input.replaceAll("[\r\n\t]", "_");
    }
}
