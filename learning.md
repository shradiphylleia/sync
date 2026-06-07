# Learning Log

`mkdir -p` creates parent folders if they are missing and does not complain if the folder already exists.
Brace expansion does not like spaces:
Works: `sync/{backend,sync-agent}`

Paths are relative to the folder I am currently in.
If I am already inside `sync`, then running `mkdir sync/...` creates another nested `sync`.
`ssh -T git@github.com-personal` checks whether my SSH key can log in to GitHub.

Maven Wrapper:
```bash
./mvnw --version
```

- On Windows/Git Bash, the wrapper may be:

```bash
./mvnw.cmd --version
```

- The Maven Wrapper is useful because it lets the project choose its Maven version.
- This project wrapper uses Maven `3.9.16`.
- Global Maven and project Maven can be different. Prefer the wrapper inside the project.

## Spring Boot Basics

- The main Spring Boot class is under:

```text
backend/src/main/java/com/syncvault/backend/BackendApplication.java
```
- Spring Boot scans components under the package of the main application class.
- Since the app package is `com.syncvault.backend`, controllers should live under that package, for example:

```text
com.syncvault.backend.controller
```

A controller handles HTTP requests.
`@RestController` tells Spring that the class returns response data directly.
`@GetMapping("/api/health")` maps a GET request to a Java method.
A simple health endpoint can return a map:

```java
return Map.of(
    "status", "UP",
    "service", "SyncVault"
);
```

- To run the backend from the `backend` folder:

```bash
./mvnw spring-boot:run
```

Git Bash if needed:
```bash
./mvnw.cmd spring-boot:run
```

## Application Config
- Spring Boot reads config from files like:

```text
src/main/resources/application.properties
src/main/resources/application.yml
```
- This project currently uses `application.yml`.
- `application.yml` contains settings like:
  - server port
  - datasource URL
  - database username/password
  - Redis host/port

- Example datasource URL:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/syncvault
```

## Docker Concepts

Docker container = a temporary running box for a service.
A container can be deleted and recreated.
If data exists only inside the container, deleting the container can delete the data.
Docker volumes keep data outside containers, in Docker-managed storage.
Volumes let Postgres, Redis, and MinIO data survive container recreation.

Example:

```yaml
volumes:
  - postgres-data:/var/lib/postgresql/data
```
`docker compose up -d` starts all services defined in `docker-compose.yml`.
`-d` means detached mode, so containers run in the background.
`docker compose ps` shows the services for the current compose project.
`docker ps` shows currently running containers.

## Project Infrastructure

```text
infrastructure/docker-compose.yml
```

Postgres is the relational database.
Redis is an in-memory datastore/cache.
MinIO is local object storage, similar to S3 for development.

## Ports And Conflicts

- A port can only be owned by one process at a time.
The Docker Postgres container originally tried to expose itself on host port `5432`.
Because local Postgres already used `5432`, the backend connected to the wrong database.
That caused:

```text
FATAL: password authentication failed for user "admin"
```

- The fix was to expose Docker Postgres on host port `5433` while keeping container Postgres on port `5432`:

```yaml
ports:
  - "5433:5432"
```

## Docker Desktop And WSL

- Docker Desktop on Windows uses WSL, which means Windows Subsystem for Linux.
- Docker containers usually run as Linux containers.
- WSL gives Docker Desktop a Linux environment on Windows.
- Docker CLI talks to Docker Desktop.
- Docker Desktop talks to the WSL Linux engine.

Flow:

```text
docker command -> Docker Desktop -> WSL Linux engine -> containers
```

- If Docker says it cannot connect to:

```text
npipe:////./pipe/dockerDesktopLinuxEngine
```

then Docker Desktop or its Linux engine is probably not running.
