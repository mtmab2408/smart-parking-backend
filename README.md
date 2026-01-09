# Smart Parking Backend

A Spring Boot REST API that manages real-time parking spot availability using IoT sensors and MQTT messaging. This system is designed to work with an Android application that displays live parking availability on Google Maps.

## What Does This Project Do?

This backend system:
- Receives real-time parking data from IoT sensors via MQTT
- Stores parking lot and parking slot information in a database
- Updates parking spot status (occupied/free) in real-time
- Provides REST API endpoints for Android app to fetch parking data
- Supports GPS coordinates for displaying parking locations on maps

**Example Flow:**
1. A car parks in a spot and the sensor detects it
2. Sensor sends MQTT message to the backend
3. Backend updates database and marks slot as "occupied"
4. Android app fetches data and shows "occupied" status on map

## Technology Stack

- Java 21
- Spring Boot 3.5.0
- H2 In-Memory Database
- HiveMQ Cloud MQTT Broker
- Gradle (Kotlin DSL)

## Prerequisites

Before you begin, make sure you have installed:
- Java 21 or higher
- Git

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/mtmab2408/smart-parking-backend.git
cd smart-parking-backend
```

### 2. Switch to Main Branch

```bash
git checkout main
```

### 3. Run the Application

On Mac/Linux:
```bash
./gradlew bootRun
```

On Windows:
```bash
gradlew.bat bootRun
```

The application will start on `http://localhost:8080`

### 4. Access H2 Database Console (Optional)

Once the application is running, you can view the database at:
```
http://localhost:8080/h2-console
```

Login credentials:
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## Running Tests

### Run All Tests

```bash
./gradlew test
```

### Run Tests and Build

```bash
./gradlew clean build
```

### View Test Report

After running tests, open the HTML report:
```bash
open build/reports/tests/test/index.html
```

Test Coverage:
- 90 tests total
- Unit tests for services
- Integration tests for REST API
- MQTT message processing tests
- Model entity tests

## API Endpoints

### Get All Parking Lots
```
GET http://localhost:8080/api/parking/lots
```
Returns all parking lots with their GPS coordinates and slots.

### Get All Parking Slots
```
GET http://localhost:8080/api/parking/slots
```
Returns all parking slots with their real-time occupancy status.

### Create New Parking Lot
```
POST http://localhost:8080/api/parking/lots
Content-Type: application/json

{
  "name": "Downtown Parking",
  "address": "123 Main St",
  "latitude": 45.450708,
  "longitude": 4.387879
}
```

### Add Slot to Parking Lot
```
POST http://localhost:8080/api/parking/lots/{lotId}/slots
Content-Type: application/json

{
  "slotNumber": 1,
  "sensorId": "sensor-01",
  "occupied": false
}
```

### Update Slot Status (Used by MQTT)
```
PUT http://localhost:8080/api/parking/slots/{slotId}/status?occupied=true
```

### Delete Parking Slot
```
DELETE http://localhost:8080/api/parking/slots/{slotId}
```

### Delete Parking Lot
```
DELETE http://localhost:8080/api/parking/lots/{lotId}
```

## MQTT Configuration

The application connects to HiveMQ Cloud MQTT broker. Configuration is in:
```
src/main/resources/application.properties
```

### MQTT Message Format

Sensors should send JSON messages to topic `parking/#`:
```json
{
  "spot": 1,
  "status": true
}
```

Fields:
- `spot`: Slot ID (matches database slot ID)
- `status`: `true` = occupied, `false` = free (also supports 1/0 or "true"/"false")

### Testing Without Real Sensors

The application includes a dummy MQTT publisher for testing. Enable it in `application.properties`:
```properties
mqtt.dummy.enabled=true
```

This will simulate sensor data every 5 seconds.

## Project Structure

```
src/main/java/com/smartpark/parking_backend/
├── config/           # Configuration files (DataLoader, MQTT)
├── controller/       # REST API endpoints
├── model/           # Database entities (ParkingLot, ParkingSlot)
├── mqtt/            # MQTT message listener
├── repository/      # Database repositories
└── service/         # Business logic

src/test/java/       # All test files (90 tests)
```

## Sample Data

The application seeds the database with sample data on startup:

Parking Lots:
1. CPS2 Smart Garage - University Campus (3 slots)
2. ICM Smart Garage - University Campus (1 slot)

Parking Slots:
- Slot 1: sensor-01 (CPS2)
- Slot 2: sensor-02 (ICM)
- Slot 3: No sensor (CPS2)

## Troubleshooting

### Port Already in Use
If port 8080 is already in use, change it in `application.properties`:
```properties
server.port=8081
```

### Build Fails
Try cleaning and rebuilding:
```bash
./gradlew clean build --refresh-dependencies
```

### Tests Fail
Make sure you have Java 21:
```bash
java -version
```

### MQTT Connection Issues
Check your internet connection. The application connects to HiveMQ Cloud which requires internet access.

## For Android App Integration

1. Start the backend application
2. The Android app should call:
   - `GET /api/parking/lots` - To get parking locations for map markers
   - `GET /api/parking/slots` - To get real-time occupancy status
3. Poll these endpoints every 10-30 seconds or implement WebSocket for push updates

## License

This project is part of a Masters thesis project.
