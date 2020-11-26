# Quarkus Micro Services 

## Demo 01 - Number

### Bootstrap

* Execute `bootstrap-book.sh`
* `curl http://localhost:8080/api/numbers`
* `mvn test`

### Change code

* In `NumberResource` rename the `hello` method in `generateISBN`
* Change the `return` statement to `return "13-" + new Random().nextInt(100_000_000);`
* Change listening port `quarkus.http.port=8701`
* `curl http://localhost:8701/api/numbers`
* Change the `NumberResourceTest` to test `.body(startsWith("13-"));`
* `mvn test`

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
