package com.smartpark.parking_backend.repository;

import com.smartpark.parking_backend.model.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    List<ParkingSlot> findByLotId(Long lotId);
    Optional<ParkingSlot> findBySensorId(String sensorId);
}
