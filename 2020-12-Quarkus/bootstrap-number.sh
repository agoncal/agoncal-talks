#!/usr/bin/env bash
mvn io.quarkus:quarkus-maven-plugin:3.6.0:create \
    -DplatformVersion=3.6.0 \
    -DprojectGroupId=org.agoncal.talk.quarkus.microservices \
    -DprojectArtifactId=number \
    -DclassName="org.agoncal.talk.quarkus.number.NumberResource" \
    -Dpath="/api/numbers" \
    -Dextensions="resteasy"

cd number

mvn quarkus:add-extension -Dextensions="container-image-docker"
