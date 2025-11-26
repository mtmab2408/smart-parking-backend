package com.smartpark.parking_backend;

import com.smartpark.parking_backend.model.ParkingLot;
import com.smartpark.parking_backend.model.ParkingSlot;
import com.smartpark.parking_backend.model.SlotStatus;
import com.smartpark.parking_backend.repository.ParkingLotRepository;
import com.smartpark.parking_backend.repository.ParkingSlotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final ParkingLotRepository lotRepo;
    private final ParkingSlotRepository slotRepo;

    public DataLoader(ParkingLotRepository lotRepo, ParkingSlotRepository slotRepo) {
        this.lotRepo = lotRepo;
        this.slotRepo = slotRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        ParkingLot lot = new ParkingLot();
        lot.setName("Main Street Car Park");
        lot.setAddress("12 Main St");
        lot.setLatitude(48.8566);
        lot.setLongitude(2.3522);
        lot.setPricing("1.5 EUR/hr");
        lot = lotRepo.save(lot);

        ParkingSlot s1 = new ParkingSlot();
        s1.setLot(lot);
        s1.setSlotNumber("A1");
        s1.setSensorId("SENS-001");
        s1.setStatus(SlotStatus.FREE);
        slotRepo.save(s1);

        ParkingSlot s2 = new ParkingSlot();
        s2.setLot(lot);
        s2.setSlotNumber("A2");
        s2.setSensorId("SENS-002");
        s2.setStatus(SlotStatus.FREE);
        slotRepo.save(s2);

        ParkingSlot s3 = new ParkingSlot();
        s3.setLot(lot);
        s3.setSlotNumber("B1");
        // no sensor assigned (manual only)
        s3.setStatus(SlotStatus.UNKNOWN);
        slotRepo.save(s3);


        lot.setTotalSlots(3);
        lot.setFreeSlots(2);
        lotRepo.save(lot);
    }
}
