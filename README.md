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

### 4.1 Sub-Resource Locator Benefits
(answer)

---

## Part 5: Exception Handling & Observability

### 5.1 HTTP 422 vs 404
(answer)

### 5.2 Security: Hiding Stack Traces
(answer)

### 5.3 Logging Filter
(answer)
