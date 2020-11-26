# Quarkus Micro Services 

## Setup

* Print this doc with BBEdit
* Make sure batteries are charged (keyboard, trackpad, etc.)
* Phone on mute
* Stop WhatsApp, Slack, Skype, etc.
* Bigger mouser pointer: Preferences -> Accesibility -> Display -> Cursor (to middle)
* Remove previous Docker image `docker image ls | grep agoncal` and `docker image rm <sha1>` for `agoncal/number`
* Open an Araxis Merge with both project code so I can compare

### Intellij IDEA

* In `$CODE_HOME/temp` remove everything `rm -rf *` and `mkdir microservices`
* Copy the structure with `cp -R $CODE_HOME/Agoncal/agoncal-talks/2020-12-Quarkus/ microservices`
* Remove `rm -rf .idea/ book/ book-fallback/ number/ microservices.iml`
* Remove the `<modules>` in `pom.xml`
* Open the project in Intellij
* Switch to `Bigger` in `Editor -> Color Scheme`

### Terminal

* Switch to `Presentation` profile
* iTerm2 create 4 terminal
* Each terminal type `CMD+I` the name of the tab (in order Book, Number, Backup) and `ESC`
* `cd $CODE_HOME/Temp/microservices` in each tab

## Demo 01 - Book

### Bootstrap

* Execute `bootstrap-book.sh`
* `curl http://localhost:8080/api/books`

### Configure Listening Port

* Change listening port `quarkus.http.port=8702`
* `curl http://localhost:8702/api/books`

### Change code

* In `BookResource` rename the `hello` method in `Book createAQuarkusBook(String title)`
* Change `@GET` to `@POST` and `@Produces(MediaType.APPLICATION_JSON)` 
* It consumes TEXT `@Consumes(MediaType.TEXT_PLAIN)`
* In `BookResource` create an inner class
```
public class Book { 
  public String title; 
  public String topic; 
  public Instant createdAt = Instant.now();
}
```
* Add to the body of the method
```
    Book book = new Book();
    book.title = title;
    book.topic = "Quarkus";
    System.out.println("### Book created " + book);
    return book;
```

* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books`

### Swagger UI

* Go to http://localhost:8702/swagger-ui Swagger UI is not there
* Without stopping Quarkus `mvn quarkus:add-extension -Dextensions="smallrye-openapi"`
* Go back to http://localhost:8702/swagger-ui
* Create books

### Change test

* Rename `testHelloEndpoint` in `shouldCreateAQuarkusBook`
* Change the `given` to `given().body("title of the book")`
* Change the `when` from a `get` to `.post("/api/books").`
* Change the body 
```
    .body("title", is("title of the book"))
    .body("topic", is("Quarkus"))
    .body("$", hasKey("createdAt"));
```

### Add ISBN

* Change test and add `.body("$", hasKey("isbn"));`
* `mvn test` fail
* In `Book` add `public String isbn;`
* In `createAQuarkusBook` and add `book.isbn = "We need to invoke a microservice";`
* `mvn test` pass

## Demo 02 - Number

### Bootstrap

* Execute `bootstrap-number.sh`

### Configure Listening Port

* Change listening port `quarkus.http.port=8701`

### Change code

* In `NumberResource` rename the `hello` method in `generateISBN`
* Change the body
```
    String number = "13-" + new Random().nextInt(100_000_000);
    System.out.println("### " + number);
    return number;
```
* Change the `NumberResourceTest` to test `.body(startsWith("13-"));`
* `mvn test`
* `curl http://localhost:8701/api/numbers`

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

* `mvn test` pass
* Kill Number
* `mvn test` fails
* In `src/test` generate new class `MockNumberProxy`
* Implements `NumberProxy` and implement the method `generateISBN`
* `return "mock isbn";`
* Add `@Mock @RestClient`
* `mvn test` succeeds

## Demo 04 - Fallback

### Kill Number 

* Kill Number
* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books`
* Show the logs of Book

### Add Fallback 

* Add fallback extension `mvn quarkus:add-extension -Dextensions="smallrye-fault-tolerance"`
* In `BookResource` copy/paste the metho `createAQuarkusBook` and change the name to `fallbackOnCreateAQuarkusBook`
* Change isbn to `book.isbn = "needs to be set later as the Number microservices is down"`
* Add `@Fallback(fallbackMethod = "fallbackOnCreateAQuarkusBook")`
* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books`
* Start Number, curl, Kill Number, curl

## Demo 05 - Kafka

### Send the book to a channel 

* Add Kafka extension `mvn quarkus:add-extension -Dextensions="smallrye-reactive-messaging-kafka"`
* Add the channel `@Inject @Channel("failed-books") Emitter<String> failedBook;`
* In `fallbackOnCreateAQuarkusBook` add `failedBook.send(book.toString());`
* Generate a `toString()` in `Book`
* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books`
* Exception look for `No subscriber found for the channel failed-books`

### Configure the channel

* Add the configuration
```
mp.messaging.outgoing.failed-books.connector=smallrye-kafka
mp.messaging.outgoing.failed-books.value.serializer=org.apache.kafka.common.serialization.StringSerializer
```
* Logs on Book `could not be established. Broker may not be available`
* Start Kafka `docker-compose -f infrastructure/docker-compose.yaml up -d`
* Logs have stopped 
* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books`

### Create the Book Fallback

* Execute `bootstrap-book.sh`
* In `BookFallbackSubscriber` remove the `@Path` and add the following method
```
@Incoming("failed-books")
public void bookToBeCreatedLater(String book) {
    System.out.println("### " + book);
}
```
* Make sure the method returns `void`

### Configure the channel

* Copy the conf in `application.properties` change `outgoing` with `incoming`, `serializer` with `deserializer` and `StringSerializer` with `StringDeserializer`

### Start and Kill the Number microservice 

## Demo 06 - Packaging

### Package

* `mvn clean package`
* `ll target` show size of the jar and `tree target/lib`
* Execute the runner `java -jar target/number-1.0-SNAPSHOT-runner.jar`
* Uber Jar `mvn clean package -Dquarkus.package.type=uber-jar`
* `ll target` show size of the jar no more `lib`

### Native Executable

* `mvn clean package -Dquarkus.package.type=native`
* `ll target` show size of the executable
* `./target/number-1.0-SNAPSHOT-runner`
* `curl http://localhost:8701/api/numbers`

### Native Linux Executable

* `mvn clean package -Dquarkus.package.type=native -Dquarkus.native.container-build=true`
* `ll target` show size of the executable
* `./target/number-1.0-SNAPSHOT-runner`
* could not be run by the operating system

### Docker

* Add Docker extension `mvn quarkus:add-extension -Dextensions="container-image-docker"`
* `mvn clean package -Dquarkus.package.type=native -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true`
* Show the `Dockerfile.native` file
* `docker image ls | grep agoncal`
* Execute `docker container run -i --rm -p 8701:8701 agoncal/number:1.0-SNAPSHOT`
* `curl http://localhost:8701/api/numbers`
