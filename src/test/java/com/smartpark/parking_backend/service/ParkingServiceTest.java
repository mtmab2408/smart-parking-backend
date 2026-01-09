package com.smartpark.parking_backend.service;

import com.smartpark.parking_backend.model.ParkingLot;
import com.smartpark.parking_backend.model.ParkingSlot;
import com.smartpark.parking_backend.repository.ParkingLotRepository;
import com.smartpark.parking_backend.repository.ParkingSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock
    private ParkingSlotRepository parkingSlotRepository;

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @InjectMocks
    private ParkingService parkingService;

    private ParkingLot testLot;
    private ParkingSlot testSlot;

    @BeforeEach
    void setUp() {
        testLot = new ParkingLot();
        testLot.setName("Test Garage");
        testLot.setAddress("Test Address");
        testLot.setLatitude(45.450708);
        testLot.setLongitude(4.387879);

        testSlot = new ParkingSlot();
        testSlot.setSlotNumber(1);
        testSlot.setOccupied(false);
        testSlot.setSensorId("sensor-test-01");
        testSlot.setParkingLot(testLot);
    }

    // Lot Management Tests

    @Test
    void getAllLots_shouldReturnAllParkingLots() {
        // Arrange: Mock repository returns two parking lots
        ParkingLot lot1 = new ParkingLot();
        lot1.setName("Lot 1");
        ParkingLot lot2 = new ParkingLot();
        lot2.setName("Lot 2");
        when(parkingLotRepository.findAll()).thenReturn(Arrays.asList(lot1, lot2));

        // Act: Call service method
        List<ParkingLot> result = parkingService.getAllLots();

        // Assert: Verify correct number of lots returned
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(lot1, lot2);
        verify(parkingLotRepository, times(1)).findAll();
    }

    @Test
    void getAllLots_shouldReturnEmptyListWhenNoLotsExist() {
        // Arrange: Mock repository returns empty list
        when(parkingLotRepository.findAll()).thenReturn(Arrays.asList());

        // Act: Call service method
        List<ParkingLot> result = parkingService.getAllLots();

        // Assert: Verify empty list is returned
        assertThat(result).isEmpty();
    }

    @Test
    void createParkingLot_shouldSaveAndReturnNewParkingLot() {
        // Arrange: Mock repository save operation
        when(parkingLotRepository.save(testLot)).thenReturn(testLot);

        // Act: Create new parking lot
        ParkingLot result = parkingService.createParkingLot(testLot);

        // Assert: Verify lot is saved and returned
        assertThat(result).isEqualTo(testLot);
        assertThat(result.getName()).isEqualTo("Test Garage");
        verify(parkingLotRepository, times(1)).save(testLot);
    }

    @Test
    void createParkingLot_shouldSaveParkingLotWithCoordinates() {
        // Arrange: Create lot with GPS coordinates
        when(parkingLotRepository.save(testLot)).thenReturn(testLot);

        // Act: Save parking lot
        ParkingLot result = parkingService.createParkingLot(testLot);

        // Assert: Verify coordinates are saved correctly
        assertThat(result.getLatitude()).isEqualTo(45.450708);
        assertThat(result.getLongitude()).isEqualTo(4.387879);
    }

    @Test
    void deleteParkingLot_shouldCallRepositoryDeleteById() {
        // Arrange: Setup lot ID to delete
        Long lotId = 1L;
        doNothing().when(parkingLotRepository).deleteById(lotId);

        // Act: Delete parking lot
        parkingService.deleteParkingLot(lotId);

        // Assert: Verify delete was called once
        verify(parkingLotRepository, times(1)).deleteById(lotId);
    }

    // Slot Management Tests

    @Test
    void getAllSlots_shouldReturnAllParkingSlots() {
        // Arrange: Mock repository returns multiple slots
        ParkingSlot slot1 = new ParkingSlot();
        slot1.setSlotNumber(1);
        ParkingSlot slot2 = new ParkingSlot();
        slot2.setSlotNumber(2);
        when(parkingSlotRepository.findAll()).thenReturn(Arrays.asList(slot1, slot2));

        // Act: Get all slots
        List<ParkingSlot> result = parkingService.getAllSlots();

        // Assert: Verify both slots are returned
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSlotNumber()).isEqualTo(1);
        assertThat(result.get(1).getSlotNumber()).isEqualTo(2);
    }

    @Test
    void addSlotToLot_shouldAssignSlotToLotAndSave() {
        // Arrange: Mock lot exists in repository
        Long lotId = 1L;
        when(parkingLotRepository.findById(lotId)).thenReturn(Optional.of(testLot));
        when(parkingSlotRepository.save(testSlot)).thenReturn(testSlot);

        // Act: Add slot to parking lot
        ParkingSlot result = parkingService.addSlotToLot(lotId, testSlot);

        // Assert: Verify slot is linked to lot and saved
        assertThat(result.getParkingLot()).isEqualTo(testLot);
        verify(parkingLotRepository, times(1)).findById(lotId);
        verify(parkingSlotRepository, times(1)).save(testSlot);
    }

    @Test
    void addSlotToLot_shouldThrowExceptionWhenLotNotFound() {
        // Arrange: Mock lot does not exist
        Long nonExistentLotId = 999L;
        when(parkingLotRepository.findById(nonExistentLotId)).thenReturn(Optional.empty());

        // Act & Assert: Verify exception is thrown with correct message
        assertThatThrownBy(() -> parkingService.addSlotToLot(nonExistentLotId, testSlot))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Lot not found");
    }

    @Test
    void deleteSlot_shouldCallRepositoryDeleteById() {
        // Arrange: Setup slot ID to delete
        Long slotId = 1L;
        doNothing().when(parkingSlotRepository).deleteById(slotId);

        // Act: Delete parking slot
        parkingService.deleteSlot(slotId);

        // Assert: Verify delete was called once
        verify(parkingSlotRepository, times(1)).deleteById(slotId);
    }

    @Test
    void updateSlotDetails_shouldUpdateSlotNumberAndSensorId() {
        // Arrange: Create existing and updated slot data
        ParkingSlot existingSlot = new ParkingSlot();
        existingSlot.setSlotNumber(1);
        existingSlot.setSensorId("old-sensor");

        ParkingSlot newDetails = new ParkingSlot();
        newDetails.setSlotNumber(10);
        newDetails.setSensorId("new-sensor");

        when(parkingSlotRepository.findById(1L)).thenReturn(Optional.of(existingSlot));
        when(parkingSlotRepository.save(any(ParkingSlot.class))).thenReturn(existingSlot);

        // Act: Update slot details
        ParkingSlot result = parkingService.updateSLotDetails(1L, newDetails);

        // Assert: Verify slot number and sensor ID are updated
        assertThat(result.getSlotNumber()).isEqualTo(10);
        assertThat(result.getSensorId()).isEqualTo("new-sensor");
        verify(parkingSlotRepository, times(1)).save(existingSlot);
    }

    @Test
    void updateSlotDetails_shouldThrowExceptionWhenSlotNotFound() {
        // Arrange: Mock slot does not exist
        when(parkingSlotRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert: Verify exception is thrown when updating non-existent slot
        assertThatThrownBy(() -> parkingService.updateSLotDetails(999L, testSlot))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Slot not found");
    }

    @Test
    void getSlotById_shouldReturnSlotWhenExists() {
        // Arrange: Mock repository returns slot
        when(parkingSlotRepository.findById(1L)).thenReturn(Optional.of(testSlot));

        // Act: Get slot by ID
        Optional<ParkingSlot> result = parkingService.getSlotById(1L);

        // Assert: Verify slot is returned and contains correct data
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testSlot);
        assertThat(result.get().getSlotNumber()).isEqualTo(1);
    }

    @Test
    void getSlotById_shouldReturnEmptyWhenSlotNotFound() {
        // Arrange: Mock repository returns empty
        when(parkingSlotRepository.findById(999L)).thenReturn(Optional.empty());

        // Act: Try to get non-existent slot
        Optional<ParkingSlot> result = parkingService.getSlotById(999L);

        // Assert: Verify empty optional is returned
        assertThat(result).isEmpty();
    }

    @Test
    void updateSlotStatus_shouldMarkSlotAsOccupied() {
        // Arrange: Create free slot and mock repository
        testSlot.setOccupied(false);
        when(parkingSlotRepository.findById(1L)).thenReturn(Optional.of(testSlot));
        when(parkingSlotRepository.save(testSlot)).thenReturn(testSlot);

        // Act: Update slot status to occupied
        ParkingSlot result = parkingService.updateSlotStatus(1L, true);

        // Assert: Verify slot is now marked as occupied
        assertThat(result.getOccupied()).isTrue();
        verify(parkingSlotRepository, times(1)).save(testSlot);
    }

    @Test
    void updateSlotStatus_shouldMarkSlotAsFree() {
        // Arrange: Create occupied slot and mock repository
        testSlot.setOccupied(true);
        when(parkingSlotRepository.findById(1L)).thenReturn(Optional.of(testSlot));
        when(parkingSlotRepository.save(testSlot)).thenReturn(testSlot);

        // Act: Update slot status to free
        ParkingSlot result = parkingService.updateSlotStatus(1L, false);

        // Assert: Verify slot is now marked as free
        assertThat(result.getOccupied()).isFalse();
        verify(parkingSlotRepository, times(1)).save(testSlot);
    }

    @Test
    void updateSlotStatus_shouldThrowExceptionWhenSlotNotFound() {
        // Arrange: Mock slot does not exist
        when(parkingSlotRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert: Verify exception is thrown when updating status of non-existent slot
        assertThatThrownBy(() -> parkingService.updateSlotStatus(999L, true))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Slot not Found");
    }

    @Test
    void updateSlotStatus_shouldHandleMultipleStatusChanges() {
        // Arrange: Create slot and mock repository
        when(parkingSlotRepository.findById(1L)).thenReturn(Optional.of(testSlot));
        when(parkingSlotRepository.save(testSlot)).thenReturn(testSlot);

        // Act: Perform multiple status changes
        parkingService.updateSlotStatus(1L, true);
        parkingService.updateSlotStatus(1L, false);
        parkingService.updateSlotStatus(1L, true);

        // Assert: Verify save was called three times for status changes
        verify(parkingSlotRepository, times(3)).save(testSlot);
    }
}
