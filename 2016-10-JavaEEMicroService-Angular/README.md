# Java EE Micro Services and Angular (University) 

## Demo 01 - WildFly Swarm

### Size of War / Jar

* Show `pom.xml` and profile `swarm`
* `mvn clean package`
* `ll target/`
* `mvn clean package -Pswarm`
* `ll target/`

### Execute

* Show `RoomEndpoint`
* Run Jar `java -jar target/demo01-conference-venue-swarm.jar`
* Check URL `http://localhost:9191/demo01-conference-venue/api/rooms`

## Demo 02 - NG CLI

Set Yarn as the default package manager

```
ng set --global packageManager=yarn
```

Then create the project 

```
ng version
ng new conference-web

```

## Demo 03 - Docker

### Clean images

* `docker images`
* `/bin/bash -c 'docker rmi -f $(docker images -q --filter dangling=true)'`
* `/bin/bash -c 'docker rmi -f $(docker images "agoncal/demo*" -q)'`

### Build both Jar/War Docker images

* `mvn clean package -Pdocker-war`
* `mvn clean package -Pswarm,docker-jar`
* Look at the image creation time and image size
* `docker run -p8080:8080 <hash>`
* `docker run -p9191:9191 <hash>`
* Change the repository class and re create the image
