package org.agoncal.talk.quarkus.bookfallback;

import org.eclipse.microprofile.reactive.messaging.Incoming;

public class BookFallbackSubscriber {

    @Incoming("failed-book")
    public void bookToBeCreatedLater(String book) {
        System.out.println("### Invalid book needs to be created later");
        System.out.println(book);
    }
}
