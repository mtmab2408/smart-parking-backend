package com.smartpark.parking_backend.config;

import com.smartpark.parking_backend.model.Admin;
import com.smartpark.parking_backend.model.ParkingLot;
import com.smartpark.parking_backend.model.ParkingSlot;
import com.smartpark.parking_backend.repository.AdminRepository;
import com.smartpark.parking_backend.repository.ParkingLotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final ParkingLotRepository parkingLotRepository;
    private final AdminRepository adminRepository;

    public DataLoader(ParkingLotRepository parkingLotRepository, AdminRepository adminRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        boolean seeded = false;

        if (parkingLotRepository.count() == 0) {
            System.out.println("--- SEEDING PARKING DATA ---");

            ParkingLot lot = new ParkingLot();
            lot.setName("CPS2 Smart Garage");
            lot.setAddress("University Campus");
            lot.setLatitude(40.7128);
            lot.setLongitude(-74.0060);

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
            
            parkingLotRepository.save(lot);
            seeded = true;
        }

        if (adminRepository.count() == 0) {
            System.out.println("--- SEEDING SUPER ADMIN ---");
            Admin superAdmin = new Admin("superadmin", "superadmin123");
            adminRepository.save(superAdmin);
            seeded = true;
        }

        if (seeded) {
            System.out.println("--- DATA SEEDED ---");
        }
    }
}
