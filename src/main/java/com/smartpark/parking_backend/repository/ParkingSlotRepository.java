package com.smartpark.parking_backend.repository;

import com.smartpark.parking_backend.model.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
}
