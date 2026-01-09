package com.smartpark.parking_backend.websocket;

import com.smartpark.parking_backend.model.ParkingSlot;
import java.util.List;

public record SlotUpdateMessage(String type, List<ParkingSlot> slots) {
}
