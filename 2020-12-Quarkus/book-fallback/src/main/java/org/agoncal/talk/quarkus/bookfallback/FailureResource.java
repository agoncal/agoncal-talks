package org.agoncal.talk.quarkus.bookfallback;

import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/failures")
public class FailureResource {

    @Inject
    Logger logger;

    /**
     * curl http://localhost:8703/api/failures
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Failure> listAllQuarkusFailures() {
        logger.info("### Failures in the database " + Failure.count());
        return Failure.listAll();
    }

    /**
     * curl -X POST -H "Content-Type: application/json" -d '{"message":"my_msg","payload":"my_payload"}' http://localhost:8703/api/failures -v
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Failure createAQuarkusFailure(Failure failure) {
        failure.persist();
        logger.info("### Failure created " + failure);
        return failure;
    }
}
