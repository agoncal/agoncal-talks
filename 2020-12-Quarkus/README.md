# Quarkus Micro Services 

## Demo 01 - Book

### Bootstrap

* Execute `bootstrap-book.sh`
* `curl http://localhost:8080/api/books`
* `mvn test`

### Configure Listening Port

* Change listening port `quarkus.http.port=8702`
* `curl http://localhost:8702/api/books`

### Change code

* In `BookResource` rename the `hello` method in `Book createAQuarkusBook(String title)`
* Cgange `@GET` to `@POST` and `@Produces(MediaType.APPLICATION_JSON)` 
* In `BookResource` create an inner class `public class Book { public String title; public String topic; }`
* It consumes TEXT `@Consumes(MediaType.TEXT_PLAIN)`
* Add to the body of the method `Book book = new Book(); book.title = title; return book;`
* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books -v`

### Swagger UI

* Go to http://localhost:8702/swagger-ui Swagger UI is not there
* Without stopping Quarkus `mvn quarkus:add-extension -Dextensions="smallrye-openapi"`
* Go back to http://localhost:8702/swagger-ui
* Create books

### Change test

* Rename `testHelloEndpoint` in `shouldCreateABook`
* Change the `given` to `given().body("title of the book", ObjectMapperType.JSONB)`
* Change the `when` from a `get` to `.post("/api/books").`
* Change the body `.body("title", is("title of the book")).body("topic", is("Quarkus"));`

### Add ISBN

* In `Book` add `public String isbn;`
* Change test and add `.body("$", hasKey("isbn"));`
* `mvn test` fail
* Go back to `createAQuarkusBook` and add `book.isbn = "We need to invoke a microservice";`
* `mvn test` pass

## Demo 02 - Number

### Bootstrap

* Execute `bootstrap-number.sh`

### Configure Listening Port

* Change listening port `quarkus.http.port=8701`
* `curl http://localhost:8701/api/numbers`
* `mvn test`

### Change code

* In `NumberResource` rename the `hello` method in `generateISBN`
* Change the `return` statement to `return "13-" + new Random().nextInt(100_000_000);`
* Change listening port `quarkus.http.port=8701`
* `curl http://localhost:8701/api/numbers`
* Change the `NumberResourceTest` to test `.body(startsWith("13-"));`
* `mvn test`

## Demo 03 - Rest Client

### Create the Proxy

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

## Demo 04 - Fallback

### Kill Number 

* Kill Number
* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books`
* Show the logs of Book

### Add Fallback 

* Add fallback extension `mvn quarkus:add-extension -Dextensions="smallrye-fault-tolerance"`
* In `BookResource` copy/paste the metho `createAQuarkusBook` and change the name to `fallbackOnCreateAQuarkusBook`
* Change isbn to `book.isbn = "needs to be set later";`
* Add `@Fallback(fallbackMethod = "fallbackOnCreateAQuarkusBook")`

## Demo 05 - Send the book to Kafka

### Send the book to a channel 

* Add Kafka extension `mvn quarkus:add-extension -Dextensions="smallrye-reactive-messaging-kafka"`
* Add the channel `@Inject @Channel("failed-book") Emitter<String> failedBook;`
* In `fallbackOnCreateAQuarkusBook` add `failedBook.send(book.toString());`
* Generate a `toString()` in `Book`
* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books`
* Exception look for `No subscriber found for the channel failed-book`

### Configure the channel

* Add the configuration
```
mp.messaging.outgoing.failed-book.connector=smallrye-kafka
mp.messaging.outgoing.failed-book.value.serializer=org.apache.kafka.common.serialization.StringSerializer
```
* Logs on Book `could not be established. Broker may not be available`
* Start Kafka `docker-compose -f infrastructure/docker-compose.yaml up -d`
* Logs have stopped 
* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books`

### Create the Book Backup

* Execute `bootstrap-book.sh`
* In `BookFallbackSubscriber` remove the `@Path` and add the following method
```
@Incoming("failed-book")
public void bookToBeCreatedLater(String book) {
    System.out.println("### Invalid book needs to be created later");
    System.out.println(book);
}
```

### Configure the channel

* Copy the conf in `application.properties` change `outgoing` with `incoming`, `serializer` with `deserializer` and `StringSerializer` with `StringDeserializer`
