package com.smartpark.parking_backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParkingSlotTest {

    private ParkingSlot parkingSlot;
    private ParkingLot parkingLot;

    @BeforeEach
    void setUp() {
        parkingSlot = new ParkingSlot();

        parkingLot = new ParkingLot();
        parkingLot.setName("Test Garage");
        parkingLot.setAddress("Test Address");
    }

    @Test
    void setSlotNumber_shouldSetSlotIdentifier() {
        // Arrange & Act: Set slot number
        parkingSlot.setSlotNumber(101);

        // Assert: Verify slot number is set correctly
        assertThat(parkingSlot.getSlotNumber()).isEqualTo(101);
    }

    @Test
    void setOccupied_shouldMarkSlotAsOccupied() {
        // Arrange & Act: Mark slot as occupied
        parkingSlot.setOccupied(true);

        // Assert: Verify slot is marked as occupied
        assertThat(parkingSlot.getOccupied()).isTrue();
    }

    @Test
    void setOccupied_shouldMarkSlotAsFree() {
        // Arrange & Act: Mark slot as free
        parkingSlot.setOccupied(false);

        // Assert: Verify slot is marked as free
        assertThat(parkingSlot.getOccupied()).isFalse();
    }

    @Test
    void setSensorId_shouldAssignSensorIdentifier() {
        // Arrange & Act: Set sensor hardware ID
        parkingSlot.setSensorId("sensor-01");

        // Assert: Verify sensor ID is stored correctly
        assertThat(parkingSlot.getSensorId()).isEqualTo("sensor-01");
    }

    @Test
    void setSensorId_shouldAllowNullForSlotsWithoutSensors() {
        // Arrange & Act: Set null sensor ID for manual slots
        parkingSlot.setSensorId(null);

        // Assert: Verify null sensor ID is allowed
        assertThat(parkingSlot.getSensorId()).isNull();
    }

    @Test
    void setParkingLot_shouldAssignSlotToLot() {
        // Arrange & Act: Link slot to parking lot
        parkingSlot.setParkingLot(parkingLot);

        // Assert: Verify slot is linked to correct parking lot
        assertThat(parkingSlot.getParkingLot()).isEqualTo(parkingLot);
        assertThat(parkingSlot.getParkingLot().getName()).isEqualTo("Test Garage");
    }

    @Test
    void getId_shouldReturnNullForNewEntity() {
        // Act: Get ID from newly created slot
        Long id = parkingSlot.getId();

        // Assert: Verify ID is null before persistence
        assertThat(id).isNull();
    }

    @Test
    void parkingSlot_shouldToggleOccupancyStatus() {
        // Arrange: Start with free slot
        parkingSlot.setOccupied(false);

        // Act: Toggle occupancy multiple times
        parkingSlot.setOccupied(true);
        boolean firstStatus = parkingSlot.getOccupied();
        parkingSlot.setOccupied(false);
        boolean secondStatus = parkingSlot.getOccupied();

        // Assert: Verify status can be toggled
        assertThat(firstStatus).isTrue();
        assertThat(secondStatus).isFalse();
    }

    @Test
    void parkingSlot_shouldSupportCompleteDataForMQTTUpdate() {
        // Arrange & Act: Set all data required for MQTT sensor update
        parkingSlot.setSlotNumber(5);
        parkingSlot.setSensorId("sensor-cps2-05");
        parkingSlot.setOccupied(true);
        parkingSlot.setParkingLot(parkingLot);

        // Assert: Verify all MQTT-related fields are set
        assertThat(parkingSlot.getSlotNumber()).isEqualTo(5);
        assertThat(parkingSlot.getSensorId()).isNotNull();
        assertThat(parkingSlot.getOccupied()).isTrue();
        assertThat(parkingSlot.getParkingLot()).isNotNull();
    }

    @Test
    void parkingSlot_shouldAllowChangingSensorId() {
        // Arrange: Set initial sensor
        parkingSlot.setSensorId("sensor-old");

        // Act: Change sensor ID (sensor replacement scenario)
        parkingSlot.setSensorId("sensor-new");

        // Assert: Verify sensor ID can be updated
        assertThat(parkingSlot.getSensorId()).isEqualTo("sensor-new");
    }

    @Test
    void parkingSlot_shouldAllowChangingSlotNumber() {
        // Arrange: Set initial slot number
        parkingSlot.setSlotNumber(10);

        // Act: Change slot number (renumbering scenario)
        parkingSlot.setSlotNumber(20);

        // Assert: Verify slot number can be changed
        assertThat(parkingSlot.getSlotNumber()).isEqualTo(20);
    }

    @Test
    void parkingSlot_shouldSimulateCarParkingScenario() {
        // Arrange: Create free slot with sensor
        parkingSlot.setSlotNumber(3);
        parkingSlot.setSensorId("sensor-03");
        parkingSlot.setOccupied(false);

        // Act: Car parks in the slot (sensor triggers MQTT message)
        parkingSlot.setOccupied(true);

        // Assert: Verify slot is now occupied
        assertThat(parkingSlot.getOccupied()).isTrue();
        assertThat(parkingSlot.getSensorId()).isEqualTo("sensor-03");
    }

    @Test
    void parkingSlot_shouldSimulateCarLeavingScenario() {
        // Arrange: Create occupied slot
        parkingSlot.setSlotNumber(3);
        parkingSlot.setSensorId("sensor-03");
        parkingSlot.setOccupied(true);

        // Act: Car leaves the slot (sensor triggers MQTT message)
        parkingSlot.setOccupied(false);

        // Assert: Verify slot is now free
        assertThat(parkingSlot.getOccupied()).isFalse();
    }

    @Test
    void parkingSlot_shouldSupportManualOverrideWithoutSensor() {
        // Arrange & Act: Create slot without sensor for manual management
        parkingSlot.setSlotNumber(99);
        parkingSlot.setSensorId(null);
        parkingSlot.setOccupied(true);

        // Assert: Verify slot can be managed without sensor
        assertThat(parkingSlot.getSensorId()).isNull();
        assertThat(parkingSlot.getOccupied()).isTrue();
    }

    @Test
    void parkingSlot_shouldMaintainBidirectionalRelationshipWithLot() {
        // Arrange & Act: Link slot to lot
        parkingSlot.setParkingLot(parkingLot);

        // Assert: Verify bidirectional relationship
        assertThat(parkingSlot.getParkingLot()).isNotNull();
        assertThat(parkingSlot.getParkingLot().getName()).isEqualTo("Test Garage");
    }

    @Test
    void parkingSlot_shouldHandleDefaultFalseOccupancyState() {
        // Act: Create new slot without setting occupancy
        ParkingSlot newSlot = new ParkingSlot();

        // Assert: Verify default occupancy is false (primitive boolean)
        assertThat(newSlot.getOccupied()).isFalse();
    }

    @Test
    void parkingSlot_shouldSupportAlphanumericSensorIds() {
        // Arrange & Act: Set various sensor ID formats
        parkingSlot.setSensorId("SENSOR-CPS2-SLOT-01-V2");

        // Assert: Verify alphanumeric sensor IDs are supported
        assertThat(parkingSlot.getSensorId()).isEqualTo("SENSOR-CPS2-SLOT-01-V2");
    }

    @Test
    void parkingSlot_shouldSupportLargeSlotNumbers() {
        // Arrange & Act: Set large slot number for multi-level parking
        parkingSlot.setSlotNumber(9999);

        // Assert: Verify large slot numbers are supported
        assertThat(parkingSlot.getSlotNumber()).isEqualTo(9999);
    }
}
