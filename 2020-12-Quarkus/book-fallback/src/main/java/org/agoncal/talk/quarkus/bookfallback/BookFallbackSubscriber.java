package org.agoncal.talk.quarkus.bookfallback;

import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.reactive.messaging.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.stream.Stream;

@ApplicationScoped
public class BookFallbackSubscriber {

    @Inject
    Logger logger;

    @Incoming("failed-books")
    public void bookToBeCreatedLater(String book) {
        logger.info("### Book to be created later " + book);
    }
}
