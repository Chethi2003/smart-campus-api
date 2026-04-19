package com.example.smartcampusapi.resources;

import com.example.smartcampusapi.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("/rooms")
public class RoomResource {

    private static Map<Integer, Room> rooms = new HashMap<>();
    private static int idCounter = 1;

    // GET ALL ROOMS
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Room> getAllRooms() {
        return rooms.values();
    }

    // GET ROOM BY ID
    @GET
@Path("/{id}")
@Produces(MediaType.APPLICATION_JSON)
public Room getRoom(@PathParam("id") int id) {

    Room room = rooms.get(id);

    if (room == null) {
        throw new NotFoundException("Room with ID " + id + " not found");
    }

    return room;
}

    // CREATE ROOM
    @POST
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Room createRoom(Room room) {

    if (room.getName() == null || room.getName().isEmpty()) {
        throw new BadRequestException("Room name is required");
    }

    if (room.getCapacity() <= 0) {
        throw new BadRequestException("Capacity must be greater than 0");
    }

    room.setId(idCounter++);
    rooms.put(room.getId(), room);

    return room;
}

    // DELETE ROOM
    @DELETE
@Path("/{id}")
public void deleteRoom(@PathParam("id") int id) {

    Room removed = rooms.remove(id);

    if (removed == null) {
        throw new NotFoundException("Room not found");
    }
}
}