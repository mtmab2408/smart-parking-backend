package com.smartpark.parking_backend.service;

import com.smartpark.parking_backend.model.ParkingLot;
import com.smartpark.parking_backend.model.ParkingSlot;
import com.smartpark.parking_backend.repository.ParkingLotRepository;
import com.smartpark.parking_backend.repository.ParkingSlotRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParkingService {

    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingLotRepository parkingLotRepository;

    public ParkingService(ParkingSlotRepository parkingSlotRepository, ParkingLotRepository parkingLotRepository){
        this.parkingLotRepository =parkingLotRepository;
        this.parkingSlotRepository =parkingSlotRepository;
    }


    //Lot Management Services
    public List<ParkingLot> getAllLots(){
        return parkingLotRepository.findAll();
        }
    public ParkingLot createParkingLot(ParkingLot lot){
        return parkingLotRepository.save(lot); // iwill use this to create AND update lots
    }
    public void deleteParkingLot(Long id){
        parkingLotRepository.deleteById(id);
    }

    //slot management services
    public List <ParkingSlot> getAllSlots(){
        return parkingSlotRepository.findAll();
    }

    //i love this function's name addSlotToLot. goofy ahh 
    public ParkingSlot addSlotToLot(long lotId, ParkingSlot slot){
        ParkingLot lot = parkingLotRepository.findById(lotId).orElseThrow(() -> new RuntimeException("Lot not found"));
        slot.setParkingLot(lot);
        return parkingSlotRepository.save(slot);

    }

    public void deleteSlot(Long SlotId){
        parkingSlotRepository.deleteById(SlotId);
    }

    public ParkingSlot updateSLotDetails(Long slotId, ParkingSlot newDetails){
        return parkingSlotRepository.findById(slotId)
        .map(existingSlot->{
            existingSlot.setSlotNumber(newDetails.getSlotNumber());
            existingSlot.setSensorId(newDetails.getSensorId());
            return parkingSlotRepository.save(existingSlot);
        })
        .orElseThrow(()->new RuntimeException("Slot not found"));
    }

    //more operations for admins
    public Optional <ParkingSlot> getSlotById(Long id){
        return parkingSlotRepository.findById(id);
    }

    public ParkingSlot updateSlotStatus(Long slotId,boolean isOccupied){
        ParkingSlot slot = parkingSlotRepository.findById(slotId).orElseThrow(() -> new RuntimeException("Slot not Found"));
        slot.setOccupied(isOccupied);
        return parkingSlotRepository.save(slot);
    }

      
}
