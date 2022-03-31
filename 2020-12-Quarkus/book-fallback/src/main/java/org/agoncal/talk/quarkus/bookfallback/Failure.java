package org.agoncal.talk.quarkus.bookfallback;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

import java.time.Instant;

public class Failure extends PanacheMongoEntity {

    public String message;
    public String payload;
    public Instant createdAt = Instant.now();

    @Override
    public String toString() {
        return "Failure{" +
            "message='" + message + '\'' +
            ", payload='" + payload + '\'' +
            ", createdAt=" + createdAt +
            '}';
    }
}
