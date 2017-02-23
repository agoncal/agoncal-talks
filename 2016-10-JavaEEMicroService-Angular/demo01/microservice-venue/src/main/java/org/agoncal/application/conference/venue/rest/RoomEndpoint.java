package org.agoncal.application.conference.venue.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.agoncal.application.conference.venue.domain.Room;
import org.agoncal.application.conference.venue.repository.RoomRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

/**
 * @author Antonio Goncalves
 *         http://www.antoniogoncalves.org
 *         --
 */
@Path("/rooms")
@Api(description = "Rooms REST Endpoint", tags = {"Venue"})
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomEndpoint {

    // ======================================
    // =          Injection Points          =
    // ======================================

    @Inject
    private RoomRepository roomRepository;

    @Context
    private UriInfo uriInfo;


    // ======================================
    // =          Business methods          =
    // ======================================

    @POST
    @ApiOperation(value = "Adds a new room to the venue")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid input")}
    )
    public Response add(@NotNull Room room) {
        Room created = roomRepository.create(room);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(created.getId()).build()).build();
    }

    @GET
    @Path("/{id}")
    @ApiOperation(value = "Finds a room by ID", response = Room.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 404, message = "Room not found")}
    )
    public Response retrieve(@PathParam("id") String id) {

        Room room = roomRepository.findById(id);

        return Response.ok(room).build();
    }

    @GET
    @ApiOperation(value = "Finds all the rooms", response = Room.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Rooms not found")}
    )
    public Response allRooms() {
        List<Room> allRooms = roomRepository.findAllRooms();

        if (allRooms == null || allRooms.isEmpty())
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(buildEntity(allRooms)).build();
    }

    @DELETE
    @Path("/{id}")
    @ApiOperation(value = "Deletes a room")
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid room value")}
    )
    public Response remove(@PathParam("id") String id) {
        roomRepository.delete(id);
        return Response.noContent().build();
    }

    @PUT
    @ApiOperation(value = "Update an existing room")
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid values")}
    )
    public Response update(@NotNull Room room) {
        roomRepository.update(room);
        return Response.ok(room).build();
    }

    // ======================================
    // =           Private methods          =
    // ======================================

    private GenericEntity<List<Room>> buildEntity(final List<Room> roomList) {
        return new GenericEntity<List<Room>>(roomList) {
        };
    }
}
