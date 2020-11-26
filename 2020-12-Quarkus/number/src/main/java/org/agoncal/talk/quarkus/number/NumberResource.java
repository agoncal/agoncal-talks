package org.agoncal.talk.quarkus.number;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Random;

@Path("/api/numbers")
public class NumberResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String generateISBN() {
        String number = "13-" + new Random().nextInt(100_000_000);
        System.out.println("### " + number);
        return number;
    }
}
