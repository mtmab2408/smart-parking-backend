package com.smartpark.parking_backend.repository;
import com.smartpark.parking_backend.model.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> { }
