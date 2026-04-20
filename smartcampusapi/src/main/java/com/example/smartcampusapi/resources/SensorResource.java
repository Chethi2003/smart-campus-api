package com.example.smartcampusapi.resources;

import com.example.smartcampusapi.exception.LinkedResourceNotFoundException;
import com.example.smartcampusapi.model.Sensor;
import com.example.smartcampusapi.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Path("/sensors")
public class SensorResource {

    public static Map<String, Sensor> sensors = new ConcurrentHashMap<>();

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

        Room room = RoomResource.rooms.get(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException("Room does not exist");
        }

        String id = UUID.randomUUID().toString();
        sensor.setId(id);

        sensor.setStatus("ACTIVE");
        sensor.setCurrentValue(0.0);

        sensors.put(id, sensor);

        room.getSensorIds().add(id);

        return Response.status(Response.Status.CREATED)
                .entity(sensor)
                .build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}