# ADR 0012: Sử dụng ArgoCD cho GitOps

## Status
Accepted

## Context
K8s deployments cần được quản lý declaratively và tự động sync từ Git.

## Decision
Sử dụng ArgoCD để watch Git repo và tự deploy Helm charts tới K8s clusters. Dev environment auto-sync, prod environment manual sync.

## Consequences
- **Tích cực:** Git là single source of truth, audit mọi thay đổi, dễ rollback
- **Tiêu cực:** Cần quản lý ArgoCD instance
