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

* In `$CODE/temp` remove everything `rm -rf *` and `mkdir microservices`
* Copy the structure with `cp -R $CODE/Agoncal/agoncal-talks/2020-12-Quarkus/ microservices`
* Copy .gitignore `cp $CODE/Agoncal/agoncal-talks/.gitignore microservices`
* `cd microservices`  
* Clean with `mvn clean` and then remove `rm -rf .idea/ book/ book-fallback/ number/ microservices.iml`
* Remove the `<modules>` in `pom.xml`
* In all the `bootstrap-*.sh` files, remove everything (`cd`, `quarkus:add-extension`) but the `quarkus-maven-plugin:create`
* `chmod +x *.sh`
* Make sure containers are stopped `docker-compose -f infrastructure/kafka.yaml down`
* Make sure to remove images `docker image ls | grep agoncal`
* Make sure listening ports are free `lsof -i tcp:8701` and `lsof -i tcp:8702`

### Add to Git

* `microservices$ git init`
* `microservices$ git add .`
* `microservices$ git commit -am "init"`

### Intellij IDEA

* Open the project in Intellij
* Switch to `Bigger` in `Editor -> Color Scheme` in the preferences

### Terminal

* Switch to `Presentation` profile
* Split the terminal into 4 
* `cd $CODE/Temp/microservices` in each tab

## Demo 01 - Book

### Bootstrap and Configure Listening Port

* Execute `./bootstrap-book.sh`
* Show code
* Change listening port `quarkus.http.port=8702`
* `mvn quarkus:dev`  
* `curl http://localhost:8702/api/books`

### Show continuous testing

* Change the `BookResource` to return a different value
* Execute continuous testing `mvn quarkus:dev`
* Fix the test

### Change BookResource

* In `BookResource` rename the `hello` method in `Book createAQuarkusBook(String title)`
* Change `@GET` to `@POST` 
* It consumes TEXT `@Consumes(MediaType.TEXT_PLAIN)`
* And consumes JSON `@Produces(MediaType.APPLICATION_JSON)` 

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

### Create Book

* Create a separate class `Book`
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

### Execute

* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books -v | jq`

### Dev UI

* Go to http://localhost:8702/q/dev
* There is no Swagger UI

### OpenAPI and Swagger UI

* Without stopping Quarkus `mvn quarkus:add-extension -Dextensions="smallrye-openapi"`
* Go back to http://localhost:8702/q/dev and show Swagger UI http://localhost:8702/q/swagger-ui
* Create books

### Change test

* Press `r` in the continuous testing
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
* Press `r` in the continuous testing
* Show `mvn test` while Quarkus is running

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

* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books | jq`
* Add the REST Client extension in Book `mvn quarkus:add-extension -Dextensions="rest-client"`
* Show `pom.xml`
* Copy `NumberResource` and rename it to `NumberProxy`
* Change `NumberProxy` to an interface, empty the `generateISBN` method
* Add `@RegisterRestClient` to the interface
* Make sure the interface is `public`

### Use the Proxy

* In `BookResource` add `@RestClient NumberProxy proxy;`
* Change the code to  `book.isbn = proxy.generateISBN();`

### Configure the proxy

* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books -v`
* Exception, look for `Unable to determine the proper baseUrl/baseUri`
* Add `quarkus.rest-client."org.agoncal.talk.quarkus.book.NumberProxy".url=http://localhost:8701`

### Mock the proxy

* In Book run the continuous tests (or `mvn test`), show they pass
* Kill Number
* Run the continuous tests, show they fail
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

* In Book, add fallback extension `mvn quarkus:add-extension -Dextensions="smallrye-fault-tolerance"`
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

* Stop the Book microservice
* In Book add Kafka extension `mvn quarkus:add-extension -Dextensions="smallrye-reactive-messaging-kafka"`
* In `BookResource` add the channel `@Inject @Channel("failed-books") Emitter<String> failedBook;`
* In `fallbackOnCreateAQuarkusBook` add `failedBook.send(book.toString());`

### Configure the channel

* Add the configuration
```
mp.messaging.outgoing.failed-books.connector=smallrye-kafka
mp.messaging.outgoing.failed-books.value.serializer=org.apache.kafka.common.serialization.StringSerializer
```

### Create the Book Fallback Subscriber

* Execute `./bootstrap-book-fallback.sh`
* Rename `MyReactiveMessagingApplication` in `BookFallbackSubscriber`
* In `BookFallbackSubscriber` remove the `Emitter` attribute, `onStart` and `toUpperCase` methods
* Rename `sink` with `bookToBeCreatedLater` 
```
@Inject
Logger logger;

@Incoming("failed-books")
public void bookToBeCreatedLater(String book) {
    logger.info("### Book to be created later " + book);
}
```
* Remove the test (delete the `src/main/test` folder) 

