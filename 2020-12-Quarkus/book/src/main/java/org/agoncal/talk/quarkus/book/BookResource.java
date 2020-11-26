package org.agoncal.talk.quarkus.book;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/books")
public class BookResource {

    @RestClient
    NumberProxy proxy;

    @Inject
    @Channel("failed-book")
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
        return book;
    }

    public Book fallbackOnCreateAQuarkusBook(String title) {
        Book book = new Book();
        book.title = title;
        book.topic = "Quarkus";
        book.isbn = "needs to be set later";
        failedBook.send(book.toString());
        return book;
    }

    public class Book {
        public String title;
        public String topic;
        public String isbn;

        @Override
        public String toString() {
            return "Book{" +
                "title='" + title + '\'' +
                ", topic='" + topic + '\'' +
                ", isbn='" + isbn + '\'' +
                '}';
        }
    }
}
