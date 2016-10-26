package org.agoncal.application.conference.venue.rest;

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
    public Response add(@NotNull Room room) {
        Room created = roomRepository.create(room);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(created.getId()).build()).build();
    }

    @GET
    @Path("/{id}")
    public Response retrieve(@PathParam("id") String id, @Context Request request) {

        Room room = roomRepository.findById(id);

        return Response.ok(room).build();
    }

    @GET
    public Response allRooms() {
        List<Room> allRooms = roomRepository.findAllRooms();

        if (allRooms == null || allRooms.isEmpty())
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(buildEntity(allRooms)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response remove(@PathParam("id") String id) {
        roomRepository.delete(id);
        return Response.noContent().build();
    }

    @PUT
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
