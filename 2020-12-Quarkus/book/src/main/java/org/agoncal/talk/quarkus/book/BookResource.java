package org.agoncal.talk.quarkus.book;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Instant;

@Path("/api/books")
public class BookResource {

    @Inject
    Logger logger;

    @RestClient
    NumberProxy proxy;

    @Inject
    @Channel("failed-books")
    Emitter<String> failedBook;

    /**
     * curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books -v
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Fallback(fallbackMethod = "fallbackOnCreateAQuarkusBook")
    public Book createAQuarkusBook(String title) {
        Book book = new Book();
        book.title = title;
        book.topic = "Quarkus";
        book.isbn = proxy.generateISBN();
        logger.info("### Book created " + book);
        return book;
    }

    public Book fallbackOnCreateAQuarkusBook(String title) {
        Book book = new Book();
        book.title = title;
        book.topic = "Quarkus";
        book.isbn = "needs to be set later as the Number microservices is down";
        logger.warn("### FallBack !!! Book will be created later " + book);
        failedBook.send(book.toString());
        return book;
    }

    public class Book {
        public String title;
        public String topic;
        public Instant createdAt = Instant.now();
        public String isbn;

        @Override
        public String toString() {
            return "Book{" +
                "title='" + title + '\'' +
                ", topic='" + topic + '\'' +
                ", createdAt=" + createdAt +
                ", isbn='" + isbn + '\'' +
                '}';
        }
    }
}
