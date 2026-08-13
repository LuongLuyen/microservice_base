# ADR 0011: Sử dụng GitHub Actions cho CI/CD

## Status
Accepted

## Context
Cần tự động build, test, và deploy mỗi khi code thay đổi.

## Decision
Sử dụng GitHub Actions với 4 workflows:
- `ci-pr.yml`: build + test mỗi PR
- `ci-main.yml`: build + push Docker images khi merge main
- `cd-deploy.yml`: deploy tới K8s cluster
- `security-scan.yml`: OWASP dependency scan weekly

## Consequences
- **Tích cực:** Tích hợp sẵn với GitHub, không cần external CI/CD
- **Tiêu cực:** Phụ thuộc GitHub, cần secrets để push images/deploy
