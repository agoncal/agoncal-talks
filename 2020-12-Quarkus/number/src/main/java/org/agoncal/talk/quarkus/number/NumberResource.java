package org.agoncal.talk.quarkus.number;

import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Random;

@Path("/api/numbers")
public class NumberResource {

    @Inject
    Logger logger;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String generateISBN() {
        String number = "13-" + new Random().nextInt(100_000_000);
        logger.info("### ISBN " + number);
        return number;
    }
}
