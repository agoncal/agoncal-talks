# Java EE Micro Services and Angular (University) 

## Demo 01

### REST Service 

* Show `RoomEndpoint`
* Run war in WildFly
* Check URL `http://localhost:8080/demo01-conference-venue/api/rooms`

### Arquillian Test

* Show `RoomEndpointTest`
* `mvn clean test  -Parquillian-wildfly-remote`
* `mvn clean test  -Parquillian-wildfly-managed`

### WildFly Swarm

* Show `pom.xml` and profile `swarm`
* `mvn clean package`
* `ll target/`
* `mvn clean package -Pswarm`
* `ll target/`

### Docker

* `mvn clean package -Pdocker-war`
* `mvn clean package -Pswarm,docker-jar`
* Look at the image creation time and image size
* `docker run -p8080:8080 <hash>`
* Change the repository class and re create the image
