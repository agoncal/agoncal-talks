package org.expenses.web.rest;

import org.expenses.web.model.Conference;
import org.expenses.web.service.ConferenceService;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

@Path("/conferences")
public class ConferenceEndpoint {

    @Inject
    private ConferenceService service;

    @POST
    @Consumes("application/json")
    public Response create(Conference entity) {
        entity = service.persist(entity);
        return Response.created(
                UriBuilder.fromResource(ConferenceEndpoint.class)
                        .path(String.valueOf(entity.getId())).build()).build();
    }

    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteById(@PathParam("id") Long id) {
        Conference entity = service.findById(id);
        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        service.remove(entity);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces("application/json")
    public Response findById(@PathParam("id") Long id) {
        Conference entity;
        try {
            entity = service.findById(id);
        } catch (NoResultException nre) {
            entity = null;
        }
        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(entity).build();
    }

    @GET
    @Produces("application/json")
    public List<Conference> listAll() {
        final List<Conference> results = service.listAll();
        return results;
    }
}
