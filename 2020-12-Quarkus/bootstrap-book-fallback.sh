#!/usr/bin/env bash
mvn -U io.quarkus:quarkus-maven-plugin:1.13.7.Final:create \
    -DprojectGroupId=org.agoncal.talk.quarkus.microservices \
    -DprojectArtifactId=book-fallback \
    -DprojectVersion=1.0-SNAPSHOT \
    -DclassName="org.agoncal.talk.quarkus.bookfallback.BookFallbackSubscriber" \
    -Dpath="/api/books" \
    -Dextensions="resteasy, smallrye-reactive-messaging-kafka"

mvn quarkus:add-extension -Dextensions="container-image-docker"
