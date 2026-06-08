# SyncVault

## demo: 
you can find the demo in the project root: pow_sync

the files should be uploaded to (src-> target -> watched-folder)

## tldr:
a distributed file synchronization prototype that explores how modern storage systems avoid storing the same content multiple times.

instead of treating files as single blobs, files are split into chunks, each chunk is hashed using SHA-256 and chunks are then  stored in object storage using their content hash as the identifier. essentially meaning uploading the same file twice does not store the same data twice.
a lightweight sync agent monitors a local folder, automatically uploads new files, and the backend publishes synchronization events through Redis after processing is complete.

## concept:
<img width="1037" height="537" alt="Screenshot 2026-06-08 102009" src="https://github.com/user-attachments/assets/f264e8a0-2eab-47da-bd96-b14650326a80" />

## implementation
<img width="701" height="717" alt="Screenshot 2026-06-08 103837" src="https://github.com/user-attachments/assets/f2d6bb5f-85ae-4d6e-a9f3-bdee79dff96f" />

A file enters the system:

```text
jimjam.pdf
```

it gets split into chunks:

```text
Chunk S
Chunk H
Chunk R
```

each chunk receives a SHA-256 hash:
```text
S -> 7af3...
H -> 9c21...
R -> e18d...
```

chunks are stored in MinIO using the hash itself as the storage key.
when the same content appears again:
```text
chunk S already exists
chunk H already exists
chunk R already exists
```

the upload is skipped and the existing content is reused.
this single idea ended up driving most of the design decisions in the project :)

## how ?!
backend is responsible for metadata, version tracking, chunk analysis and storage coordination.
PostgreSQL stores metadata about files and versions.
MinIO stores the actual chunk contents.
Redis is used to publish synchronization events after uploads complete.
separate sync agent monitors a local folder using Java's WatchService API and automatically uploads files to the backend when changes are detected.
overall flow looks roughly like this:

```text
file created
        |
sync agent detects change
        |
backend upload api
        |
chunking
        |
SHA-256 hashing
        |
deduplication check
        |
MinIO storage
        |
Redis event published
```

## implementations at hand by hand :)
file metadata management.
version tracking.
chunk-based file processing.
SHA-256 content hashing.
content-addressable storage.
chunk-level deduplication.
object storage through MinIO.
folder monitoring through a sync agent.
redis event publishing.
dockerized infrastructure for local development.

## Example
Uploading a new file:

```json
{
  "uploadedChunks": 1,
  "reusedChunks": 0,
  "deduplicationRatio": "0.00%"
}
```

Uploading the exact same file again:
```json
{
  "uploadedChunks": 0,
  "reusedChunks": 1,
  "deduplicationRatio": "100.00%"
}
```

The second upload succeeds without storing duplicate content.

## Tech Stack:
Java 21, Spring Boot, PostgreSQL, Redis, MinIO, Docker, Maven, WatchService

## Running Locally
start infra: 

```bash
cd infrastructure
docker compose up -d
```

start backend:
```bash
./mvnw spring-boot:run
```

start the sync agent:
```bash
java -jar sync-agent.jar
```

drop a file into:
```text
watched-folder/
```

and the upload pipeline should begin automatically.

## further to build on:
if you are reading this, highly likely the project peaked some interest, while i would love to get back on this project some-time in future you could try working out the following ideas, fork and push some changes ! 
OPEN TO COLLAB & work :)

Conflict detection between concurrent updates.
Optimistic concurrency control.

## side note:
currently empty files generate zero chunks and therefore no uploads. deliberately kept that behaviour simple for the prototype, though another valid design would be storing a canonical empty-file hash.
