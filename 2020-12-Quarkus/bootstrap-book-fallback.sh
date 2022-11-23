#!/usr/bin/env bash
mvn io.quarkus:quarkus-maven-plugin:2.14.1.Final:create \
    -DplatformVersion=2.14.1.Final \
    -DprojectGroupId=org.agoncal.talk.quarkus.microservices \
    -DprojectArtifactId=book-fallback \
    -DclassName="org.agoncal.talk.quarkus.bookfallback.MyReactiveMessagingApplication" \
    -Dextensions="resteasy, smallrye-reactive-messaging-kafka"

mvn quarkus:add-extension -Dextensions="container-image-docker"
