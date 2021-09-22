package org.agoncal.talk.quarkus.bookfallback;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.inject.Inject;

public class BookFallbackSubscriber {

    @Inject
    Logger logger;

    @Incoming("failed-books")
    public void bookToBeCreatedLater(String book) {
        logger.info("### Book to be created later " + book);
    }
}
