package com.smartpark.parking_backend.websocket;

import com.smartpark.parking_backend.service.ParkingService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SlotWebSocketHandler extends TextWebSocketHandler {
    private final ParkingService parkingService;
    private final SlotWebSocketPublisher slotWebSocketPublisher;

    public SlotWebSocketHandler(
        ParkingService parkingService,
        SlotWebSocketPublisher slotWebSocketPublisher
    ) {
        this.parkingService = parkingService;
        this.slotWebSocketPublisher = slotWebSocketPublisher;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        slotWebSocketPublisher.register(session);
        slotWebSocketPublisher.sendSlotsTo(session, parkingService.getAllSlots());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        slotWebSocketPublisher.unregister(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        slotWebSocketPublisher.unregister(session);
    }
}
