# SyncVault
Distributed file synchronization platform with chunk-level deduplication.

## Components
### Sync Agent
Monitors folders for changes.
Chunks files.
Uploads updates.

### Sync Server
Coordinates synchronization.

### PostgreSQL
Stores metadata.

### MinIO
Stores file chunks.

### Redis
Publishes synchronization events.

## Synchronization Flow
1. File changes.
2. File chunked.
3. SHA-256 hashes generated.
4. Missing chunks uploaded.
5. Metadata persisted.
6. Redis event emitted.
7. Other clients synchronize.
