# ADR 0008: Sử dụng HashiCorp Vault cho secrets management

## Status
Accepted

## Context
Secrets (DB passwords, Kafka credentials, encryption keys) không được hardcode trong source code hoặc config files.

## Decision
Sử dụng HashiCorp Vault làm central secret store. Services đọc secrets qua Spring Cloud Vault với token authentication. Trong local dev, Vault chạy trong Docker Compose và secrets dùng default.

## Consequences
- **Tích cực:** Secrets không nằm trong source, dễ rotate, audit được
- **Tiêu cực:** Cần quản lý Vault cluster, thêm operational complexity
