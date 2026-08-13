# ADR 0005: Sử dụng Keycloak cho authentication

## Status
Accepted

## Context
Cần bảo vệ API endpoints với OAuth2/OIDC và role-based access control.

## Decision
Sử dụng Keycloak làm identity provider. Gateway validates JWT tokens và áp dụng role-based authorization. Keycloak được chọn vì:
- Open source, production-ready
- Hỗ trợ OAuth2/OIDC đầy đủ
- Dễ tích hợp với Spring Security

## Consequences
- **Tích cực:** Centralized auth, chuẩn hóa, bảo mật cao
- **Tiêu cực:** Thêm infrastructure component, cần quản lý realms/clients/users
