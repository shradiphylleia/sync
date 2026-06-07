# Learning Log

- `mkdir -p`: `-p` creates parent folders if missing and does not complain if the folder already exists.
- Brace expansion does not like spaces: `sync/{backend,sync-agent}` works, `sync/{backend, sync-agent}` breaks.
- Paths are relative to where I am. If I am already inside `sync`, then `mkdir sync/...` creates another nested `sync`.
- `ssh -T git@github.com-personal` checks whether my SSH key can log in to GitHub.
- `jenv` can keep Java 8 as default but use Java 21 only in this project.
- `.java-version` tells `jenv` what Java this project wants.
- `JAVA_HOME` matters because Maven checks it.



- Docker container = temporary running box for a service.
- If container data is only inside the container, deleting the container can reset the data.
- Docker volumes keep data outside containers so Postgres/MinIO data can survive container recreation.


1. volumes: docker
when i run psql in docker, the db stores its files inside the container by default (upon docker compose up)
if we were to later remove the cntr via docker compose down / delte/recreate it then anything stored also deletes.
db tables/data gone. colume keeps this data outside the container, in docker managed storage.

Container can be temporary.
Database data should persist.

example:
``` bash
volumes:
  - postgres-data:/var/lib/postgresql/data
```

2. docker compose up -d : start all services defined in docker-compose.yml
Run them in bg
