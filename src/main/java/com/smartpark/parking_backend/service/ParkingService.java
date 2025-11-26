package com.smartpark.parking_backend.service;



import com.smartpark.parking_backend.dto.OccupancyEvent;
import com.smartpark.parking_backend.model.ParkingSlot;
import com.smartpark.parking_backend.model.SlotStatus;
import com.smartpark.parking_backend.model.ParkingLot;
import com.smartpark.parking_backend.repository.ParkingLotRepository;
import com.smartpark.parking_backend.repository.ParkingSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingService {
    private final ParkingSlotRepository slotRepo;
    private final ParkingLotRepository lotRepo;

    public ParkingService(ParkingSlotRepository slotRepo, ParkingLotRepository lotRepo) {
        this.slotRepo = slotRepo;
        this.lotRepo = lotRepo;
    }

    /**
     * Handle occupancy event from gateway (sensor).
     * Returns true if matched to a slot and saved.
     */
    @Transactional
    public boolean handleOccupancyEvent(OccupancyEvent ev) {
        if (ev == null || ev.sensorId() == null) return false;
        Optional<ParkingSlot> maybe = slotRepo.findBySensorId(ev.sensorId());
        SlotStatus newStatus;
        try {
            newStatus = SlotStatus.valueOf(ev.status().toUpperCase());
        } catch (Exception e) {
            newStatus = SlotStatus.UNKNOWN;
        }
        Instant ts = ev.timestamp() == null ? Instant.now() : ev.timestamp();

        if (maybe.isPresent()) {
            ParkingSlot slot = maybe.get();
            slot.setStatus(newStatus);
            slot.setLastSeen(ts);
            slotRepo.save(slot);
            recalcLotCounts(slot.getLot().getId());
            return true;
        } else {
            // unknown sensor: we could log this or create a slot placeholder
            return false;
        }
    }

    @Transactional
    public ParkingSlot manualUpdateSlot(Long slotId, SlotStatus status, String operator, String note) {
        ParkingSlot slot = slotRepo.findById(slotId).orElseThrow(() -> new IllegalArgumentException("Slot not found"));
        slot.setStatus(status);
        slot.setLastSeen(Instant.now());
        // for now, we ignore operator/note persistence; can extend with audit table
        slotRepo.save(slot);
        recalcLotCounts(slot.getLot().getId());
        return slot;
    }

    public List<ParkingSlot> getSlotsForLot(Long lotId) {
        return slotRepo.findByLotId(lotId);
    }

    private void recalcLotCounts(Long lotId) {
        List<ParkingSlot> slots = slotRepo.findByLotId(lotId);
        ParkingLot lot = lotRepo.findById(lotId).orElseThrow();
        long free = slots.stream().filter(s -> s.getStatus() == SlotStatus.FREE).count();
        lot.setFreeSlots((int) free);
        lot.setTotalSlots(slots.size());
        lotRepo.save(lot);
    }
}
