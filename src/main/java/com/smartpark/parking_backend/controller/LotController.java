package com.smartpark.parking_backend.controller;


import com.smartpark.parking_backend.dto.ManualUpdateRequest;
import com.smartpark.parking_backend.model.ParkingLot;
import com.smartpark.parking_backend.model.ParkingSlot;
import com.smartpark.parking_backend.model.SlotStatus;
import com.smartpark.parking_backend.repository.ParkingLotRepository;
import com.smartpark.parking_backend.repository.ParkingSlotRepository;
import com.smartpark.parking_backend.service.ParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lots")
public class LotController {
    private final ParkingLotRepository lotRepo;
    private final ParkingSlotRepository slotRepo;
    private final ParkingService parkingService;

    public LotController(ParkingLotRepository lotRepo, ParkingSlotRepository slotRepo, ParkingService parkingService) {
        this.lotRepo = lotRepo;
        this.slotRepo = slotRepo;
        this.parkingService = parkingService;
    }

    @PostMapping
    public ResponseEntity<ParkingLot> createLot(@RequestBody ParkingLot lot) {
        ParkingLot saved = lotRepo.save(lot);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<ParkingLot>> listLots() {
        return ResponseEntity.ok(lotRepo.findAll());
    }

    @PostMapping("/{lotId}/slots")
    public ResponseEntity<ParkingSlot> addSlot(@PathVariable Long lotId, @RequestBody ParkingSlot slot) {
        ParkingLot lot = lotRepo.findById(lotId).orElseThrow();
        slot.setLot(lot);
        ParkingSlot saved = slotRepo.save(slot);
        // update counts
        parkingService.getSlotsForLot(lotId); // not used, but counts recalculated on next recalc
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{lotId}/slots")
    public ResponseEntity<List<ParkingSlot>> listSlots(@PathVariable Long lotId) {
        return ResponseEntity.ok(parkingService.getSlotsForLot(lotId));
    }

    /**
     * Manual override endpoint: operator can mark a slot FREE/OCCUPIED/UNKNOWN.
     */
    @PutMapping("/slots/{slotId}/manual")
    public ResponseEntity<?> manualUpdate(@PathVariable Long slotId, @RequestBody ManualUpdateRequest req) {
        SlotStatus status;
        try {
            status = SlotStatus.valueOf(req.status().toUpperCase());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid status");
        }
        try {
            ParkingSlot updated = parkingService.manualUpdateSlot(slotId, status, req.operator(), req.note());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body("Slot not found");
        }
    }
}

