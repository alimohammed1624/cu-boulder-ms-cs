# The milk problem

An example architecture used for managing product inventory which highlights the use of database transactions.

## History

The milk problem first surfaced while working with a well-known grocery store to track product inventory in real time.
The choice of database was largely driven by a non-trivial performance requirement. The initial solution used an 
_eventually consistent_ database which was available and partition tolerant. Read about
the [CAP theorem](https://en.wikipedia.org/wiki/CAP_theorem)
to learn more about the relationship between consistency, availability, and partition tolerance.

The challenge is that high availability comes at the cost of consistency. High availability databases are eventually
consistent, and thus are notorious for _dirty reads_: allowing uncommitted changes from one transaction to affect a read
in another transaction. As a result, the grocery chain was unable to produce an accurate count of milk on the shelves.

The below exercise introduces the reader to transactions while highlighting the challenges of dirty reads.

## The exercise

Get the tests to pass!

- Remove dirty reads.
- Ensure the correct product quantities.

Look for *todo* items in the codebase to get started.

## Quick start

The below steps walk through the environment setup necessary to run the application in both local and production
environments.

### Install dependencies

1. Install PostgreSQL.

   ```bash
   brew install postgresql
   brew services run postgres
   ```

1. Install Flyway.

   ```bash
   brew install flyway
   ```

1. Create a PostgreSQL database.

   ```bash
   createdb
   ```

### Set up the test environment

1. Create the _milk_test_ database.

   ```bash
   psql -c "create database milk_test;"
   psql -c "create user milk with password 'milk';"
   ```

1. Migrate the database with Flyway.

   ```bash
   FLYWAY_CLEAN_DISABLED=false flyway -user=milk -password=milk -url="jdbc:postgresql://localhost:5432/milk_test" -locations=filesystem:databases/milk clean migrate
   ```

### Run tests

Use Gradle to run tests. You'll see a few failures at first.

```bash
./gradlew build
```

### Set up the development environment

1. Create the _milk_development_ database.

   ```bash
   psql -c "create database milk_development;"
   ```

1. Migrate the database with Flyway.

   ```bash
   FLYWAY_CLEAN_DISABLED=false flyway -user=milk -password=milk -url="jdbc:postgresql://localhost:5432/milk_development" -locations=filesystem:databases/milk clean migrate
   ```

1. Source the `.env` file for local development.

   ```bash
   source .env
   ```

1. Populate development data with a product scenario.

   ```bash
   psql -f applications/products-server/src/test/resources/scenarios/products.sql milk_development
   ```

### Run apps

1.  Use Gradle to run the products server

    ```bash
    ./gradlew applications:products-server:run
    ```

1.  Use Gradle to run the simple client

    ```bash
    ./gradlew applications:simple-client:run
    ```

Hope you enjoy the exercise!

Thanks,

The IC Team

© 2023 by Initial Capacity, Inc. All rights reserved.
