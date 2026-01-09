package com.smartpark.parking_backend.service;

import com.smartpark.parking_backend.model.ParkingLot;
import com.smartpark.parking_backend.model.ParkingSlot;
import com.smartpark.parking_backend.repository.ParkingLotRepository;
import com.smartpark.parking_backend.repository.ParkingSlotRepository;
import com.smartpark.parking_backend.websocket.SlotWebSocketPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParkingService {

    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final SlotWebSocketPublisher slotWebSocketPublisher;

    public ParkingService(
        ParkingSlotRepository parkingSlotRepository,
        ParkingLotRepository parkingLotRepository,
        SlotWebSocketPublisher slotWebSocketPublisher
    ){
        this.parkingLotRepository =parkingLotRepository;
        this.parkingSlotRepository =parkingSlotRepository;
        this.slotWebSocketPublisher = slotWebSocketPublisher;
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
        slotWebSocketPublisher.broadcastSlots(getAllSlots());
    }

    //slot management services
    public List <ParkingSlot> getAllSlots(){
        return parkingSlotRepository.findAll();
    }

    //i love this function's name addSlotToLot. goofy ahh 
    public ParkingSlot addSlotToLot(long lotId, ParkingSlot slot){
        ParkingLot lot = parkingLotRepository.findById(lotId).orElseThrow(() -> new RuntimeException("Lot not found"));
        slot.setParkingLot(lot);
        ParkingSlot saved = parkingSlotRepository.save(slot);
        slotWebSocketPublisher.broadcastSlots(getAllSlots());
        return saved;

    }

    public void deleteSlot(Long SlotId){
        parkingSlotRepository.deleteById(SlotId);
        slotWebSocketPublisher.broadcastSlots(getAllSlots());
    }

    public ParkingSlot updateSLotDetails(Long slotId, ParkingSlot newDetails){
        return parkingSlotRepository.findById(slotId)
        .map(existingSlot->{
            existingSlot.setSlotNumber(newDetails.getSlotNumber());
            existingSlot.setSensorId(newDetails.getSensorId());
            ParkingSlot saved = parkingSlotRepository.save(existingSlot);
            slotWebSocketPublisher.broadcastSlots(getAllSlots());
            return saved;
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
        ParkingSlot saved = parkingSlotRepository.save(slot);
        slotWebSocketPublisher.broadcastSlots(getAllSlots());
        return saved;
    }

    public int updateSlotsStatusBySensorId(String sensorId, boolean isOccupied) {
        if (sensorId == null || sensorId.trim().isEmpty()) {
            return 0;
        }
        List<ParkingSlot> slots = parkingSlotRepository.findAllBySensorId(sensorId);
        if (slots.isEmpty()) {
            return 0;
        }
        for (ParkingSlot slot : slots) {
            slot.setOccupied(isOccupied);
        }
        parkingSlotRepository.saveAll(slots);
        slotWebSocketPublisher.broadcastSlots(getAllSlots());
        return slots.size();
    }

    public int updateSlotStatusBySlotIdUsingSensor(Long slotId, boolean isOccupied) {
        ParkingSlot slot = parkingSlotRepository.findById(slotId)
            .orElseThrow(() -> new RuntimeException("Slot not Found"));
        String sensorId = slot.getSensorId();
        if (sensorId != null && !sensorId.trim().isEmpty()) {
            return updateSlotsStatusBySensorId(sensorId, isOccupied);
        }
        slot.setOccupied(isOccupied);
        parkingSlotRepository.save(slot);
        slotWebSocketPublisher.broadcastSlots(getAllSlots());
        return 1;
    }

      
}
