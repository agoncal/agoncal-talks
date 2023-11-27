#!/usr/bin/env bash
mvn io.quarkus:quarkus-maven-plugin:3.6.0:create \
    -DplatformVersion=3.6.0 \
    -DprojectGroupId=org.agoncal.talk.quarkus.microservices \
    -DprojectArtifactId=book-fallback \
    -DclassName="org.agoncal.talk.quarkus.bookfallback.MyReactiveMessagingApplication" \
    -Dextensions="resteasy, smallrye-reactive-messaging-kafka"

mvn quarkus:add-extension -Dextensions="container-image-docker"
