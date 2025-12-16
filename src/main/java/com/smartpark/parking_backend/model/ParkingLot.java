package com.smartpark.parking_backend.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
public class ParkingLot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL)
    private List<ParkingSlot> parkingSlots;

    public ParkingLot(){}

    // getters / setters
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public void setParkingSpots(List<ParkingSlot> parkingSlots) { this.parkingSlots = parkingSlots; }
}
