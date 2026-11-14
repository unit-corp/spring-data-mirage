# Migration Guide

This file is the migration index. Detailed instructions are maintained in versioned guides under `docs/migration/`.

## Supported migration paths

| From | To | Guide |
|---|---|---|
| `4.x` Legacy artifacts | `5.x` standard artifacts | [Migrating from 4.x to 5.x](docs/migration/4-to-5.md) |

## Versioning convention

Each major-version transition has one guide named:

```text
docs/migration/{from-major}-to-{to-major}.md
```

Add a new guide when a new major version introduces a supported migration path. Keep this index short and link to the versioned guide rather than duplicating detailed instructions here.

## Before any major upgrade

1. Identify the exact source and target versions, artifact family, Java packages, Java runtime, Spring baseline, Spring Data baseline, and Mirage-SQL dependencies.
2. Read [Compatibility](COMPATIBILITY.md) and the relevant sections in [Changelog](CHANGELOG.md).
3. Capture the dependency tree and check for mixed Legacy and standard artifacts.
4. Back up configuration and establish an application rollback point.
5. Run repository, SQL-resource, transaction, paging, chunking, auditing, and database integration tests against the target stack.

Do not infer migration steps from a version number alone. Verify the target source, tag, published artifacts, and application-specific integration behavior.
