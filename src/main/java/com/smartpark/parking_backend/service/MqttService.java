package com.smartpark.parking_backend.service;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.smartpark.parking_backend.config.MqttProperties;
import com.smartpark.parking_backend.mqtt.MqttMessageListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class MqttService {

    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);

    private final MqttProperties mqttProperties;
    private final MqttMessageListener messageListener;
    private Mqtt5BlockingClient client;

    public MqttService(MqttProperties mqttProperties, MqttMessageListener messageListener) {
        this.mqttProperties = mqttProperties;
        this.messageListener = messageListener;
    }

    @PostConstruct
    public void connect() {
        if (!mqttProperties.isEnabled()) {
            logger.info("MQTT is disabled");
            return;
        }

        if (mqttProperties.getBrokerHost() == null || mqttProperties.getClientUsername() == null) {
            logger.warn("MQTT configuration is incomplete");
            return;
        }

        try {
            logger.info("Connecting to MQTT broker at " + mqttProperties.getBrokerHost() + ":" + mqttProperties.getBrokerPort());

            client = MqttClient.builder()
                    .useMqttVersion5()
                    .serverHost(mqttProperties.getBrokerHost())
                    .serverPort(mqttProperties.getBrokerPort())
                    .sslWithDefaultConfig()
                    .build()
                    .toBlocking();

            client.connectWith()
                    .simpleAuth()
                    .username(mqttProperties.getClientUsername())
                    .password(mqttProperties.getClientPassword().getBytes())
                    .applySimpleAuth()
                    .send();

            logger.info("Connected to MQTT broker successfully");

            client.subscribeWith()
                    .topicFilter(mqttProperties.getTopicSubscribe())
                    .send();

            logger.info("Subscribed to topic: " + mqttProperties.getTopicSubscribe());

            startListening();

        } catch (Exception e) {
            logger.error("Failed to connect to MQTT broker", e);
        }
    }

    private void startListening() {
        Thread thread = new Thread(() -> {
            try {
                client.toAsync().publishes(com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL, publish -> {
                    String topic = publish.getTopic().toString();
                    byte[] payloadBytes = new byte[publish.getPayload().get().remaining()];
                    publish.getPayload().get().get(payloadBytes);
                    String payload = new String(payloadBytes);

                    System.out.println("\n=== MQTT DATA RECEIVED ===");
                    System.out.println("Endpoint: " + topic);
                    System.out.println("MQTT Response: " + payload);
                    System.out.println("===========================\n");

                    messageListener.processMessage(topic, payload);
                });
            } catch (Exception e) {
                logger.error("Error listening for messages", e);
                e.printStackTrace();
            }
        });

        thread.setDaemon(true);
        thread.setName("mqtt-listener");
        thread.start();
    }

    @PreDestroy
    public void disconnect() {
        if (client != null) {
            try {
                client.disconnect();
                logger.info("Disconnected from MQTT broker");
            } catch (Exception e) {
                logger.error("Error disconnecting", e);
            }
        }
    }
}
