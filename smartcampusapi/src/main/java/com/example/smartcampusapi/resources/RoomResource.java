package com.example.smartcampusapi.resources;

import com.example.smartcampusapi.exception.RoomNotEmptyException;
import com.example.smartcampusapi.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

@Path("/rooms")
public class RoomResource {

    public static Map<String, Room> rooms = new HashMap<>();

    // 🔹 GET ALL ROOMS
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Room> getAllRooms() {
        return rooms.values();
    }

    // 🔹 GET ROOM BY ID
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Room getRoom(@PathParam("roomId") String roomId) {

        Room room = rooms.get(roomId);

        if (room == null) {
            throw new NotFoundException("Room not found");
        }

        return room;
    }

    // 🔹 CREATE ROOM
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {

        if (room.getName() == null || room.getName().isEmpty()) {
            throw new BadRequestException("Room name is required");
        }

        if (room.getCapacity() <= 0) {
            throw new BadRequestException("Capacity must be greater than 0");
        }

        String id = UUID.randomUUID().toString();
        room.setId(id);

        rooms.put(id, room);

        return Response.status(Response.Status.CREATED)
                .entity(room)
                .build();
    }

    // 🔹 DELETE ROOM
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {

        Room room = rooms.get(roomId);

        if (room == null) {
            throw new NotFoundException("Room not found");
        }

        // ❗ BUSINESS RULE
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room cannot be deleted because it has sensors");
        }

        rooms.remove(roomId);

        return Response.noContent().build();
    }
}