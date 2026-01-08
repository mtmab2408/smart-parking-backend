package com.smartpark.parking_backend.model;

import java.util.List;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class ParkingLot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    @Column(nullable = false)
    private Double latitude;
    @Column(nullable = false)
    private Double longitude;

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("parkingLot")
    private List<ParkingSlot> parkingSlots;

    public ParkingLot(){}

    // getters / setters
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public void setParkingSpots(List<ParkingSlot> parkingSlots) { this.parkingSlots = parkingSlots; }

    public List<ParkingSlot> getParkingSlots() {
        return parkingSlots;
    }
}
