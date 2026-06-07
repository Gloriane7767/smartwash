package com.gloriane.smartwash.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloriane.smartwash.dto.SensorReadingDTO;
import com.gloriane.smartwash.dto.TtnUplinkMessage;
import com.gloriane.smartwash.model.SensorReading;
import com.gloriane.smartwash.service.DashboardWebSocketService;
import com.gloriane.smartwash.service.SensorReadingService;
import com.gloriane.smartwash.service.SmsAlertService;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MqttSubscriber implements MqttCallback {

    private static final Logger log = LoggerFactory.getLogger(MqttSubscriber.class);
    private final SmsAlertService smsAlertService;

    @Value("${mqtt.topic.uplink}")
    private String topic;

    private final MqttClient mqttClient;
    private final SensorReadingService sensorReadingService;
    private final DashboardWebSocketService webSocketService;
    private final ObjectMapper objectMapper;

    public MqttSubscriber(MqttClient mqttClient,
                          SensorReadingService sensorReadingService,
                          DashboardWebSocketService webSocketService, SmsAlertService smsAlertService) {
        this.mqttClient = mqttClient;
        this.sensorReadingService = sensorReadingService;
        this.webSocketService = webSocketService;
        this.objectMapper = new ObjectMapper()
                .configure(com.fasterxml.jackson.databind
                        .DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, false)
                .configure(com.fasterxml.jackson.databind
                        .DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.smsAlertService = smsAlertService;
    }

    @PostConstruct
    public void subscribe() throws MqttException {
        mqttClient.setCallback(this);
        mqttClient.subscribe(topic);
        log.info("SmartWASH: Subscribed to TTN topic: {}", topic);
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.warn("MQTT connection lost: {}", cause.getMessage(), cause);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload())
                    .replace("\uFEFF", "")
                    .trim();

            log.info("📡 TTN message on topic: {}", topic);
            log.debug("Raw payload: {}", payload);

            SensorReadingDTO dto = parsePayload(payload, topic);

            if (dto == null) {
                log.warn("Could not parse payload — skipping");
                return;
            }

            if (dto.getWaterLevel() == null) {
                log.warn("Missing waterLevel — skipping");
                return;
            }

            SensorReading reading = new SensorReading(
                    dto.getSiteName(),
                    dto.getWaterLevel(),
                    dto.getBatteryLevel() != null ? dto.getBatteryLevel() : 0.0,
                    dto.getWaterQuality() != null ? dto.getWaterQuality() : "Unknown",
                    LocalDateTime.now()
            );

            SensorReading saved = sensorReadingService.saveReading(reading);
            log.info("💾 Saved: {}", saved);

            webSocketService.broadcastReading(reading);

            // Critical alert
            if (dto.getWaterLevel() < 25.0) {
                // Push to dashboard
                webSocketService.broadcastAlert(
                        dto.getSiteName(), dto.getWaterLevel());

                // Send SMS to field workers
                smsAlertService.sendCriticalWaterAlert(
                        dto.getSiteName(), dto.getWaterLevel());
            }

        } catch (Exception e) {
            log.error("Error processing MQTT message: {}", e.getMessage(), e);
        }
    }

    private SensorReadingDTO parsePayload(String payload, String topic) {
        String deviceId = extractDeviceId(topic);
        log.debug("Device ID: {}", deviceId);

        try {
            TtnUplinkMessage ttnMessage = objectMapper.readValue(payload, TtnUplinkMessage.class);

            if (ttnMessage.getUplinkMessage() != null) {
                SensorReadingDTO dto = null;

                if (ttnMessage.getUplinkMessage().getDecodedPayload() != null) {
                    dto = ttnMessage.getUplinkMessage().getDecodedPayload();
                    log.debug("Got decoded payload from TTN");
                }

                if (dto == null || dto.getWaterLevel() == null) {
                    log.warn("No waterLevel in TTN payload — using simulated values");
                    dto = new SensorReadingDTO();
                    dto.setWaterLevel(getSimulatedWaterLevel(deviceId));
                    dto.setBatteryLevel(85.0);
                    dto.setWaterQuality("Good");
                }

                dto.setSiteName(mapDeviceIdToSiteName(deviceId));
                log.debug("Final waterLevel: {}", dto.getWaterLevel());
                return dto;
            }
        } catch (Exception e) {
            log.warn("TTN parse failed: {}", e.getMessage(), e);
        }

        try {
            SensorReadingDTO dto = objectMapper.readValue(payload, SensorReadingDTO.class);
            if (dto.getSiteName() == null) {
                dto.setSiteName(mapDeviceIdToSiteName(deviceId));
            }
            log.debug("Parsed as simple format");
            return dto;
        } catch (Exception e) {
            log.error("Could not parse payload: {}", e.getMessage(), e);
            return null;
        }
    }

    private Double getSimulatedWaterLevel(String deviceId) {
        switch (deviceId) {
            case "bamenda-well-sensor": return 18.5;
            case "my-new-device":       return 75.3;
            default:                    return 50.0;
        }
    }

    private String extractDeviceId(String topic) {
        try {
            String[] parts = topic.split("/");
            if (parts.length >= 4) return parts[3];
        } catch (Exception e) {
            log.warn("Could not extract device ID from topic: {}", topic);
        }
        return "unknown-device";
    }

    private String mapDeviceIdToSiteName(String deviceId) {
        switch (deviceId) {
            case "bamenda-well-sensor": return "Bamenda Well";
            case "my-new-device":       return "Buea Borehole";
            default:                    return deviceId;
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}
}
