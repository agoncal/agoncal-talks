#!/usr/bin/env bash
mvn -U io.quarkus:quarkus-maven-plugin:create \
    -DprojectGroupId=org.agoncal.talk.quarkus.microservices \
    -DprojectArtifactId=number \
    -DclassName="org.agoncal.talk.quarkus.number.NumberResource" \
    -Dpath="/api/numbers" \
    -Dextensions="resteasy"

cd number

mvn quarkus:add-extension -Dextensions="container-image-docker"
