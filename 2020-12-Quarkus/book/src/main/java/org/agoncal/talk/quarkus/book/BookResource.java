package org.agoncal.talk.quarkus.book;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
     * curl http://localhost:8702/api/books
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> listAllQuarkusBooks() {
        logger.info("### Books in the database " + Book.count());
        return Book.listAll();
    }

    /**
     * curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books -v
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Fallback(fallbackMethod = "fallbackOnCreateAQuarkusBook")
    @Transactional
    public Book createAQuarkusBook(String title) {
        Book book = new Book();
        book.title = title;
        book.topic = "Quarkus";
        book.isbn = proxy.generateISBN();
        book.persist();
        logger.info("### Book created " + book);
        return book;
    }

    public Book fallbackOnCreateAQuarkusBook(String title) {
        Book book = new Book();
        book.title = title;
        book.topic = "Quarkus";
        book.isbn = "needs to be set later as the Number microservices is down";
        logger.warn("### FallBack !!! Book will be created " + book);
        failedBook.send(book.toString());
        return book;
    }
}
