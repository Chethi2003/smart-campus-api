package com.example.smartcampusapi.resources;

import com.example.smartcampusapi.exception.SensorUnavailableException;
import com.example.smartcampusapi.model.Sensor;
import com.example.smartcampusapi.model.SensorReading;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

public class SensorReadingResource {

    private static Map<String, List<SensorReading>> readingsMap = new HashMap<>();

    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getReadings() {

        Sensor sensor = SensorResource.sensors.get(sensorId);

        if (sensor == null) {
            throw new NotFoundException("Sensor not found");
        }

        return readingsMap.getOrDefault(sensorId, new ArrayList<>());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {

        Sensor sensor = SensorResource.sensors.get(sensorId);

        if (sensor == null) {
            throw new NotFoundException("Sensor not found");
        }

        if (reading.getValue() <= 0) {
            throw new BadRequestException("Reading value must be > 0");
        }
        
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
    throw new SensorUnavailableException("Sensor is under maintenance");
}

        reading.setId(UUID.randomUUID().toString());
        reading.setTimestamp(System.currentTimeMillis());

        readingsMap
                .computeIfAbsent(sensorId, k -> new ArrayList<>())
                .add(reading);

        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED)
                .entity(reading)
                .build();
    }
}