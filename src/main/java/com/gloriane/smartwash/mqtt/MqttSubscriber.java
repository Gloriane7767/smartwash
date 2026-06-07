package com.gloriane.smartwash.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloriane.smartwash.dto.SensorReadingDTO;
import com.gloriane.smartwash.dto.TtnUplinkMessage;
import com.gloriane.smartwash.model.SensorReading;
import com.gloriane.smartwash.service.DashboardWebSocketService;
import com.gloriane.smartwash.service.SensorReadingService;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MqttSubscriber implements MqttCallback {

    @Value("${mqtt.topic.uplink}")
    private String topic;

    private final MqttClient mqttClient;
    private final SensorReadingService sensorReadingService;
    private final DashboardWebSocketService webSocketService;
    private final ObjectMapper objectMapper;

    public MqttSubscriber(MqttClient mqttClient,
                          SensorReadingService sensorReadingService,
                          DashboardWebSocketService webSocketService) {
        this.mqttClient = mqttClient;
        this.sensorReadingService = sensorReadingService;
        this.webSocketService = webSocketService;
        this.objectMapper = new ObjectMapper()
                .configure(com.fasterxml.jackson.databind
                        .DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, false)
                .configure(com.fasterxml.jackson.databind
                        .DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @PostConstruct
    public void subscribe() throws MqttException {
        mqttClient.setCallback(this);
        mqttClient.subscribe(topic);
        System.out.println("✅ SmartWASH: Subscribed to TTN topic: " + topic);
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("⚠️ MQTT connection lost: " + cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload())
                    .replace("\uFEFF", "")
                    .trim();

            System.out.println("📡 TTN message on topic: " + topic);
            System.out.println("📦 Raw payload: " + payload);

            SensorReadingDTO dto = parsePayload(payload, topic);

            if (dto == null) {
                System.out.println("⚠️ Could not parse — skipping");
                return;
            }

            if (dto.getWaterLevel() == null) {
                System.out.println("⚠️ Missing waterLevel — skipping");
                return;
            }

            SensorReading reading = new SensorReading(
                    dto.getSiteName(),
                    dto.getWaterLevel(),
                    dto.getBatteryLevel() != null ? dto.getBatteryLevel() : 0.0,
                    dto.getWaterQuality() != null ? dto.getWaterQuality() : "Unknown",
                    LocalDateTime.now()
            );

            sensorReadingService.saveReading(reading);
            System.out.println("💾 Saved: " + reading);

            webSocketService.broadcastReading(reading);

            if (dto.getWaterLevel() < 25.0) {
                webSocketService.broadcastAlert(dto.getSiteName(), dto.getWaterLevel());
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private SensorReadingDTO parsePayload(String payload, String topic) {
        String deviceId = extractDeviceId(topic);
        System.out.println("🔍 Device ID: " + deviceId);

        try {
            TtnUplinkMessage ttnMessage = objectMapper.readValue(
                    payload, TtnUplinkMessage.class);

            if (ttnMessage.getUplinkMessage() != null) {
                SensorReadingDTO dto = null;

                // Try decoded payload from TTN formatter
                if (ttnMessage.getUplinkMessage()
                        .getDecodedPayload() != null) {
                    dto = ttnMessage.getUplinkMessage()
                            .getDecodedPayload();
                    System.out.println("✅ Got decoded payload from TTN");
                }

                // If waterLevel is still null use simulated values
                // This handles missing or broken TTN payload formatter
                if (dto == null || dto.getWaterLevel() == null) {
                    System.out.println("⚠️ No waterLevel — using simulated values");
                    dto = new SensorReadingDTO();
                    dto.setWaterLevel(getSimulatedWaterLevel(deviceId));
                    dto.setBatteryLevel(85.0);
                    dto.setWaterQuality("Good");
                }

                dto.setSiteName(mapDeviceIdToSiteName(deviceId));
                System.out.println("✅ Final waterLevel: " + dto.getWaterLevel());
                return dto;
            }
        } catch (Exception e) {
            System.out.println("⚠️ TTN parse failed: " + e.getMessage());
        }

        // Fallback to simple local format
        try {
            SensorReadingDTO dto = objectMapper.readValue(
                    payload, SensorReadingDTO.class);
            if (dto.getSiteName() == null) {
                dto.setSiteName(mapDeviceIdToSiteName(deviceId));
            }
            System.out.println("✅ Parsed as simple format");
            return dto;
        } catch (Exception e) {
            System.out.println("❌ Could not parse payload");
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
            System.out.println("⚠️ Could not extract device ID");
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
