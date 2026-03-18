---
description: Check critical file changes against coding and security standards
event: on_file_save
---

## Conditions

- The saved file matches any of these paths:
  - `**/migration/**` or `**/db/**` (database migrations)
  - `**/security/**` or `**/auth/**` (security-related code)
  - `**/model/**` or `**/entity/**` (domain models / JPA entities)
  - `**/application*.yml` or `**/application*.properties` (configuration)

## Instructions

- For database migration files:
  - Warn if the migration contains `DROP TABLE` or `DROP COLUMN` without a comment explaining why
  - Warn if `CREATE INDEX` is used without `CONCURRENTLY` on a table likely to be large
  - Warn if a `NOT NULL` column is added without a `DEFAULT` value
- For security/auth files:
  - Flag any hardcoded strings that look like credentials, tokens, or API keys
  - Verify password handling uses bcrypt or a strong hashing algorithm, not MD5/SHA-1
  - Flag `@PermitAll` or public endpoint annotations — confirm they are intentional
- For model/entity files:
  - Warn if a field is removed (could break existing queries or API contracts)
  - Warn if a field type is changed (could require data migration)
- For configuration files:
  - Flag any values that look like secrets (passwords, keys, tokens) — these should be environment variables
  - Warn if database connection pool size is changed
- Keep warnings concise — one line per issue, with the file path and line number when possible
