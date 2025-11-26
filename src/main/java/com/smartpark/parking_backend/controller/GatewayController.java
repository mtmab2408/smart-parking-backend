package com.smartpark.parking_backend.controller;


import com.smartpark.parking_backend.dto.OccupancyEvent;
import com.smartpark.parking_backend.service.ParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gateway")
public class GatewayController {
    private final ParkingService parkingService;

    public GatewayController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping("/events")
    public ResponseEntity<?> receiveEvent(@RequestBody OccupancyEvent ev) {
        boolean ok = parkingService.handleOccupancyEvent(ev);
        if (ok) {
            return ResponseEntity.ok("Event processed");
        } else {
            return ResponseEntity.status(404).body("Sensor not mapped");
        }
    }
}

