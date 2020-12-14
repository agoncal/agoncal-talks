#!/usr/bin/env bash
mvn io.quarkus:quarkus-maven-plugin:1.10.3.Final:create \
    -DplatformVersion=1.10.3.Final \
    -DprojectGroupId=org.agoncal.talk.quarkus.microservices \
    -DprojectArtifactId=book-fallback \
    -DprojectVersion=1.0-SNAPSHOT \
    -DclassName="org.agoncal.talk.quarkus.bookfallback.BookFallbackSubscriber" \
    -Dpath="/api/books" \
    -Dextensions="resteasy, smallrye-reactive-messaging-kafka"

mvn quarkus:add-extension -Dextensions="container-image-docker"
