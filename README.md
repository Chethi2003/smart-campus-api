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

### 2.1 Returning IDs vs Full Objects
(answer)

### 2.2 DELETE Idempotency
(answer)

---

## Part 3: Sensor Management

### 3.1 @Consumes and Media Type Mismatch
(answer)

### 3.2 QueryParam vs PathParam
(answer)

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
