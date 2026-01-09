package com.smartpark.parking_backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ParkingLotTest {

    private ParkingLot parkingLot;

    @BeforeEach
    void setUp() {
        parkingLot = new ParkingLot();
    }

    @Test
    void setName_shouldSetParkingLotName() {
        // Arrange & Act: Set parking lot name
        parkingLot.setName("CPS2 Smart Garage");

        // Assert: Verify name is set correctly
        assertThat(parkingLot.getName()).isEqualTo("CPS2 Smart Garage");
    }

    @Test
    void setAddress_shouldSetParkingLotAddress() {
        // Arrange & Act: Set parking lot address
        parkingLot.setAddress("University Campus");

        // Assert: Verify address is set correctly
        assertThat(parkingLot.getAddress()).isEqualTo("University Campus");
    }

    @Test
    void setLatitude_shouldSetGPSLatitudeCoordinate() {
        // Arrange & Act: Set latitude coordinate
        parkingLot.setLatitude(45.450708);

        // Assert: Verify latitude is stored correctly
        assertThat(parkingLot.getLatitude()).isEqualTo(45.450708);
    }

    @Test
    void setLongitude_shouldSetGPSLongitudeCoordinate() {
        // Arrange & Act: Set longitude coordinate
        parkingLot.setLongitude(4.387879);

        // Assert: Verify longitude is stored correctly
        assertThat(parkingLot.getLongitude()).isEqualTo(4.387879);
    }

    @Test
    void setLatitude_shouldHandleNegativeCoordinates() {
        // Arrange & Act: Set negative latitude (Southern hemisphere)
        parkingLot.setLatitude(-33.8688);

        // Assert: Verify negative coordinates are supported
        assertThat(parkingLot.getLatitude()).isEqualTo(-33.8688);
    }

    @Test
    void setLongitude_shouldHandleNegativeCoordinates() {
        // Arrange & Act: Set negative longitude (Western hemisphere)
        parkingLot.setLongitude(-118.2445);

        // Assert: Verify negative longitude values work correctly
        assertThat(parkingLot.getLongitude()).isEqualTo(-118.2445);
    }

    @Test
    void setParkingSpots_shouldAssignSlotsToLot() {
        // Arrange: Create parking slots
        ParkingSlot slot1 = new ParkingSlot();
        slot1.setSlotNumber(1);
        ParkingSlot slot2 = new ParkingSlot();
        slot2.setSlotNumber(2);
        List<ParkingSlot> slots = Arrays.asList(slot1, slot2);

        // Act: Assign slots to parking lot
        parkingLot.setParkingSpots(slots);

        // Assert: Verify slots are assigned and accessible
        assertThat(parkingLot.getParkingSlots()).hasSize(2);
        assertThat(parkingLot.getParkingSlots()).contains(slot1, slot2);
    }

    @Test
    void setParkingSpots_shouldHandleEmptySlotList() {
        // Arrange: Create empty slot list
        List<ParkingSlot> emptySlots = new ArrayList<>();

        // Act: Set empty slot list
        parkingLot.setParkingSpots(emptySlots);

        // Assert: Verify lot can have zero slots
        assertThat(parkingLot.getParkingSlots()).isEmpty();
    }

    @Test
    void getId_shouldReturnNullForNewEntity() {
        // Act: Get ID from newly created entity
        Long id = parkingLot.getId();

        // Assert: Verify ID is null before persistence
        assertThat(id).isNull();
    }

    @Test
    void constructor_shouldCreateEmptyParkingLot() {
        // Act: Create new parking lot using default constructor
        ParkingLot newLot = new ParkingLot();

        // Assert: Verify new instance has null fields
        assertThat(newLot.getName()).isNull();
        assertThat(newLot.getAddress()).isNull();
        assertThat(newLot.getLatitude()).isNull();
        assertThat(newLot.getLongitude()).isNull();
    }

    @Test
    void parkingLot_shouldSupportFullDataSetForAndroidApp() {
        // Arrange & Act: Set all fields required for Android app
        parkingLot.setName("CPS2 Smart Garage");
        parkingLot.setAddress("University Campus");
        parkingLot.setLatitude(45.450708);
        parkingLot.setLongitude(4.387879);

        ParkingSlot slot1 = new ParkingSlot();
        slot1.setSlotNumber(1);
        slot1.setOccupied(false);
        parkingLot.setParkingSpots(Arrays.asList(slot1));

        // Assert: Verify all data is available for map display
        assertThat(parkingLot.getName()).isNotNull();
        assertThat(parkingLot.getAddress()).isNotNull();
        assertThat(parkingLot.getLatitude()).isNotNull();
        assertThat(parkingLot.getLongitude()).isNotNull();
        assertThat(parkingLot.getParkingSlots()).isNotEmpty();
    }

    @Test
    void parkingLot_shouldSupportMultipleSlotsForAvailabilityCalculation() {
        // Arrange: Create lot with multiple slots with different statuses
        ParkingSlot freeSlot = new ParkingSlot();
        freeSlot.setOccupied(false);
        ParkingSlot occupiedSlot = new ParkingSlot();
        occupiedSlot.setOccupied(true);

        // Act: Assign multiple slots
        parkingLot.setParkingSpots(Arrays.asList(freeSlot, occupiedSlot));

        // Assert: Verify lot contains mixed availability slots
        assertThat(parkingLot.getParkingSlots()).hasSize(2);
        long freeCount = parkingLot.getParkingSlots().stream()
                .filter(slot -> !slot.getOccupied()).count();
        assertThat(freeCount).isEqualTo(1);
    }

    @Test
    void parkingLot_shouldAllowUpdatingCoordinates() {
        // Arrange: Set initial coordinates
        parkingLot.setLatitude(45.450708);
        parkingLot.setLongitude(4.387879);

        // Act: Update coordinates to new location
        parkingLot.setLatitude(45.427725);
        parkingLot.setLongitude(4.403586);

        // Assert: Verify coordinates can be changed
        assertThat(parkingLot.getLatitude()).isEqualTo(45.427725);
        assertThat(parkingLot.getLongitude()).isEqualTo(4.403586);
    }
}
