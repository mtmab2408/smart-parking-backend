package com.smartpark.parking_backend.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartpark.parking_backend.model.ParkingSlot;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class SlotWebSocketPublisher {
    private final ObjectMapper objectMapper;
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    public SlotWebSocketPublisher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void register(WebSocketSession session) {
        sessions.add(session);
    }

    public void unregister(WebSocketSession session) {
        sessions.remove(session);
    }

    public void sendSlotsTo(WebSocketSession session, List<ParkingSlot> slots) {
        sendMessage(session, new SlotUpdateMessage("slots", slots));
    }

    public void broadcastSlots(List<ParkingSlot> slots) {
        SlotUpdateMessage payload = new SlotUpdateMessage("slots", slots);
        for (WebSocketSession session : sessions) {
            sendMessage(session, payload);
        }
    }

    private void sendMessage(WebSocketSession session, SlotUpdateMessage payload) {
        if (session == null || !session.isOpen()) {
            sessions.remove(session);
            return;
        }
        try {
            String body = objectMapper.writeValueAsString(payload);
            session.sendMessage(new TextMessage(body));
        } catch (IOException ex) {
            sessions.remove(session);
        }
    }
}