### Configure the channel

* Copy the conf in `application.properties` change `outgoing` with `incoming`, `serializer` with `deserializer` and `StringSerializer` with `StringDeserializer`
``` 
mp.messaging.incoming.failed-books.connector=smallrye-kafka
mp.messaging.incoming.failed-books.value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

### Execute

* Show the Docker Dashboard
* Start both Quarkus (with a Maven Clean)
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
* In the Book.toString() method add the id that is inherited `", id=" + id +`
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
* `curl http://localhost:8702/api/books | jq`
* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books`
* `curl http://localhost:8702/api/books | jq`

### Git

```
$ git add .
$ git commit -am "entity"
```

## Demo 07 - Augmentation

* In Book
* Set the property `quarkus.package.vineflower.enabled=true`
* Package Book with `mvn clean package -Dmaven.test.skip=true`
* Show byte code in `target/decompiled`
* Show `generated-bytecode/META-INF/quarkus-generated-openapi-doc.JSON`
* Show all the generated methods in `transformed-bytecode/org/agoncal/talk/quarkus/book/Book.java`
* Show the setters in `transformed-bytecode/org/agoncal/talk/quarkus/book/BookResource.java`

## Demo 08 - Packaging

### Package

* In Number
* `mvn quarkus:dev` and show startup time  
* `mvn clean package -Dmaven.test.skip=true`
* `ll target/quarkus-app` show size of the jar and `tree target/quarkus-app/`
* Execute the runner `java -jar target/quarkus-app/quarkus-run.jar`
* Uber Jar `mvn clean package -Dmaven.test.skip=true -Dquarkus.package.type=uber-jar`
* `ll target` show size of the jar no more `lib`
* Execute the runner `java -jar target/number-1.0.0-SNAPSHOT-runner.jar`

### Native Executable

Make sure GraalVMN and JDK are align
* `sdk use java 17.0.9-graal`
* `ls -al ~/.sdkman/candidates/java/`
* `GRAALVM_HOME=~/.sdkman/candidates/java/17.0.9-graal`

* `mvn clean package -Dmaven.test.skip=true -Dquarkus.package.type=native`
* `ll target` show size of the executable
* `./target/number-1.0.0-SNAPSHOT-runner`
* `curl http://localhost:8701/api/numbers`

### Native Linux Executable and Docker

* Show Docker dashboard
* `docker image ls | grep agoncal`
* To the 3 microservices add Docker extension `mvn quarkus:add-extension -Dextensions="container-image-docker"`
* Show the `Dockerfile.native` file
* `mvn clean package -Dmaven.test.skip=true -Dquarkus.package.type=native -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true`  
* `ll target` show size of the executable
* `./target/number-1.0.0-SNAPSHOT-runner`
* could not be run by the operating system
* `docker image ls | grep agoncal`
* Execute `docker container run -i --rm -p 8701:8701 agoncal/number:1.0.0-SNAPSHOT`
* `curl http://localhost:8701/api/numbers`

### Execute all with Docker Compose

* `docker image ls | grep agoncal`
* Show `infrastructure/app.yaml`
* Run `docker-compose -f infrastructure/app.yaml up`
* Show Docker Desktop
* `curl http://localhost:8701/api/numbers`
* `curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books`
* `curl http://localhost:8702/api/books | jq`
* `curl http://localhost:8703/api/failures | jq`

## Deploying to Azure Container Apps

