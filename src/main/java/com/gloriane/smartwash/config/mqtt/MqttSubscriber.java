package com.gloriane.smartwash.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloriane.smartwash.dto.SensorReadingDTO;
import com.gloriane.smartwash.model.SensorReading;
import com.gloriane.smartwash.service.SensorReadingService;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component // This tells Spring Boot to create an instance of this class and manage it
public class MqttSubscriber implements MqttCallback {

    // The topic pattern to listen to
    // # means any topic starting with smartwash/readings/
    private static final String TOPIC = "smartwash/readings/#";

    private final MqttClient mqttClient;
    private final SensorReadingService sensorReadingService;
    private final ObjectMapper objectMapper;

    public MqttSubscriber(MqttClient mqttClient,
                          SensorReadingService sensorReadingService) {
        this.mqttClient = mqttClient;
        this.sensorReadingService = sensorReadingService;
        this.objectMapper = new ObjectMapper();
    }

    // This runs automatically when Spring Boot starts
    @PostConstruct
    public void subscribe() throws MqttException {
        // Tell the broker we want to receive messages
        mqttClient.setCallback(this);
        mqttClient.subscribe(TOPIC);
        System.out.println("✅ SmartWASH: Subscribed to topic: " + TOPIC);
    }

    // Called when connection to broker is lost
    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("⚠️ SmartWASH: MQTT connection lost - "
                + cause.getMessage());
        System.out.println("↻ Attempting to reconnect...");
    }

    // Called every time a new sensor message arrives
    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            // Convert the raw bytes to a string
            // Remove BOM and trim whitespace
            String payload = new String(message.getPayload())
                    .replace("\uFEFF", "")
                    .trim();

            System.out.println("📡 Message received on topic: " + topic);
            System.out.println("📦 Payload: " + payload);

            // Parse JSON string into our DTO object
            SensorReadingDTO dto = objectMapper.readValue(
                    payload, SensorReadingDTO.class);

            // Convert DTO to Entity and save to database
            SensorReading reading = new SensorReading(
                    dto.getSiteName(),
                    dto.getWaterLevel(),
                    dto.getBatteryLevel(),
                    dto.getWaterQuality(),
                    LocalDateTime.now()
            );

            sensorReadingService.saveReading(reading);

            System.out.println("💾 Saved to database: " + reading);

            // Check if water level is critical
            if (dto.getWaterLevel() < 25.0) {
                System.out.println("🚨 ALERT: Critical water level at "
                        + dto.getSiteName()
                        + " — Level: " + dto.getWaterLevel() + "%");
            }

        } catch (Exception e) {
            System.out.println("❌ Error processing MQTT message: "
                    + e.getMessage());
        }
    }

    // Called when a message is delivered (for QoS 1 and 2)
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Not needed for subscriber but required by interface
    }
}