package com.example.smartcampusapi.resources;

import com.example.smartcampusapi.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

@Path("/rooms")
public class RoomResource {

    public static Map<String, Room> rooms = new HashMap<>();

    // GET ALL
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Room> getAllRooms() {
        return rooms.values();
    }

    // GET BY ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Room getRoom(@PathParam("id") String id) {
        Room room = rooms.get(id);

        if (room == null) {
            throw new NotFoundException("Room not found");
        }

        return room;
    }

    // CREATE
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {

        if (room.getName() == null || room.getName().isEmpty()) {
            throw new BadRequestException("Room name required");
        }

        if (room.getCapacity() <= 0) {
            throw new BadRequestException("Capacity must be > 0");
        }

        String id = UUID.randomUUID().toString();
        room.setId(id);

        rooms.put(id, room);

        return Response.status(Response.Status.CREATED)
                .entity(room)
                .build();
    }

    // DELETE
    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") String id) {

        Room room = rooms.get(id);

        if (room == null) {
            throw new NotFoundException("Room not found");
        }

        if (!room.getSensorIds().isEmpty()) {
            throw new ForbiddenException("Room has sensors, cannot delete");
        }

        rooms.remove(id);

        return Response.noContent().build();
    }
}