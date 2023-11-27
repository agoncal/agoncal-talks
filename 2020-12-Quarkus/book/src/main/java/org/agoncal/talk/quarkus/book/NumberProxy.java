package org.agoncal.talk.quarkus.book;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.logging.Logger;

import java.util.Random;

@RegisterRestClient
@Path("/api/numbers")
public interface NumberProxy {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    String generateISBN();
}
