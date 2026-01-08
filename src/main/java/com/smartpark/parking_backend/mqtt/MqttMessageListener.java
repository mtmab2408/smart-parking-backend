package com.smartpark.parking_backend.mqtt;

import com.smartpark.parking_backend.service.ParkingService;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.JSONObject;

@Component
public class MqttMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(MqttMessageListener.class);

    private final ParkingService parkingService;

    public MqttMessageListener(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    public void processMessage(String topic, String payload) {
        System.out.println("\n");
        System.out.println("MQTT MESSAGE RECEIVED FROM SENSOR");
        System.out.println("Endpoint: " + topic);
        System.out.println("MQTT Response: " + payload);
        System.out.println("\n");

        try {
            JSONObject json = new JSONObject(payload);

            Long slotId = null;
            Boolean isOccupied = null;
            
            System.out.println("JSON Payload: " + json.toString());
            String spotValue = json.get("spot").toString().trim();
            if (!spotValue.matches("\\d+")) {
                // so we can extract digits from values like "spot1" or "spot-2"
                //this is a problem in embedded payload, discuss with awais
                spotValue = spotValue.replaceAll("\\D+", "");
            }
            if (!spotValue.isEmpty()) {
                slotId = Long.valueOf(spotValue);
            }
            Object statusValue = json.get("status");
            if (statusValue instanceof Boolean) {
                isOccupied = (Boolean) statusValue;
            } else if (statusValue instanceof Number) {
                isOccupied = ((Number) statusValue).intValue() != 0;
            } else {
                String statusString = statusValue.toString().trim();
                isOccupied = "1".equals(statusString) || "true".equalsIgnoreCase(statusString);
            }

            System.out.println("Parsed - SlotId: " + slotId + ", IsOccupied: " + isOccupied);

            if (slotId != null && isOccupied != null) {
                parkingService.updateSlotStatus(slotId, isOccupied);
                String statusString;
                if (isOccupied) {
                    statusString = "OCCUPIED";
                } else {
                    statusString = "FREE";
                }
                System.out.println("SUCCESS: Updated slot " + slotId + " to " + statusString);
            } else {
                System.out.println("SlotId=" + slotId + ", IsOccupied=" + isOccupied);
                System.out.println("Full JSON: " + json.toString(2));
            }

        } catch (Exception e) {
            System.out.println("ERROR: Failed to parse JSON");
            System.out.println("Error: " + e.getMessage());
            System.out.println("Raw payload (non-JSON): " + payload);
        }
    }
}
