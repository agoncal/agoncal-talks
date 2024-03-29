#!/usr/bin/env bash
mvn io.quarkus:quarkus-maven-plugin:3.6.0:create \
    -DplatformVersion=3.6.0 \
    -DprojectGroupId=org.agoncal.talk.quarkus.microservices \
    -DprojectArtifactId=book \
    -DclassName="org.agoncal.talk.quarkus.book.BookResource" \
    -Dpath="/api/books" \
    -Dextensions="resteasy, resteasy-jsonb"

cd book

mvn quarkus:add-extension -Dextensions="smallrye-openapi"

mvn quarkus:add-extension -Dextensions="rest-client"

mvn quarkus:add-extension -Dextensions="smallrye-fault-tolerance"

mvn quarkus:add-extension -Dextensions="smallrye-reactive-messaging-kafka"

mvn quarkus:add-extension -Dextensions="container-image-docker"
