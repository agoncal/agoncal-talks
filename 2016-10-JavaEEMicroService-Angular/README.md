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
ng generate component venue

```

Execute it and go to [http://localhost:4200/]()

```
yarn run start
```

## Demo 03 - Docker

### Clean images

* `docker image ls "agoncal/demo*"`
* `docker image prune`
* `/bin/bash -c 'docker image rm $(docker image ls "agoncal/demo*" -q) -f'`

### Build both Jar/War Docker images

* `mvn clean package -Pdocker-war`
* `mvn clean package -Pswarm,docker-jar`
* Look at the image creation time and image size
* `docker container run -p8080:8080 <hash war>`
* `docker container run -p9191:9191 <hash jar>`
* Change the repository class and re create the image

## Demo 04 - Swagger and Angular Generation

### Swagger

* In `RoomEndpoint` show the Swagger annotations
* In `pom.xml` show `swagger-maven-plugin`
* Execute `mvn clean install -Pswarm`
* Show under `webapp` the file `swagger.yaml`
* Run Jar `java -jar target/demo01-conference-venue-swarm.jar`
* Check URL `http://localhost:9191/demo01-conference-venue/swagger.json`

### Generate Angular from Swagger

/|\ Make sure to be under `demo01` directory

* `swagger-codegen help`
* `swagger-codegen generate -i http://localhost:9191/demo01-conference-venue/swagger.json -l typescript-angular2 -o conference-web/src/app/`
* Show ` conference-web/src/app/api` and `model`
