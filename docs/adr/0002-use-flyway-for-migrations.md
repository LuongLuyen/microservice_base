# ADR 0002: Sử dụng Flyway cho database migration

## Status
Accepted

## Context
Hibernate `ddl-auto: update` tự động thay đổi schema, nhưng:
- Không có version control cho schema changes
- Không thể rollback
- Khó review trong PR
- Nguy hiểm khi deploy production

## Decision
Sử dụng Flyway migration scripts, đặt tại `src/main/resources/db/migration/`. Cấu hình `ddl-auto: validate` để Hibernate chỉ validate chứ không thay đổi schema.

## Consequences
- **Tích cực:** Schema changes được version control, review được, rollback được
- **Tiêu cực:** Cần viết migration script thủ công cho mỗi schema change
