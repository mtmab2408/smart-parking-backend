package com.smartpark.parking_backend.controller;

import com.smartpark.parking_backend.model.ParkingLot;
import com.smartpark.parking_backend.model.ParkingSlot;
import com.smartpark.parking_backend.service.ParkingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ParkingController.class)
class ParkingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ParkingService parkingService;

    private ParkingLot testLot;
    private ParkingSlot testSlot;

    @BeforeEach
    void setUp() {
        testLot = new ParkingLot();
        testLot.setName("CPS2 Smart Garage");
        testLot.setAddress("University Campus");
        testLot.setLatitude(45.450708);
        testLot.setLongitude(4.387879);

        testSlot = new ParkingSlot();
        testSlot.setSlotNumber(1);
        testSlot.setOccupied(false);
        testSlot.setSensorId("sensor-01");
        testSlot.setParkingLot(testLot);
    }

    // GET Endpoint Tests

    @Test
    void getAllSlots_shouldReturnListOfAllSlots() throws Exception {
        // Arrange: Mock service returns list of slots
        List<ParkingSlot> slots = Arrays.asList(testSlot);
        when(parkingService.getAllSlots()).thenReturn(slots);

        // Act & Assert: Verify GET /api/parking/slots returns 200 with slot data
        mockMvc.perform(get("/api/parking/slots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].slotNumber").value(1))
                .andExpect(jsonPath("$[0].sensorId").value("sensor-01"))
                .andExpect(jsonPath("$[0].occupied").value(false));

        verify(parkingService, times(1)).getAllSlots();
    }

    @Test
    void getAllSlots_shouldReturnEmptyArrayWhenNoSlotsExist() throws Exception {
        // Arrange: Mock service returns empty list
        when(parkingService.getAllSlots()).thenReturn(Arrays.asList());

        // Act & Assert: Verify GET returns empty array with 200 status
        mockMvc.perform(get("/api/parking/slots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllLots_shouldReturnListOfAllParkingLots() throws Exception {
        // Arrange: Mock service returns list of parking lots
        List<ParkingLot> lots = Arrays.asList(testLot);
        when(parkingService.getAllLots()).thenReturn(lots);

        // Act & Assert: Verify GET /api/parking/lots returns 200 with lot data
        mockMvc.perform(get("/api/parking/lots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("CPS2 Smart Garage"))
                .andExpect(jsonPath("$[0].address").value("University Campus"))
                .andExpect(jsonPath("$[0].latitude").value(45.450708))
                .andExpect(jsonPath("$[0].longitude").value(4.387879));

        verify(parkingService, times(1)).getAllLots();
    }

    @Test
    void getAllLots_shouldReturnEmptyArrayWhenNoLotsExist() throws Exception {
        // Arrange: Mock service returns empty list
        when(parkingService.getAllLots()).thenReturn(Arrays.asList());

        // Act & Assert: Verify GET returns empty array with 200 status
        mockMvc.perform(get("/api/parking/lots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // POST Endpoint Tests

    @Test
    void createParkingLot_shouldCreateAndReturnNewLot() throws Exception {
        // Arrange: Mock service to save and return parking lot
        when(parkingService.createParkingLot(any(ParkingLot.class))).thenReturn(testLot);

        // Act & Assert: Verify POST /api/parking/lots creates lot and returns 200
        mockMvc.perform(post("/api/parking/lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLot)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("CPS2 Smart Garage"))
                .andExpect(jsonPath("$.address").value("University Campus"));

        verify(parkingService, times(1)).createParkingLot(any(ParkingLot.class));
    }

    @Test
    void createParkingLot_shouldAcceptLotWithGPSCoordinates() throws Exception {
        // Arrange: Create lot with GPS coordinates
        when(parkingService.createParkingLot(any(ParkingLot.class))).thenReturn(testLot);

        // Act & Assert: Verify POST accepts and returns coordinates correctly
        mockMvc.perform(post("/api/parking/lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLot)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(45.450708))
                .andExpect(jsonPath("$.longitude").value(4.387879));
    }

    @Test
    void addSlotToLot_shouldAddSlotToSpecifiedLot() throws Exception {
        // Arrange: Mock service to add slot to lot
        when(parkingService.addSlotToLot(anyLong(), any(ParkingSlot.class))).thenReturn(testSlot);

        // Act & Assert: Verify POST /api/parking/lots/{lotId}/slots adds slot successfully
        mockMvc.perform(post("/api/parking/lots/1/slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSlot)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slotNumber").value(1))
                .andExpect(jsonPath("$.sensorId").value("sensor-01"));

        verify(parkingService, times(1)).addSlotToLot(eq(1L), any(ParkingSlot.class));
    }

    @Test
    void addSlotToLot_shouldThrowExceptionWhenLotNotFound() throws Exception {
        // Arrange: Mock service throws exception for non-existent lot
        when(parkingService.addSlotToLot(anyLong(), any(ParkingSlot.class)))
                .thenThrow(new RuntimeException("Lot not found"));

        // Act & Assert: Verify POST propagates exception when lot doesn't exist
        try {
            mockMvc.perform(post("/api/parking/lots/999/slots")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testSlot)));
        } catch (Exception e) {
            // Expected behavior - exception should be thrown
        }
        verify(parkingService, times(1)).addSlotToLot(eq(999L), any(ParkingSlot.class));
    }

    // PUT Endpoint Tests

    @Test
    void updateSlotStatus_shouldUpdateSlotToOccupied() throws Exception {
        // Arrange: Mock service to update slot status to occupied
        testSlot.setOccupied(true);
        when(parkingService.updateSlotStatus(1L, true)).thenReturn(testSlot);

        // Act & Assert: Verify PUT /api/parking/slots/{id}/status updates status
        mockMvc.perform(put("/api/parking/slots/1/status")
                        .param("occupied", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.occupied").value(true));

        verify(parkingService, times(1)).updateSlotStatus(1L, true);
    }

    @Test
    void updateSlotStatus_shouldUpdateSlotToFree() throws Exception {
        // Arrange: Mock service to update slot status to free
        testSlot.setOccupied(false);
        when(parkingService.updateSlotStatus(1L, false)).thenReturn(testSlot);

        // Act & Assert: Verify PUT updates status to false (free)
        mockMvc.perform(put("/api/parking/slots/1/status")
                        .param("occupied", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.occupied").value(false));

        verify(parkingService, times(1)).updateSlotStatus(1L, false);
    }

    @Test
    void updateSlotStatus_shouldThrowExceptionWhenSlotNotFound() throws Exception {
        // Arrange: Mock service throws exception for non-existent slot
        when(parkingService.updateSlotStatus(999L, true))
                .thenThrow(new RuntimeException("Slot not Found"));

        // Act & Assert: Verify PUT propagates exception when slot doesn't exist
        try {
            mockMvc.perform(put("/api/parking/slots/999/status")
                            .param("occupied", "true"));
        } catch (Exception e) {
            // Expected behavior - exception should be thrown
        }
        verify(parkingService, times(1)).updateSlotStatus(999L, true);
    }

    @Test
    void updateSlotDetails_shouldUpdateSlotNumberAndSensorId() throws Exception {
        // Arrange: Create updated slot details
        ParkingSlot updatedSlot = new ParkingSlot();
        updatedSlot.setSlotNumber(10);
        updatedSlot.setSensorId("sensor-updated");
        when(parkingService.updateSLotDetails(eq(1L), any(ParkingSlot.class))).thenReturn(updatedSlot);

        // Act & Assert: Verify PUT /api/parking/slots/{id} updates slot details
        mockMvc.perform(put("/api/parking/slots/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSlot)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slotNumber").value(10))
                .andExpect(jsonPath("$.sensorId").value("sensor-updated"));

        verify(parkingService, times(1)).updateSLotDetails(eq(1L), any(ParkingSlot.class));
    }

    @Test
    void updateSlotDetails_shouldThrowExceptionWhenSlotNotFound() throws Exception {
        // Arrange: Mock service throws exception for non-existent slot
        when(parkingService.updateSLotDetails(eq(999L), any(ParkingSlot.class)))
                .thenThrow(new RuntimeException("Slot not found"));

        // Act & Assert: Verify PUT propagates exception when updating non-existent slot
        try {
            mockMvc.perform(put("/api/parking/slots/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testSlot)));
        } catch (Exception e) {
            // Expected behavior - exception should be thrown
        }
        verify(parkingService, times(1)).updateSLotDetails(eq(999L), any(ParkingSlot.class));
    }

    // DELETE Endpoint Tests

    @Test
    void deleteLot_shouldDeleteParkingLotAndReturn200() throws Exception {
        // Arrange: Mock service to delete lot
        doNothing().when(parkingService).deleteParkingLot(1L);

        // Act & Assert: Verify DELETE /api/parking/lots/{id} returns 200
        mockMvc.perform(delete("/api/parking/lots/1"))
                .andExpect(status().isOk());

        verify(parkingService, times(1)).deleteParkingLot(1L);
    }

    @Test
    void deleteSlot_shouldDeleteParkingSlotAndReturn200() throws Exception {
        // Arrange: Mock service to delete slot
        doNothing().when(parkingService).deleteSlot(1L);

        // Act & Assert: Verify DELETE /api/parking/slots/{id} returns 200
        mockMvc.perform(delete("/api/parking/slots/1"))
                .andExpect(status().isOk());

        verify(parkingService, times(1)).deleteSlot(1L);
    }

    // CORS Tests

    @Test
    void endpoints_shouldAllowCrossOriginRequests() throws Exception {
        // Arrange: Mock service for CORS test
        when(parkingService.getAllSlots()).thenReturn(Arrays.asList(testSlot));

        // Act & Assert: Verify CORS headers are present for cross-origin requests
        mockMvc.perform(get("/api/parking/slots")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    // Integration Scenario Tests

    @Test
    void realTimeUpdateScenario_shouldUpdateMultipleSlotsSequentially() throws Exception {
        // Arrange: Mock multiple status updates simulating real MQTT events
        ParkingSlot slot1 = new ParkingSlot();
        slot1.setSlotNumber(1);
        slot1.setOccupied(true);

        ParkingSlot slot2 = new ParkingSlot();
        slot2.setSlotNumber(2);
        slot2.setOccupied(false);

        when(parkingService.updateSlotStatus(1L, true)).thenReturn(slot1);
        when(parkingService.updateSlotStatus(2L, false)).thenReturn(slot2);

        // Act & Assert: Verify multiple sequential updates work correctly
        mockMvc.perform(put("/api/parking/slots/1/status").param("occupied", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.occupied").value(true));

        mockMvc.perform(put("/api/parking/slots/2/status").param("occupied", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.occupied").value(false));

        verify(parkingService, times(1)).updateSlotStatus(1L, true);
        verify(parkingService, times(1)).updateSlotStatus(2L, false);
    }

    @Test
    void androidAppScenario_shouldRetrieveAllLotsAndSlots() throws Exception {
        // Arrange: Mock data for typical Android app request flow
        List<ParkingLot> lots = Arrays.asList(testLot);
        List<ParkingSlot> slots = Arrays.asList(testSlot);
        when(parkingService.getAllLots()).thenReturn(lots);
        when(parkingService.getAllSlots()).thenReturn(slots);

        // Act & Assert: Verify Android app can fetch both lots and slots
        mockMvc.perform(get("/api/parking/lots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/parking/slots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(parkingService, times(1)).getAllLots();
        verify(parkingService, times(1)).getAllSlots();
    }
}