[Azure Container Apps](https://docs.microsoft.com/en-us/azure/container-apps/) allows to run containerized applications without worrying about orchestration or infrastructure (i.e. we don't have to directly use K8s, it's used under the hoods).
For that, the Docker images need to be publicly accessible.

### Building and Pushing Docker images to Docker Hub

You can build the image using JVM mode with:

```shell
mvn clean package -Dmaven.test.skip=true -Dquarkus.container-image.build=true
```

If you want a specific tag with your images you can use:

```shell
mvn clean package -Dmaven.test.skip=true -Dquarkus.container-image.build=true -Dquarkus.container-image.tag=azure
```

If you want to build the native images, you can do so with the following command on the 3 microservices (using the SNAPSHOT tag):

```shell
mvn clean package -Dmaven.test.skip=true -Dquarkus.package.type=native -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true
```

If you want to tag your Docker image, you can use the `quarkus.container-image.tag` parameter

```shell
mvn clean package -Dmaven.test.skip=true -Dquarkus.package.type=native -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true -Dquarkus.container-image.tag=azure
```

To push the images to Docker Hub, execute the following commands:

```shell
$ docker login
$ docker push agoncal/book-fallback:1.0.0-SNAPSHOT
$ docker push agoncal/book:1.0.0-SNAPSHOT
$ docker push agoncal/number:1.0.0-SNAPSHOT
```

### Setup

* Sign in to Azure from the CLI:

```shell
az login
```

* Install the Azure Container Apps extension for the CLI:

```shell
az extension add --source https://workerappscliextension.blob.core.windows.net/azure-cli-extension/containerapp-0.2.4-py2.py3-none-any.whl
```

* Register the Microsoft.Web namespace

```shell
az provider register --namespace Microsoft.Web
```

* Set the following environment variables

```shell
RESOURCE_GROUP="rg-bookstore"
LOCATION="eastus2"
# Container Apps
LOG_ANALYTICS_WORKSPACE="bookstore-apps-logs"
CONTAINERAPPS_ENVIRONMENT="bookstore-apps-env"
# Kafka
EVENTHUB_NAMESPACE="bookstore-event"
EVENTHUB_TOPIC="failed-books-topic"
# Number
NUMBER_APP="number-app"
# Book
BOOK_APP="book-app"
BOOK_DB="bookstore-db"
BOOK_DB_SCHEMA="book"
BOOK_DB_USER="userbookstore"
BOOK_DB_PWD="p#ssw0rd-12046"
# Book Fallback
FAILURE_APP="failure-app"
FAILURE_DB="failure-db"
FAILURE_DB_SCHEMA="failures"
```

* Create a resource group 

```shell
az group create \
  --name $RESOURCE_GROUP \
  --location $LOCATION
```

### Create a Container Apps environment

* Create a Log Analytics workspace

```shell
az monitor log-analytics workspace create \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --workspace-name $LOG_ANALYTICS_WORKSPACE
```

* Retrieve the Log Analytics Client ID and client secret:

```shell
LOG_ANALYTICS_WORKSPACE_CLIENT_ID=`az monitor log-analytics workspace show --query customerId -g $RESOURCE_GROUP -n $LOG_ANALYTICS_WORKSPACE -o tsv | tr -d '[:space:]'`
echo $LOG_ANALYTICS_WORKSPACE_CLIENT_ID
LOG_ANALYTICS_WORKSPACE_CLIENT_SECRET=`az monitor log-analytics workspace get-shared-keys --query primarySharedKey -g $RESOURCE_GROUP -n $LOG_ANALYTICS_WORKSPACE -o tsv | tr -d '[:space:]'`
echo $LOG_ANALYTICS_WORKSPACE_CLIENT_SECRET
```

* Create the container apps environment:

````shell
az containerapp env create \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --name $CONTAINERAPPS_ENVIRONMENT \
  --logs-workspace-id $LOG_ANALYTICS_WORKSPACE_CLIENT_ID \
  --logs-workspace-key $LOG_ANALYTICS_WORKSPACE_CLIENT_SECRET
````

### Create the managed Postgres Database

We need to create a PostgreSQL so the Book microservice can store data.
To know which SKUs are available in a region, execute the following command:

```shell
az postgres server list-skus --location $LOCATION
```

Then, create a database in the region where it's available:

```shell
az postgres server create \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --name $BOOK_DB \
  --public all \
  --admin-user $BOOK_DB_USER \
  --admin-password $BOOK_DB_PWD \
  --sku-name B_Gen5_1 \
  --storage-size 5120 \
  --version 11
```

Create the Book database:

```shell
az postgres db create \
    --resource-group $RESOURCE_GROUP \
    --name $BOOK_DB_SCHEMA \
    --server-name $BOOK_DB
```

If you want to access the Postgres database from your local machine, you need to give access to your local IP address.
A convenient way to know the local IP address is to go to http://whatismyip.akamai.com:

```shell
az postgres server firewall-rule create \
    --resource-group $RESOURCE_GROUP \
    --name $BOOK_DB-allow-local-ip \
    --server $BOOK_DB \
    --start-ip-address <LOCAL_IP_ADDRESS> \
    --end-ip-address <LOCAL_IP_ADDRESS>
```

You can check the firewall rules with:

````shell
az postgres server firewall-rule list  \
    --resource-group $RESOURCE_GROUP \
    --server-name $BOOK_DB \
    --out table
````

Get the connection string with the following command so you can connect to it:

```shell
az postgres server show-connection-string \
  --database-name $BOOK_DB \
  --admin-user $BOOK_DB_USER \
  --admin-password $BOOK_DB_PWD \
  --query connectionStrings.jdbc
```

Add this connection string to the `application.properties` file, with the `prod` profile:

```shell
%prod.quarkus.datasource.username=userbookstore@bookstore-db-antoniomanug.postgres.database.azure.com
%prod.quarkus.datasource.password=p#ssw0rd-12046
%prod.quarkus.datasource.jdbc.url=jdbc:postgresql://bookstore-db-antoniomanug.postgres.database.azure.com:5432/book?ssl=true&sslmode=require
```

### Create the managed MongoDB Database

We need to create a MongoDB so the Book Fallback microservice can store failures.
Create a database in the region where it's available:

```shell
az cosmosdb create \
  --resource-group $RESOURCE_GROUP \
  --locations regionName="$LOCATION" failoverPriority=0 \
  --name $FAILURE_DB \
  --kind MongoDB
```

Create the Failure collection:

````shell
az cosmosdb mongodb database create \
  --resource-group $RESOURCE_GROUP \
  --account-name $FAILURE_DB \
  --name $FAILURE_DB_SCHEMA
````

To get the connection string, execute the following command:

````shell
az cosmosdb keys list \
  --resource-group $RESOURCE_GROUP \
  --name $FAILURE_DB \
  --type connection-strings \
  --query "connectionStrings[?description=='Primary MongoDB Connection String'].connectionString"
````

Take the _Primary MongoDB Connection String_ and add it to the Book Fallback `application.properties` file with the `prod` profile:

```shell
%prod.quarkus.mongodb.connection-string=mongodb://failure-db:lYOwEResTGLsSR3jyBdi0OMWJtgWePdPvIBB9z99h0n9fxcijuhOUyd8HqeL9Myj2cbDffxmONnW2wUF2yWtIA==@failure-db.mongo.cosmos.azure.com:10255/?ssl=true&replicaSet=globaldb&retrywrites=false&maxIdleTimeMS=120000&appName=@failure-db@
```

### Create the Managed Event Hub

The Book microservice communicates with the Book Fail microservice through Kafka.
We need to create an Azure event hub for that.

```shell
az eventhubs namespace create \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --name $EVENTHUB_NAMESPACE
```

```shell
az eventhubs eventhub create \
  --resource-group $RESOURCE_GROUP \
  --name $EVENTHUB_TOPIC \
  --namespace-name $EVENTHUB_NAMESPACE
```

Get the connection string of the event hub namespace.

```shell
az eventhubs namespace authorization-rule keys list \
  --resource-group $RESOURCE_GROUP \
  --namespace-name $EVENTHUB_NAMESPACE \
  --name RootManageSharedAccessKey \
  --query primaryConnectionString
```

Add this connection string to the Book and Failure microservice `application.properties` files with the `prod` profile:

```shell
%prod.kafka.bootstrap.servers=bookstore-event.servicebus.windows.net:9093
%prod.kafka.security.protocol=SASL_SSL
%prod.kafka.sasl.mechanism=PLAIN
%prod.kafka.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required \
	username="$ConnectionString" \
	password="Endpoint=sb://bookstore-event.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=OtG0SZdh8DexxxxSohW+mV7oVbovjtMfFbA3fc=";
```

### Build, Push and Deploy the microservices

Nearly all the `prod` properties have been set, but there are still a few to add.
Start by building the Number microservices with:

```shell
az containerapp create \
  --resource-group $RESOURCE_GROUP \
  --image agoncal/number:1.0.0-SNAPSHOT \
  --name $NUMBER_APP \
  --environment $CONTAINERAPPS_ENVIRONMENT \
  --ingress external \
  --target-port 8701 \
  --query configuration.ingress.fqdn
```

This command returns the fully qualified name of the endpoint (eg. `number-app.icysky-c425dedf.eastus2.azurecontainerapps.io`).
That means you can now access the microservice with `curl https://number-app.icysky-c425dedf.eastus2.azurecontainerapps.io/api/numbers`
Take this fully qualified name and add it to the `application.properties` of Book:

```shell
%prod.org.agoncal.talk.quarkus.book.NumberProxy/mp-rest/url=https://number-app.redsky-874c6570.eastus2.azurecontainerapps.io
```

Now you can build both the Book and the Failure microservices:

```shell
book$ mvn clean package -Dmaven.test.skip=true -Dquarkus.container-image.build=true
book-fallback$ mvn clean package -Dmaven.test.skip=true -Dquarkus.container-image.build=true
```

Then, push the Docker images with:

```shell
$ docker push agoncal/book-fallback:1.0.0-SNAPSHOT
$ docker push agoncal/book:1.0.0-SNAPSHOT
```

Deploy the Book microservice:

```shell
az containerapp create \
  --resource-group $RESOURCE_GROUP \
  --image agoncal/book:1.0.0-SNAPSHOT \
  --name $BOOK_APP \
  --environment $CONTAINERAPPS_ENVIRONMENT \
  --ingress external \
  --target-port 8702 \
  --query configuration.ingress.fqdn
```

```shell
az containerapp create \
  --resource-group $RESOURCE_GROUP \
  --image agoncal/book-fallback:1.0.0-SNAPSHOT \
  --name $FAILURE_APP \
  --environment $CONTAINERAPPS_ENVIRONMENT \
  --ingress external \
  --target-port 8703 \
  --query configuration.ingress.fqdn
```

### Access the application

With the URLs that you got from the output, these are the curl commands you can execute:

```shell
curl https://number-app.icysky-c425dedf.eastus2.azurecontainerapps.io/api/numbers -v
curl https://book-app.icysky-c425dedf.eastus2.azurecontainerapps.io/api/books -v | jq
curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" https://book-app.icysky-c425dedf.eastus2.azurecontainerapps.io/api/books -v | jq
curl https://failure-app.icysky-c425dedf.eastus2.azurecontainerapps.io/api/failures -v | jq
```

Now let's stop the Number microservice to see if the fallback works.
First, get the name of the revision:

````shell
az containerapp revision list \
  --resource-group $RESOURCE_GROUP \
  --name $NUMBER_APP \
  --out table
````

Stop the revision:

```shell
az containerapp revision deactivate \
  --resource-group $RESOURCE_GROUP \
  --name number-app--f2yca2h  \
  --app $NUMBER_APP
```

Create a book and check that there is a failure:

```shell
curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" https://book-app.icysky-c425dedf.eastus2.azurecontainerapps.io/api/books -v | jq
curl https://failure-app.icysky-c425dedf.eastus2.azurecontainerapps.io/api/failures -v | jq
```

### Redeploy a microservice

If you need to restart the container, first check the name of the revision:

```shell
az containerapp revision list \
  --resource-group $RESOURCE_GROUP \
  --name $BOOK_APP \
  --out table
```

```shell
az containerapp revision restart \
  --resource-group $RESOURCE_GROUP \
  --name book-app--bl3tzvc \
  --app $BOOK_APP
```

If you want to change the revision to a new Docker image, update the container (in this case using a different tag `azure`):

```shell
az containerapp update \
  --resource-group $RESOURCE_GROUP \
  --name $BOOK_APP \
  --image agoncal/book:azure
```

### Check the microservices in the Container App

```shell
az containerapp list --resource-group $RESOURCE_GROUP --output table 
az containerapp show --resource-group $RESOURCE_GROUP --output table --name $NUMBER_APP 
```

### Checking Logs

To access the logs of each microservice, you can write the following queries:

````shell
az monitor log-analytics query \
--workspace $LOG_ANALYTICS_WORKSPACE_CLIENT_ID \
--analytics-query "ContainerAppConsoleLogs_CL | where ContainerAppName_s == 'number-app' | project ContainerAppName_s, Log_s, TimeGenerated " \
--out table
````

````shell
az monitor log-analytics query \
--workspace $LOG_ANALYTICS_WORKSPACE_CLIENT_ID \
--analytics-query "ContainerAppConsoleLogs_CL | where ContainerAppName_s == 'book-app' | project ContainerAppName_s, Log_s, TimeGenerated | take 30" \
--out table
````

````shell
az monitor log-analytics query \
--workspace $LOG_ANALYTICS_WORKSPACE_CLIENT_ID \
--analytics-query "ContainerAppConsoleLogs_CL | where ContainerAppName_s == 'failure-app' | project ContainerAppName_s, Log_s, TimeGenerated | take 30" \
--out table
````
### Troubleshooting

If you have the following error message you can either:

* make sure you've created a Firewall rule so that the Book microservice can access the Postgres Database
* make sure you've added `ssl=true&sslmode=require` in the Postgres connection string

```shell
FATAL: no pg_hba.conf entry for host "20.96.49.141",
```

Passing environment variables does not work because of characters:

```shell
--environment-variables QUARKUS_DATASOURCE_USERNAME=userbookstore@bookstore-db-antoniomanug.postgres.database.azure.com,QUARKUS_DATASOURCE_PASSWORD=p#ssw0rd-12046,QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://bookstore-db-antoniomanug.postgres.database.azure.com:5432/book?ssl=true&sslmode=require
```


```shell
error:1404B42E:SSL routines:ST_CONNECT:tlsv1 alert protocol version
```

