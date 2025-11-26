package com.smartpark.parking_backend.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "parking_slot", uniqueConstraints = @UniqueConstraint(columnNames = {"lot_id", "slot_number"}))
public class ParkingSlot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lot_id")
    private ParkingLot lot;

    @Column(name = "slot_number")
    private String slotNumber; // e.g., A1

    private String sensorId;   // hardware id (optional)

    @Enumerated(EnumType.STRING)
    private SlotStatus status = SlotStatus.UNKNOWN;

    private Instant lastSeen;

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ParkingLot getLot() { return lot; }
    public void setLot(ParkingLot lot) { this.lot = lot; }

    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }

    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }

    public SlotStatus getStatus() { return status; }
    public void setStatus(SlotStatus status) { this.status = status; }

    public Instant getLastSeen() { return lastSeen; }
    public void setLastSeen(Instant lastSeen) { this.lastSeen = lastSeen; }
}
