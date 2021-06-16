#!/usr/bin/env bash
mvn -U io.quarkus:quarkus-maven-plugin:1.13.7.Final:create \
    -DprojectGroupId=org.agoncal.talk.quarkus.microservices \
    -DprojectArtifactId=number \
    -DprojectVersion=1.0-SNAPSHOT \
    -DclassName="org.agoncal.talk.quarkus.number.NumberResource" \
    -Dpath="/api/numbers" \
    -Dextensions="resteasy"

cd number

mvn quarkus:add-extension -Dextensions="container-image-docker"
