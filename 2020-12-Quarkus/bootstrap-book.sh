#!/usr/bin/env bash
mvn -U io.quarkus:quarkus-maven-plugin:1.13.7.Final:create \
    -DprojectGroupId=org.agoncal.talk.quarkus.microservices \
    -DprojectArtifactId=book \
    -DprojectVersion=1.0-SNAPSHOT \
    -DclassName="org.agoncal.talk.quarkus.book.BookResource" \
    -Dpath="/api/books" \
    -Dextensions="resteasy, resteasy-jsonb"

cd book

mvn quarkus:add-extension -Dextensions="smallrye-openapi"

mvn quarkus:add-extension -Dextensions="rest-client"

mvn quarkus:add-extension -Dextensions="smallrye-fault-tolerance"

mvn quarkus:add-extension -Dextensions="smallrye-reactive-messaging-kafka"

mvn quarkus:add-extension -Dextensions="container-image-docker"
