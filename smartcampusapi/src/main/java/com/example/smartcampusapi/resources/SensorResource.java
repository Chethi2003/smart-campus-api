package com.example.smartcampusapi.resources;

import com.example.smartcampusapi.model.Sensor;
import com.example.smartcampusapi.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

@Path("/sensors")
public class SensorResource {

    public static Map<String, Sensor> sensors = new HashMap<>();

    // GET ALL / FILTER
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Sensor> getSensors(@QueryParam("type") String type) {

        if (type == null) {
            return sensors.values();
        }

        List<Sensor> filtered = new ArrayList<>();
        for (Sensor s : sensors.values()) {
            if (s.getType().equalsIgnoreCase(type)) {
                filtered.add(s);
            }
        }

        return filtered;
    }

    // GET BY ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Sensor getSensor(@PathParam("id") String id) {

        Sensor sensor = sensors.get(id);

        if (sensor == null) {
            throw new NotFoundException("Sensor not found");
        }

        return sensor;
    }

    // CREATE SENSOR
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {

        if (sensor.getType() == null || sensor.getType().isEmpty()) {
            throw new BadRequestException("Sensor type is required");
        }

        if (sensor.getRoomId() == null || sensor.getRoomId().isEmpty()) {
            throw new BadRequestException("Room ID is required");
        }

        // CHECK ROOM EXISTS
        Room room = RoomResource.rooms.get(sensor.getRoomId());
        if (room == null) {
            throw new BadRequestException("Room does not exist");
        }

        // GENERATE STRING ID
        String id = UUID.randomUUID().toString();
        sensor.setId(id);

        // DEFAULT VALUES
        sensor.setStatus("ACTIVE");
        sensor.setCurrentValue(0.0);

        sensors.put(id, sensor);

        // LINK SENSOR TO ROOM
        room.getSensorIds().add(id);

        return Response.status(Response.Status.CREATED)
                .entity(sensor)
                .build();
    }
}