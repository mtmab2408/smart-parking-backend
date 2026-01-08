package com.smartpark.parking_backend.service;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.smartpark.parking_backend.config.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Service
@ConditionalOnProperty(name = "mqtt.dummy.enabled", havingValue = "true")
public class DummyMqttPublisher {

    private static final Logger logger = LoggerFactory.getLogger(DummyMqttPublisher.class);

    private final MqttProperties mqttProperties;
    private Mqtt5BlockingClient publisherClient;
    private final Random random = new Random();

    private static final int[] SENSOR_SLOT_IDS = {1, 2};

    public DummyMqttPublisher(MqttProperties mqttProperties) {
        this.mqttProperties = mqttProperties;
    }

    @PostConstruct
    public void connect() {
        try {
            logger.info("Starting Dummy MQTT Publisher...");

            publisherClient = MqttClient.builder()
                    .useMqttVersion5()
                    .serverHost(mqttProperties.getBrokerHost())
                    .serverPort(mqttProperties.getBrokerPort())
                    .sslWithDefaultConfig()
                    .identifier("dummy-publisher-" + System.currentTimeMillis())
                    .build()
                    .toBlocking();

            publisherClient.connectWith()
                    .simpleAuth()
                    .username(mqttProperties.getClientUsername())
                    .password(mqttProperties.getClientPassword().getBytes())
                    .applySimpleAuth()
                    .send();

            logger.info("Dummy MQTT Publisher connected successfully!");

        } catch (Exception e) {
            logger.error("Failed to connect Dummy MQTT Publisher", e);
        }
    }

    @Scheduled(fixedRate = 5000)
    public void publishRandomSensorData() {
        if (publisherClient == null) {
            return;
        }

        try {
            for (int slotId : SENSOR_SLOT_IDS) {
                boolean isOccupied = random.nextBoolean();
                String payload = String.format("{\"spot\": %d, \"status\": %b}", slotId, isOccupied);
                String topic = "parking/sensor/" + slotId;

                publisherClient.publishWith()
                        .topic(topic)
                        .payload(payload.getBytes(StandardCharsets.UTF_8))
                        .send();

                System.out.println("\n>>> DUMMY SENSOR PUBLISHED >>>");
                System.out.println("Topic: " + topic);
                System.out.println("Payload: " + payload);
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
            }
        } catch (Exception e) {
            logger.error("Failed to publish dummy sensor data", e);
        }
    }

    @PreDestroy
    public void disconnect() {
        if (publisherClient != null) {
            try {
                publisherClient.disconnect();
                logger.info("Dummy MQTT Publisher disconnected");
            } catch (Exception e) {
                logger.error("Error disconnecting Dummy MQTT Publisher", e);
            }
        }
    }
}
