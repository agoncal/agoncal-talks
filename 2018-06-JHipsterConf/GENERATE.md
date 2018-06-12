# Custom and Generated Code Side by Side with JHipster

This code was developped to illustrate the purpose of my **Custom and Generated Code Side by Side with JHipster** talk at JHipster Conf in June 2018.

With JHipster you usually bootstrap some code, and change it. This has many advantages but also drawbacks: 

* not always easy to iterate and update your model frequently
* keeping up with JHipster upgrade might be difficult

The idea of the talk is to show how you can iterate your model often, upgrade JHipster version frequently, generate code... while keeping your own code side by side with JHipster.

## Generate the project and model

This repository has a `.yo-rc.json` file and a JDL one for the model. So you can just pick up these two files and execute :

```
$ jhipster # jhipster --skip-git if you want to skip adding to GIT
$ jhipster import-jdl organisation.jdl
```

## Add your custom code side by side

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
