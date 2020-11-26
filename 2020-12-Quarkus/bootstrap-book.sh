#!/usr/bin/env bash
mvn io.quarkus:quarkus-maven-plugin:1.10.1.Final:create \
    -DplatformVersion=1.10.1.Final \
    -DprojectGroupId=org.agoncal.talk.quarkus.microservices \
    -DprojectArtifactId=book \
    -DprojectVersion=1.0-SNAPSHOT \
    -DclassName="org.agoncal.talk.quarkus.book.BookResource" \
    -Dpath="/api/books" \
    -Dextensions="resteasy, resteasy-jsonb, smallrye-openapi"

./mvnw quarkus:add-extension -Dextensions="rest-client"

./mvnw quarkus:add-extension -Dextensions="smallrye-fault-tolerance"

./mvnw quarkus:add-extension -Dextensions="smallrye-reactive-messaging-kafka"

./mvnw quarkus:add-extension -Dextensions="container-image-docker"
