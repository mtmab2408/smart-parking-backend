package com.smartpark.parking_backend.model;

import jakarta.persistence.*;

@Entity
public class ParkingSlot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer slotNumber; // e.g., 101

    private boolean isOccupied;
    private String sensorId;   // hardware id (optional)

    @ManyToOne
    @JoinColumn(name="parking_lot_id")
    private ParkingLot parkingLot;

    // getters / setters
    public Long getId() { return id; }

    public Integer getSlotNumber() { return slotNumber; }
    public void setSlotNumber(Integer slotNumber) { this.slotNumber = slotNumber; }

    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }

    public boolean getOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { this.isOccupied = occupied; }

    public void setParkingLot(ParkingLot parkingLot) {
    this.parkingLot = parkingLot;
}
}
