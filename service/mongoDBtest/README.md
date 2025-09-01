# mongoDBtest

This module demonstrates basic MongoDB usage with Spring Data MongoDB.

## Run (Application)

```bash
mvn -pl service/mongoDBtest -am spring-boot:run
```

Remote profile:

```bash
set MONGODB_URI=mongodb://user:pass@host:27017/dbname
mvn -pl service/mongoDBtest -am spring-boot:run -Dspring-boot.run.profiles=remote
```

## Run (Tests with Testcontainers)

```bash
mvn -pl service/mongoDBtest -am -DskipTests=false test
```

## REST Endpoints

- POST /api/demo-users
- GET /api/demo-users/{id}
- GET /api/demo-users/search?name=...
- GET /api/demo-users/searchByAge?min=..&max=..&page=..&size=..
- PATCH /api/demo-users/{id}
- DELETE /api/demo-users/{id}
- GET /api/demo-users/analytics/top-tags?limit=..


