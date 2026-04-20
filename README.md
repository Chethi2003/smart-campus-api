# Smart Campus API

## Overview

The Smart Campus API is a RESTful web service developed using **JAX-RS**. It manages campus resources such as rooms, sensors, and sensor readings.

The API supports:

* Room management
* Sensor registration and linking
* Sensor reading history tracking
* Filtering and search
* Structured error handling
* Request/response logging

The system uses **in-memory data storage** with thread-safe collections to simulate backend operations.

---

## Setup & Run Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/smart-campus-api.git
cd smart-campus-api
```

### 2. Build the Project

Make sure Maven is installed, then run:

```bash
mvn clean install
```

### 3. Deploy to Server

* Open the project in **NetBeans / IntelliJ**
* Deploy to **Apache Tomcat (or TomEE)**
* Ensure the application is running at:

```
http://localhost:8080/SmartCampusApi/api/v1
```

---

## Base URL

```
http://localhost:8080/SmartCampusApi/api/v1
```

---

## API Endpoints

### Discovery

```
GET /api/v1
```

### Rooms

```
GET    /api/v1/rooms
POST   /api/v1/rooms
GET    /api/v1/rooms/{roomId}
DELETE /api/v1/rooms/{roomId}
```

### Sensors

```
GET    /api/v1/sensors
GET    /api/v1/sensors?type=CO2
POST   /api/v1/sensors
GET    /api/v1/sensors/{id}
```

### Sensor Readings

```
GET  /api/v1/sensors/{sensorId}/readings
POST /api/v1/sensors/{sensorId}/readings
```

---

## Sample CURL Commands

### 1. Create a Room

```bash
curl -X POST http://localhost:8080/SmartCampusApi/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{
  "id": "LIB-301",
  "name": "Library Study Room",
  "capacity": 50
}'
```

---

### 2. Get All Rooms

```bash
curl http://localhost:8080/SmartCampusApi/api/v1/rooms
```

---

### 3. Create a Sensor

```bash
curl -X POST http://localhost:8080/SmartCampusApi/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{
  "id": "TEMP-001",
  "type": "Temperature",
  "status": "ACTIVE",
  "roomId": "LIB-301"
}'
```

---

### 4. Filter Sensors by Type

```bash
curl "http://localhost:8080/SmartCampusApi/api/v1/sensors?type=Temperature"
```

---

### 5. Add Sensor Reading

```bash
curl -X POST http://localhost:8080/SmartCampusApi/api/v1/sensors/TEMP-001/readings \
-H "Content-Type: application/json" \
-d '{
  "value": 25.5
}'
```

---

### 6. Get Sensor Readings

```bash
curl http://localhost:8080/SmartCampusApi/api/v1/sensors/TEMP-001/readings
```

---

## Error Handling

The API uses custom exception mappers to return structured JSON errors.

Example:

```json
{
  "errorMessage": "Room does not exist",
  "errorCode": 422,
  "documentation": "Linked resource not found"
}
```

---

## Logging

A custom logging filter logs:

* Incoming HTTP method + URL
* Outgoing response status

Example:

```
Incoming Request: POST /api/v1/sensors
Response Status: 201
```

---

## Design Notes

* Uses **Sub-resource locator pattern** for nested endpoints
* Supports **query parameter filtering**
* Maintains **historical sensor readings**
* Uses **thread-safe collections** for concurrency safety

---

## Author

* Name: Chethina Kovida Fernando
* UOW ID: w2119865
* IIT ID: 20240331
* Module: Client-Server Architecture

---

# Q&A 

### ”Smart Campus” Sensor & Room Management API

## Part 1: Service Architecture & Setup

### 1. Project & Application Configuration - JAX-RS Resource Lifecycle
In JAX-RS resources classes follow a per request lifecycle by default which means a new instance of the resource class is created for each incoming HTTP request.So the resource classes are not treated as singletons unless explicitly configured otherwise. 

Each request operates on its own instance which avoids shared state at the object level and improves thread safety for resource methods. In Smart Campus application data is stored in a shared static in-memory data structures;(eg: Map<String, Room> , Map<String, Sensor>). Even though each request has its own resource instance these maps are shared across all requests. 

This allows multiple requests to access and modify the same data at the same time. Because of that there is a risk of race conditions or inconsistent data, if two requests try to update the same entry simultaneously. 

So the design requires careful handling of shared datashared data. In a real world system, this can be managed using thread safe collections like ConcurrentHashMap or proper synchronization techniques to ensure that concurrent updates do not cause data loss or corruption.

### 2. The ”Discovery” Endpoint - HATEOAS 
Hypermedia, often referred to as HATEOAS, is seen as a key feature of RESTful APIs since it gives the ability for the API to direct the client on how to perform actions using the links contained in the response. In other words, rather than having the client knowing where each resource endpoint URL is hardcoded, the API sends the links.

The Discovery endpoint (/api/v1) in this project is an example of how this can be done through the JSON response, which contains links to primary resources like rooms and sensors.

This method is advantageous for client programmers since they will no longer have to depend solely on the static form of documentation to be able to know how to utilize the API. They will simply be able to make use of the provided links from the responses. This will make the process easier and minimize any errors if there are any changes in the end points in the future.

---

## Part 2: Room Management

### 1. Room Resource Implementation - Returning IDs vs Full Objects
Sending only room IDs will decrease the number of bytes sent back in response, but more requests will be needed on the client side to fetch full data about the rooms.

Sending the full objects will require more bandwidth but will simplify interactions between client and server as everything is fetched in one go.

In this example, I use the second approach since it simplifies interactions with clients.

### 2. Room Deletion & Safety Logic - DELETE Idempotency
Yes. The DELETE operation in this implementation is idempotent which means that making the same DELETE request multiple times results in the same final state of the system.

When a DELETE request is sent for a room that exists but has no sensors, then the room will be removed. Even though the same DELETE request is sent again, the room no longer exists. So the system responds with a NotFoundException (404).

Although the response changes after the first request, the overall system state remains unchanged. So the operation is still considered idempotent.

---

## Part 3: Sensor Management

### 1. Sensor Resource & Integrity - @Consumes and Media Type Mismatch
The @Consumes(MediaType.APPLICATION_JSON) notation implies that only those requests having data as JSON content may be consumed by the POST method. A request cannot be mapped if data is passed in any other media type like text/plain or application/xml.

Here, a response containing HTTP 415 – “Unsupported Media Type” will be sent back by JAX-RS without even executing the code of the method because there does not exist any message body reader capable enough to read the incoming request.

This guarantees that the data received by the API is of the correct type only.

### 2. Filtered Retrieval & Search - QueryParam vs PathParam
Using @QueryParam (/api/v1/sensors?type=CO2) enables us to have an option of having filters that are optional and more flexible. This means that clients can include several filters or even leave them out entirely, in case they require all sensors.

In the second example above, where we use a path variable (/api/v1/sensors?type=CO2), the inclusion of the filter makes the API path less flexible.

The query parameter technique is often seen as more preferable when filtering, since it was intended to search and refine sets, makes clean URLs, and enables you to apply different filters without having to change your endpoint at all.

---

## Part 4: Sensor Readings

### 1) Deep Nesting with Sub - Sub Resource Locator Benefits
With regard to the Sub Resource Locator design pattern any request to access nested resource like (/sensors/{id}/reading) can be assigned to another class known as SensorReadingResource rather than implementing it all within one controller.

In other words such design ensures that responsibilities are assigned effectively since each class does what it was meant to do. For instance the responsibility of SensorResource will be managing sensor while SensorReadingResource will focus on managing readings.

It means that the application remains easy to understand and extend. On the contrary a situation where everything will be done in a single controller would result in highly complicated and unmanageable system.

---

## Part 5:  Advanced Error Handling, Exception Mapping & Logging

### 2) Dependency Validation (422 Unprocessable Entity) - HTTP 422 and 404
A 422 status code is more appropriate here because the client's request is valid as far as its structure is concerned; Anyway there is an issue of invalid data within the request.

Usually 404 is applied to cases where the resource itself does not exist which means that a 404 should be applied to the non-existent endpoint rather than invalid data provided to the server.

As such a 422 is more relevant here because the request is correct in its structure while the data within the request is logically erroneous.

### 4) The Global Safety Net (500) -  Hiding Stack Traces
It is dangerous to display stack traces from Java internally in API endpoints due to the exposure of information on how the program is designed.

A stack trace may show class names, packages, file locations, and method calls that make it easier for an attacker to figure out the architecture of the program. It may even disclose frameworks used, libraries included, or servers running the application like Jersey or Tomcat.

Moreover, a stack trace may provide information about the application's business logic and database connections that an attacker may take advantage of.

This is why APIs should not provide stack traces to clients but rather generic error messages such as those provided in this project via the global exception mapper.

### 5)  API Request & Response Logging Filters - Logging Filter
Because it allows logging to be contained in one location and does not require calling the Logger.info() method in every resource method.

This gives a cleaner design and easier maintenance because all logging modifications will only need to be made in one class and not in various locations in the whole project. This also guarantees that all requests and responses will have their logs recorded without any missing endpoints.

However doing manual logging for every request and response in every resource method results in repetitive code and is more prone to errors.
