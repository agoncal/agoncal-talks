# Custom and Generated Code Side by Side with JHipster

This code was developed to illustrate the purpose of my **Custom and Generated Code Side by Side with JHipster** talk at JHipster Conf in June 2018.

With JHipster you usually bootstrap some code, and change it. This has many advantages but also drawbacks: 

* not always easy to iterate and update your model frequently
* keeping up with JHipster upgrade might be difficult

The idea of the talk (and this code) is to show how you can iterate your model often, upgrade JHipster version frequently, generate code... while keeping your own code side by side with JHipster.

## Generate and compile the

This repository has a `.yo-rc.json` file, a JDL one for the model and the custom code only. The code as it is doesn't compile, doesn't even have a `pom.xml` file. So you have to execute the following commands to make it compile and run:

```
$ jhipster --skip-git --skip-install
$ jhipster import-jdl organisation.jdl
```

Now you can install the dependencies and compile the code

```
$ yarn install
$ mvn install
```

## Execute the application

This application uses a PostgreSQL database. Therefore you need to launch the database before executing the code:

```
$ docker-compose -f src/main/docker/postgresql.yml up -d
```

Execute the back-end and the front-end on separate consoles with:

```
$ ./mvnw
$ yarn start
```

## Custom code side by side

### Repository





## Troubleshooting

If you need to DROP the PostgreSQL schema

```
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;
GRANT ALL ON SCHEMA public TO sponsor;
```
