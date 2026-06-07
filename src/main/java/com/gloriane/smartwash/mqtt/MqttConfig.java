package com.gloriane.smartwash.mqtt;

/*import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {
    // Your Mosquitto broker address
    private static final String BROKER_URL = "tcp://localhost:1883";

    // A unique name for your Spring Boot app's connection
    private static final String CLIENT_ID = "smartwash-backend";

    @Bean
    public MqttClient mqttClient() throws MqttException {

        // Create the client
        MqttClient client = new MqttClient(
                BROKER_URL,
                CLIENT_ID,
                new MemoryPersistence()
        );

        // Configure the connection
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true); // reconnects if connection drops
        options.setConnectionTimeout(30);
        options.setKeepAliveInterval(60);

        // Connect to Mosquitto
        client.connect(options);

        System.out.println("✅ SmartWASH: Connected to MQTT broker at "
                + BROKER_URL);

        return client;
    }
}
 */
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    private static final Logger log = LoggerFactory.getLogger(MqttConfig.class);

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Bean
    public MqttClient mqttClient() throws MqttException {

        MqttClient client = new MqttClient(
                brokerUrl,
                clientId,
                new MemoryPersistence()
        );

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(30);
        options.setKeepAliveInterval(60);

        // Add authentication for TTN
        options.setUserName(username);
        options.setPassword(password.toCharArray());

        client.connect(options);

        log.info("SmartWASH: Connected to MQTT broker: {}", brokerUrl);

        return client;
    }
}