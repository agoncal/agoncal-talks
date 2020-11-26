#!/usr/bin/env bash
mvn io.quarkus:quarkus-maven-plugin:1.10.1.Final:create \
    -DplatformVersion=1.10.1.Final \
    -DprojectGroupId=org.agoncal.talk.quarkus.microservices \
    -DprojectArtifactId=number \
    -DprojectVersion=1.0-SNAPSHOT \
    -DclassName="org.agoncal.talk.quarkus.number.NumberResource" \
    -Dpath="/api/numbers" \
    -Dextensions="resteasy"

cd number

mvn quarkus:add-extension -Dextensions="smallrye-openapi"

mvn quarkus:add-extension -Dextensions="container-image-docker"
