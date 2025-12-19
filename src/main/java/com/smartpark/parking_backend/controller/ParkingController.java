package com.smartpark.parking_backend.controller;

import com.smartpark.parking_backend.model.ParkingLot;
import com.smartpark.parking_backend.model.ParkingSlot;
import com.smartpark.parking_backend.service.ParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;




@RestController
@RequestMapping("/api/parking")
@CrossOrigin(origins = "*")
public class ParkingController {
    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService){
        this.parkingService = parkingService;
    }

    //All get Requests

    @GetMapping("/slots")
    public List<ParkingSlot> getAllSlots() {
        return parkingService.getAllSlots();
    }

    @GetMapping("/lots")
    public List<ParkingLot> getAllLots() {
        return parkingService.getAllLots();
    }

    //Create Requests

    @PostMapping("/lots")
    public ParkingLot createParkingLot(@RequestBody ParkingLot lot) {
        return parkingService.createParkingLot(lot);
    }

    @PostMapping("/lots/{lotId}/slots")
    public ParkingSlot addSlotToLot(@PathVariable Long lotId, @RequestBody ParkingSlot slot) {
        return parkingService.addSlotToLot(lotId,slot);
    }
    
    //Updating data
    @PutMapping("/slots/{id}/status")
    public ParkingSlot updateSlotStatus(@PathVariable Long id, @RequestParam boolean occupied) {
    return parkingService.updateSlotStatus(id, occupied);    
    }

    @PutMapping("slots/{id}")
    public ParkingSlot updateSlotDetails(@PathVariable Long id, @RequestBody ParkingSlot slotDetails) {
    return parkingService.updateSLotDetails(id,slotDetails);    
    }
    
    //deleting data
       //deleting data
    @DeleteMapping("/slots/{id}")
    public ResponseEntity<?> deleteSlot(@PathVariable Long id){
        parkingService.deleteSlot(id);
        return ResponseEntity.ok().build();
    }

    


}
