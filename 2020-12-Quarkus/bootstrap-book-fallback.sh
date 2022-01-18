#!/usr/bin/env bash
mvn -U io.quarkus:quarkus-maven-plugin:create \
    -DprojectGroupId=org.agoncal.talk.quarkus.microservices \
    -DprojectArtifactId=book-fallback \
    -DclassName="org.agoncal.talk.quarkus.bookfallback.MyReactiveMessagingApplication" \
    -Dextensions="resteasy, smallrye-reactive-messaging-kafka"

mvn quarkus:add-extension -Dextensions="container-image-docker"
