#!/usr/bin/env bash
mvn io.quarkus:quarkus-maven-plugin:2.14.1.Final:create \
    -DplatformVersion=2.14.1.Final \
    -DprojectGroupId=org.agoncal.talk.quarkus.microservices \
    -DprojectArtifactId=number \
    -DclassName="org.agoncal.talk.quarkus.number.NumberResource" \
    -Dpath="/api/numbers" \
    -Dextensions="resteasy"

cd number

mvn quarkus:add-extension -Dextensions="container-image-docker"
