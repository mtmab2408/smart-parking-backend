package com.smartpark.parking_backend.config;



import com.smartpark.parking_backend.model.ParkingLot;
import com.smartpark.parking_backend.model.ParkingSlot;
import com.smartpark.parking_backend.repository.ParkingLotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final ParkingLotRepository repository;

    public DataLoader(ParkingLotRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() > 0) {
            return;
        }

        System.out.println("--- SEEDING DATA ---");

        ParkingLot lot = new ParkingLot();
        lot.setName("CPS2 Smart Garage");
        lot.setAddress("University Campus");

        ParkingSlot s1 = new ParkingSlot();
        s1.setSlotNumber(1);
        s1.setOccupied(false);
        s1.setSensorId("sensor-01");
        s1.setParkingLot(lot);

        ParkingSlot s2 = new ParkingSlot();
        s2.setSlotNumber(2);
        s2.setOccupied(false);
        s2.setSensorId("sensor-02");
        s2.setParkingLot(lot);

        ParkingSlot s3 = new ParkingSlot();
        s3.setSlotNumber(3);
        s3.setOccupied(true);
        s3.setSensorId(null); 
        s3.setParkingLot(lot);

        lot.setParkingSpots(List.of(s1, s2, s3));
        
        repository.save(lot);
        System.out.println("--- DATA SEEDED ---");
    }
}