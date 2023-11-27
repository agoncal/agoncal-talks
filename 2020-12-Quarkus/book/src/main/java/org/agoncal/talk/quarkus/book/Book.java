package org.agoncal.talk.quarkus.book;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

import java.time.Instant;

@Entity
public class Book extends PanacheEntity {

    public String title;
    public String topic;
    public String isbn;
    public Instant createdAt = Instant.now();

    @Override
    public String toString() {
        return "Book{" +
            "title='" + title + '\'' +
            ", topic='" + topic + '\'' +
            ", isbn='" + isbn + '\'' +
            ", createdAt=" + createdAt +
            ", id=" + id +
            '}';
    }
}
