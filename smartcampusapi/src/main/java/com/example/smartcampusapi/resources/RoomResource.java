package com.example.smartcampusapi.resources;

import com.example.smartcampusapi.exception.RoomNotEmptyException;
import com.example.smartcampusapi.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Path("/rooms")
public class RoomResource {

    public static Map<String, Room> rooms = new ConcurrentHashMap<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Room> getAllRooms() {
        return rooms.values();
    }

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

   @POST
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response createRoom(Room room) {

    if (room.getId() == null || room.getId().isEmpty()) {
        throw new BadRequestException("Room ID is required");
    }

    if (rooms.containsKey(room.getId())) {
        throw new BadRequestException("Room ID already exists");
    }

    if (room.getName() == null || room.getName().isEmpty()) {
        throw new BadRequestException("Room name is required");
    }

    if (room.getCapacity() <= 0) {
        throw new BadRequestException("Capacity must be greater than 0");
    }

    rooms.put(room.getId(), room);

    return Response.status(Response.Status.CREATED)
            .entity(room)
            .build();
}

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {

        Room room = rooms.get(roomId);

        if (room == null) {
            throw new NotFoundException("Room not found");
        }

        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room cannot be deleted because it has sensors");
        }

        rooms.remove(roomId);

        return Response.noContent().build();
    }
}