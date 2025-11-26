package com.smartpark.parking_backend.dto;

import java.time.Instant;

public record OccupancyEvent(String gatewayId, String sensorId, String status, Instant timestamp) {

}
