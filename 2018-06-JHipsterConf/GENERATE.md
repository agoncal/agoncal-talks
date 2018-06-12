# Custom and Generated Code Side by Side with JHipster

This code was developped to illustrate the purpose of my **Custom and Generated Code Side by Side with JHipster** talk at JHipster Conf in June 2018.

With JHipster you usually bootstrap some code, and change it. This has many advantages but also drawbacks: 

* not always easy to iterate and update your model frequently
* keeping up with JHipster upgrade might be difficult

The idea of the talk is to show how you can iterate your model often, upgrade JHipster version frequently, generate code... while keeping your own code side by side with JHipster.

## Generate the project

This repository has a `.yo-rc.json` file and a JDL one for the model. So you can just pick up these two files and execute :

```
$ jhipster
$ jhipster import organisation.jdl

```
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
