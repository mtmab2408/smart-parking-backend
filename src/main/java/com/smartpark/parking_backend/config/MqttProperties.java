package com.smartpark.parking_backend.config;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class MqttProperties {

    @Value("${mqtt.broker.host:}")
    private String brokerHost;

    @Value("${mqtt.broker.port:8883}")
    private int brokerPort;

    @Value("${mqtt.client.username:}")
    private String clientUsername;

    @Value("${mqtt.client.password:}")
    private String clientPassword;

    @Value("${mqtt.topic.subscribe:parking/sensors/#}")
    private String topicSubscribe;

    @Value("${mqtt.enabled:true}")
    private boolean enabled;

    public String getBrokerHost() {
        return brokerHost;
    }

    public int getBrokerPort() {
        return brokerPort;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public String getClientPassword() {
        return clientPassword;
    }

    public String getTopicSubscribe() {
        return topicSubscribe;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
