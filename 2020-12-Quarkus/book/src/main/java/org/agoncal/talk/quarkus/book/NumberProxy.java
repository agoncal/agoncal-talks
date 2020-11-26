package org.agoncal.talk.quarkus.book;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RegisterRestClient
@Path("/api/numbers")
public interface NumberProxy {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    String generateISBN();
}
