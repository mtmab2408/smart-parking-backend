package com.smartpark.parking_backend.mqtt;

import com.smartpark.parking_backend.model.ParkingSlot;
import com.smartpark.parking_backend.service.ParkingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MqttMessageListenerTest {

    @Mock
    private ParkingService parkingService;

    @InjectMocks
    private MqttMessageListener mqttMessageListener;

    private ParkingSlot testSlot;

    @BeforeEach
    void setUp() {
        testSlot = new ParkingSlot();
        testSlot.setSlotNumber(1);
        testSlot.setOccupied(false);
        // Lenient stubs for all positive test cases
        lenient().when(parkingService.updateSlotStatus(anyLong(), anyBoolean())).thenReturn(testSlot);
        lenient().when(parkingService.updateSlotsStatusBySensorId(anyString(), anyBoolean())).thenReturn(1);
        lenient().when(parkingService.updateSlotStatusBySlotIdUsingSensor(anyLong(), anyBoolean())).thenReturn(1);
    }

    // Standard MQTT Message Tests

    @Test
    void processMessage_shouldUpdateSlotStatusWhenValidJSONWithBooleanStatus() {
        // Arrange: Create valid MQTT payload with boolean status
        String payload = "{\"spot\":1,\"status\":true}";

        // Act: Process MQTT message from sensor
        mqttMessageListener.processMessage("parking/sensor/1", payload);

        // Assert: Verify service was called to update slot status to occupied
        verify(parkingService, times(1)).updateSlotStatusBySlotIdUsingSensor(1L, true);
    }

    @Test
    void processMessage_shouldUpdateSlotToFreeWhenStatusIsFalse() {
        // Arrange: Create MQTT payload indicating free parking spot
        String payload = "{\"spot\":2,\"status\":false}";

        // Act: Process message with status false
        mqttMessageListener.processMessage("parking/sensor/2", payload);

        // Assert: Verify slot is marked as free
        verify(parkingService, times(1)).updateSlotStatusBySlotIdUsingSensor(2L, false);
    }

    // Numeric Status Value Tests (Embedded System Compatibility)

    @Test
    void processMessage_shouldConvertNumeric1ToOccupiedStatus() {
        // Arrange: Create payload with numeric status (1 = occupied)
        String payload = "{\"spot\":1,\"status\":1}";

        // Act: Process message with numeric status value
        mqttMessageListener.processMessage("parking/sensor/1", payload);

        // Assert: Verify numeric 1 is interpreted as occupied (true)
        verify(parkingService, times(1)).updateSlotStatusBySlotIdUsingSensor(1L, true);
    }

    @Test
    void processMessage_shouldConvertNumeric0ToFreeStatus() {
        // Arrange: Create payload with numeric status (0 = free)
        String payload = "{\"spot\":1,\"status\":0}";

        // Act: Process message with numeric zero value
        mqttMessageListener.processMessage("parking/sensor/1", payload);

        // Assert: Verify numeric 0 is interpreted as free (false)
        verify(parkingService, times(1)).updateSlotStatusBySlotIdUsingSensor(1L, false);
    }

    @Test
    void processMessage_shouldHandleStringStatus1AsOccupied() {
        // Arrange: Create payload with string "1" status value
        String payload = "{\"spot\":1,\"status\":\"1\"}";

        // Act: Process message with string status
        mqttMessageListener.processMessage("parking/sensor/1", payload);

        // Assert: Verify string "1" is converted to occupied (true)
        verify(parkingService, times(1)).updateSlotStatusBySlotIdUsingSensor(1L, true);
    }

    @Test
    void processMessage_shouldHandleStringStatusTrueAsOccupied() {
        // Arrange: Create payload with string "true" status value
        String payload = "{\"spot\":1,\"status\":\"true\"}";

        // Act: Process message with string "true"
        mqttMessageListener.processMessage("parking/sensor/1", payload);

        // Assert: Verify string "true" is converted to occupied
        verify(parkingService, times(1)).updateSlotStatusBySlotIdUsingSensor(1L, true);
    }

    @Test
    void processMessage_shouldHandleStringStatusFalseAsFree() {
        // Arrange: Create payload with string "false" status value
        String payload = "{\"spot\":1,\"status\":\"false\"}";

        // Act: Process message with string "false"
        mqttMessageListener.processMessage("parking/sensor/1", payload);

        // Assert: Verify string "false" is converted to free status
        verify(parkingService, times(1)).updateSlotStatusBySlotIdUsingSensor(1L, false);
    }

    @Test
    void processMessage_shouldUpdateAllSlotsBySensorIdWhenProvided() {
        // Arrange: Payload includes sensorId to update all matching slots
        String payload = "{\"sensorId\":\"sensor-01\",\"spot\":1,\"status\":true}";

        // Act: Process message with sensorId
        mqttMessageListener.processMessage("parking/sensor/sensor-01", payload);

        // Assert: Sensor ID path is used and slot-id path is skipped
        verify(parkingService, times(1)).updateSlotsStatusBySensorId("sensor-01", true);
        verify(parkingService, never()).updateSlotStatusBySlotIdUsingSensor(anyLong(), anyBoolean());
    }

    @Test
    void processMessage_shouldFallbackToSlotIdWhenSensorIdHasNoMatches() {
        // Arrange: Force sensor-id path to return zero updates
        when(parkingService.updateSlotsStatusBySensorId("sensor-404", true)).thenReturn(0);
        String payload = "{\"sensorId\":\"sensor-404\",\"spot\":2,\"status\":true}";

        // Act: Process message with unknown sensorId
        mqttMessageListener.processMessage("parking/sensor/sensor-404", payload);

        // Assert: Falls back to slot-id update
        verify(parkingService, times(1)).updateSlotsStatusBySensorId("sensor-404", true);
        verify(parkingService, times(1)).updateSlotStatusBySlotIdUsingSensor(2L, true);
    }

    // Embedded System Payload Format Tests

    @Test
    void processMessage_shouldExtractDigitsFromSpotValue() {
        // Arrange: Create payload with "spot1" instead of numeric 1
        String payload = "{\"spot\":\"spot1\",\"status\":true}";

        // Act: Process message with non-numeric spot identifier
        mqttMessageListener.processMessage("parking/sensor/1", payload);

        // Assert: Verify digits are extracted from "spot1" to get slot ID 1
        verify(parkingService, times(1)).updateSlotStatus(1L, true);
    }

    @Test
    void processMessage_shouldExtractDigitsFromSpotValueWithHyphen() {
        // Arrange: Create payload with "spot-2" format from embedded system
        String payload = "{\"spot\":\"spot-2\",\"status\":false}";

        // Act: Process message with hyphenated spot identifier
        mqttMessageListener.processMessage("parking/sensor/2", payload);

        // Assert: Verify digits are extracted from "spot-2" to get slot ID 2
        verify(parkingService, times(1)).updateSlotStatus(2L, false);
    }

    @Test
    void processMessage_shouldHandleNumericSpotAsString() {
        // Arrange: Create payload with numeric spot as string "3"
        String payload = "{\"spot\":\"3\",\"status\":true}";

        // Act: Process message with string numeric spot
        mqttMessageListener.processMessage("parking/sensor/3", payload);

        // Assert: Verify string "3" is parsed to slot ID 3
        verify(parkingService, times(1)).updateSlotStatus(3L, true);
    }

    // Multiple Slot Updates Test

    @Test
    void processMessage_shouldHandleMultipleSequentialUpdates() {
        // Arrange: Create multiple MQTT messages for different slots
        String payload1 = "{\"spot\":1,\"status\":true}";
        String payload2 = "{\"spot\":2,\"status\":false}";
        String payload3 = "{\"spot\":3,\"status\":true}";

        // Act: Process multiple sequential messages from different sensors
        mqttMessageListener.processMessage("parking/sensor/1", payload1);
        mqttMessageListener.processMessage("parking/sensor/2", payload2);
        mqttMessageListener.processMessage("parking/sensor/3", payload3);

        // Assert: Verify all three updates were processed correctly
        verify(parkingService, times(1)).updateSlotStatus(1L, true);
        verify(parkingService, times(1)).updateSlotStatus(2L, false);
        verify(parkingService, times(1)).updateSlotStatus(3L, true);
    }

    // Invalid JSON Tests

    @Test
    void processMessage_shouldHandleInvalidJSONGracefully() {
        // Arrange: Create malformed JSON payload
        String invalidPayload = "this is not json";

        // Act: Process invalid JSON message
        mqttMessageListener.processMessage("parking/sensor/1", invalidPayload);

        // Assert: Verify service is never called when JSON parsing fails
        verify(parkingService, never()).updateSlotStatus(anyLong(), anyBoolean());
    }

    @Test
    void processMessage_shouldHandleEmptyPayload() {
        // Arrange: Create empty payload
        String emptyPayload = "";

        // Act: Process empty message
        mqttMessageListener.processMessage("parking/sensor/1", emptyPayload);

        // Assert: Verify service is not called for empty payload
        verify(parkingService, never()).updateSlotStatus(anyLong(), anyBoolean());
    }

    @Test
    void processMessage_shouldHandleMissingSpotField() {
        // Arrange: Create JSON without "spot" field
        String payload = "{\"status\":true}";

        // Act: Process message missing required spot field
        mqttMessageListener.processMessage("parking/sensor/1", payload);

        // Assert: Verify service is not called when spot field is missing
        verify(parkingService, never()).updateSlotStatus(anyLong(), anyBoolean());
    }

    @Test
    void processMessage_shouldHandleMissingStatusField() {
        // Arrange: Create JSON without "status" field
        String payload = "{\"spot\":1}";

        // Act: Process message missing required status field
        mqttMessageListener.processMessage("parking/sensor/1", payload);

        // Assert: Verify service is not called when status field is missing
        verify(parkingService, never()).updateSlotStatus(anyLong(), anyBoolean());
    }

    @Test
    void processMessage_shouldHandleNullPayload() {
        // Arrange: Prepare for null payload
        String nullPayload = null;

        // Act: Process null message payload
        mqttMessageListener.processMessage("parking/sensor/1", nullPayload);

        // Assert: Verify service is not called for null payload
        verify(parkingService, never()).updateSlotStatus(anyLong(), anyBoolean());
    }

    // Whitespace Handling Tests

    @Test
    void processMessage_shouldTrimWhitespaceFromSpotValue() {
        // Arrange: Create payload with whitespace in spot value
        String payload = "{\"spot\":\" 1 \",\"status\":true}";

        // Act: Process message with whitespace
        mqttMessageListener.processMessage("parking/sensor/1", payload);

        // Assert: Verify whitespace is trimmed and slot is updated
        verify(parkingService, times(1)).updateSlotStatus(1L, true);
    }

    @Test
    void processMessage_shouldTrimWhitespaceFromStatusString() {
        // Arrange: Create payload with whitespace in status value
        String payload = "{\"spot\":1,\"status\":\" true \"}";

        // Act: Process message with whitespace in status
        mqttMessageListener.processMessage("parking/sensor/1", payload);

        // Assert: Verify whitespace is trimmed and status is correctly parsed
        verify(parkingService, times(1)).updateSlotStatus(1L, true);
    }

    // Real-World Scenario Tests

    @Test
    void processMessage_shouldSimulateCarParkingEvent() {
        // Arrange: Create realistic MQTT message from ultrasonic sensor
        String payload = "{\"spot\":5,\"status\":true}";

        // Act: Simulate car parking in slot 5 triggering sensor
        mqttMessageListener.processMessage("parking/cps2/slot5", payload);

        // Assert: Verify slot 5 is marked as occupied
        verify(parkingService, times(1)).updateSlotStatus(5L, true);
    }

    @Test
    void processMessage_shouldSimulateCarLeavingEvent() {
        // Arrange: Create realistic MQTT message when car leaves
        String payload = "{\"spot\":5,\"status\":false}";

        // Act: Simulate car leaving slot 5 triggering sensor
        mqttMessageListener.processMessage("parking/cps2/slot5", payload);

        // Assert: Verify slot 5 is marked as free
        verify(parkingService, times(1)).updateSlotStatus(5L, false);
    }

    @Test
    void processMessage_shouldHandleRapidStatusChanges() {
        // Arrange: Create sequence simulating rapid sensor changes
        String occupied = "{\"spot\":1,\"status\":true}";
        String free = "{\"spot\":1,\"status\":false}";

        // Act: Simulate rapid status changes on same slot
        mqttMessageListener.processMessage("parking/sensor/1", occupied);
        mqttMessageListener.processMessage("parking/sensor/1", free);
        mqttMessageListener.processMessage("parking/sensor/1", occupied);

        // Assert: Verify all rapid changes are processed in order
        verify(parkingService, times(2)).updateSlotStatus(1L, true);
        verify(parkingService, times(1)).updateSlotStatus(1L, false);
    }

    // Service Exception Handling Tests

    @Test
    void processMessage_shouldContinueWhenServiceThrowsException() {
        // Arrange: Mock service to throw exception for invalid slot
        when(parkingService.updateSlotStatus(999L, true))
                .thenThrow(new RuntimeException("Slot not Found"));
        String payload = "{\"spot\":999,\"status\":true}";

        // Act: Process message that will cause service exception
        mqttMessageListener.processMessage("parking/sensor/999", payload);

        // Assert: Verify exception doesn't crash the listener
        verify(parkingService, times(1)).updateSlotStatus(999L, true);
    }

    // Topic Pattern Tests

    @Test
    void processMessage_shouldAcceptDifferentTopicPatterns() {
        // Arrange: Create payload for various topic patterns
        String payload = "{\"spot\":1,\"status\":true}";

        // Act: Process messages from different topic structures
        mqttMessageListener.processMessage("parking/sensor/1", payload);
        mqttMessageListener.processMessage("parking/cps2/slot1", payload);
        mqttMessageListener.processMessage("parking/icm/sensor-01", payload);

        // Assert: Verify all topic patterns are processed correctly
        verify(parkingService, times(3)).updateSlotStatus(1L, true);
    }
}
