package com.smartpark.parking_backend.repository;

import com.smartpark.parking_backend.model.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    List<ParkingSlot> findAllBySensorId(String sensorId);
}
