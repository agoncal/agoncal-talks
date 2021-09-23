# Quarkus Micro Services 

## Setup

* Print this doc with BBEdit
* Make sure batteries are charged (keyboard, trackpad, etc.)
* Phone on mute
* Stop WhatsApp, Slack, Skype, etc.
* Bigger mouser pointer: Preferences -> Accesibility -> Display -> Cursor (to middle)
* Remove previous Docker image `docker image ls | grep agoncal` and `docker image rm <sha1>` for `agoncal/number`
* Open an Araxis Merge with both project code so I can compare
* Start Docker

### Code

* In `$CODE_HOME/temp` remove everything `rm -rf *` and `mkdir microservices`
* Copy the structure with `cp -R $CODE_HOME/Agoncal/agoncal-talks/2020-12-Quarkus/ microservices`
* Copy .gitignore `cp $CODE_HOME/Agoncal/agoncal-talks/.gitignore microservices`
* `cd microservices`  
* Clean with `mvn clean` and then remove `rm -rf .idea/ book/ book-fallback/ number/ microservices.iml`
* Remove the `<modules>` in `pom.xml`
* In all the `bootstrap-*.sh` files, remove everything (`cd`, `quarkus:add-extension`) but the `quarkus-maven-plugin:create`
* `chmod +x *.sh`
* Make sure containers are stopped `docker-compose -f infrastructure/kafka.yaml down`
* Make sure to remove images `docker image ls | grep agoncal`

### Add to Git

* `microservices$ git init`
* `microservices$ git add .`
* `microservices$ git commit -am "init"`

### Intellij IDEA

* Open the project in Intellij
* Switch to `Bigger` in `Editor -> Color Scheme`

### Terminal

* Switch to `Presentation` profile
* Split the terminal into 4 
* `cd $CODE_HOME/Temp/microservices` in each tab

## Demo 01 - Book

### Bootstrap and Configure Listening Port

* Execute `./bootstrap-book.sh`
* Show code
* Change listening port `quarkus.http.port=8702`
* `mvn quarkus:dev`  
* `curl http://localhost:8702/api/books`

### Change code

* In `BookResource` rename the `hello` method in `Book createAQuarkusBook(String title)`
* Change `@GET` to `@POST` 
* It consumes TEXT `@Consumes(MediaType.TEXT_PLAIN)`
* And consumes JSON `@Produces(MediaType.APPLICATION_JSON)` 
* In `BookResource` create an inner class `Book`
* Use `pst` live template in Intellij for `public String`
```
public class Book { 
  public String title; 
  public String topic; 
  public String isbn;
  public Instant createdAt = Instant.now();
}
```
* Generate `toString()` method
* Add to the body of the method `createAQuarkusBook`
```
    Book book = new Book();
    book.title = title;
    book.topic = "Quarkus";
    book.isbn = "We need to invoke a microservice";
    logger.info("### Book created " + book);
    return book;
```

* Inject the JBoss logger
```
  @Inject
  Logger logger;
```
* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books -v | jq`

### Dev UI

* Go to http://localhost:8702/q/dev

### OpenAPI and Swagger UI

* Go to http://localhost:8702/q/swagger-ui Swagger UI is not there
* Without stopping Quarkus `mvn quarkus:add-extension -Dextensions="smallrye-openapi"`
* Go back to http://localhost:8702/q/swagger-ui
* Create books

### Change test

* Tests don't pass
* Rename `testHelloEndpoint` in `shouldCreateAQuarkusBook`
* Change the `given` to `given().body("title of the book")`
* Change the `when` from a `get` to `.post("/api/books").`
* Change the body 
```
    .body("title", is("title of the book"))
    .body("topic", is("Quarkus"))
    .body("$", hasKey("isbn"))
    .body("$", hasKey("createdAt"));
```
* `mvn test`

### Git

* From the root `microservices` directory
```
microservices$ git add .
microservices$ git commit -am "book"
```

## Demo 02 - Number

### Bootstrap and Configure Listening Port

* Execute `./bootstrap-number.sh`
* Change listening port `quarkus.http.port=8701`
* `mvn quarkus:dev`  
* `curl http://localhost:8701/api/numbers`

### Change code

* In `NumberResource` rename the `hello` method in `generateISBN`
* Inject the JBoss logger
```
  @Inject
  Logger logger;
```
* Change the body
```
    String number = "13-" + new Random().nextInt(100_000_000);
    logger.info("### ISBN " + number);
    return number;
```
* Rename test method `hello` to `shouldGenerateISBN`
* Change the `NumberResourceTest` to test `.body(startsWith("13-"));`
* `mvn test`
* `curl http://localhost:8701/api/numbers`

### Git

```
$ git add .
$ git commit -am "number"
```

## Demo 03 - Rest Client

### Create the Proxy

* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books`
* Add the REST Client extension in Book `mvn quarkus:add-extension -Dextensions="rest-client"`
* Show `pom.xml`
* Copy `NumberResource` and rename it to `NumberProxy`
* Change `NumberProxy` to an interface, empty the `generateISBN` method
* Add `@RegisterRestClient` to the interface

### Use the Proxy

* In `BookResource` add `@RestClient NumberProxy proxy;`
* Change the code to  `book.isbn = proxy.generateISBN();`

### Configure the proxy

* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books -v`
* Exception, look for `Unable to determine the proper baseUrl/baseUri`
* Add `org.agoncal.talk.quarkus.book.NumberProxy/mp-rest/url=http://localhost:8701`

### Mock the proxy

* In Book `mvn test` pass
* Kill Number
* `mvn test` fails
* In `src/test` generate new class `MockNumberProxy`
* Implements `NumberProxy` and implement the method `generateISBN`
* `return "mock isbn";`
* Add `@Mock @RestClient`
* `mvn test` succeeds

### Git

```
$ git add .
$ git commit -am "rest client"
```

## Demo 04 - Fallback

### Kill Number 

* Kill Number
* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books`
* Show the logs of Book

### Add Fallback 

* Add fallback extension `mvn quarkus:add-extension -Dextensions="smallrye-fault-tolerance"`
* In `BookResource` copy/paste the method `createAQuarkusBook` and change the name to `fallbackOnCreateAQuarkusBook`
* Change isbn to `book.isbn = "needs to be set later as the Number microservices is down"`
* Change `logger.warn("### FallBack !!! Book will be created later " + book);`
* Add `@Fallback(fallbackMethod = "fallbackOnCreateAQuarkusBook")`
* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books`
* Start Number, curl, Kill Number, curl

### Git

```
$ git add .
$ git commit -am "fallback"
```

## Demo 05 - Kafka

### Send the book to a channel 

* In Book add Kafka extension `mvn quarkus:add-extension -Dextensions="smallrye-reactive-messaging-kafka"`
* Add the channel `@Inject @Channel("failed-books") Emitter<String> failedBook;`
* In `fallbackOnCreateAQuarkusBook` add `failedBook.send(book.toString());`

### Configure the channel

* Add the configuration
```
mp.messaging.outgoing.failed-books.connector=smallrye-kafka
mp.messaging.outgoing.failed-books.value.serializer=org.apache.kafka.common.serialization.StringSerializer
```

### Create the Book Fallback Subscriber

* Execute `./bootstrap-book-fallback.sh`
* In `BookFallbackSubscriber` remove the `@Path`, `@GET`, `@Produces`
* Rename `hello` with `bookToBeCreatedLater` 
```
@Inject
Logger logger;

@Incoming("failed-books")
public void bookToBeCreatedLater(String book) {
    logger.info("### Book to be created later " + book);
}
```
* Make sure the method returns `void`
* Remove the test (delete the `src/main/test` folder) 

### Configure the channel

* Copy the conf in `application.properties` change `outgoing` with `incoming`, `serializer` with `deserializer` and `StringSerializer` with `StringDeserializer`
``` 
mp.messaging.incoming.failed-books.connector=smallrye-kafka
mp.messaging.incoming.failed-books.value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
```
* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books`
* Execute the `loop.sh` script

### Git

```
$ git add .
$ git commit -am "kafka"
```

### Start and Kill the Number microservice 

## Demo 06 - Persist Books (if time)

### Book Entity

* In Book add Panache and Postgres `mvn quarkus:add-extension -Dextensions="jdbc-postgres,hibernate-orm-panache"`
* Add `@Entity` to Book and `extends PanacheEntity`
* In `BookResource.createAQuarkusBook` add `book.persist();`
* Add `@Transactional` to the `createAQuarkusBook` method
* `quarkus.hibernate-orm.database.generation=drop-and-create` 
* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books`

### List Books

* Create a new `listAllQuarkusBooks` method
```
@GET
@Produces(MediaType.APPLICATION_JSON)
public List<Book> listAllQuarkusBooks() {
    logger.info("### Books in the database " + Book.count());
    return Book.listAll();
}
```
* `curl http://localhost:8702/api/books`
* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books`
* `curl http://localhost:8702/api/books`

## Demo 07 - Packaging

### Package

* In Number
* `mvn quarkus:dev` and show startup time  
* `mvn clean package -Dmaven.test.skip=true`
* `ll target/quarkus-app` show size of the jar and `tree target/quarkus-app/`
* Execute the runner ` java -jar target/quarkus-app/quarkus-run.jar`
* Uber Jar `mvn clean package -Dmaven.test.skip=true -Dquarkus.package.type=uber-jar`
* `ll target` show size of the jar no more `lib`

### Native Executable

* `mvn clean package -Dmaven.test.skip=true -Dquarkus.package.type=native`
* `ll target` show size of the executable
* `./target/number-1.0-SNAPSHOT-runner`
* `curl http://localhost:8701/api/numbers`

### Native Linux Executable and Docker

* `docker image ls | grep agoncal`
* To the 3 microservices add Docker extension `mvn quarkus:add-extension -Dextensions="container-image-docker"`
* Show the `Dockerfile.native` file
* `mvn clean package -Dmaven.test.skip=true -Dquarkus.package.type=native -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true`  
* `ll target` show size of the executable
* `./target/number-1.0-SNAPSHOT-runner`
* could not be run by the operating system
* `docker image ls | grep agoncal`
* Execute `docker container run -i --rm -p 8701:8701 agoncal/number:1.0.0-SNAPSHOT`
* `curl http://localhost:8701/api/numbers`

### Execute all with Docker Compose

* `docker image ls | grep agoncal`
* Show `infrastructure/app.yaml`

