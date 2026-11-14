# Changelog

## Release lines

| Line | Group | Artifact | Java package | Spring baseline | Minimum Java | Lifecycle and availability |
|---|---|---|---|---|---:|---|
| `0.14.2.RELEASE` | `jp.xet.springframework.data.mirage` | `spring-data-mirage` | `jp.xet.springframework.data.mirage` | Spring Framework 5 | 8 | Last stable original upstream release. Historical JCenter distribution, end of life |
| `2.0.1.RELEASE` | `jp.xet.springframework.data.mirage` | `spring-data-mirage-customize` | `jp.xet.springframework.data.mirage` | Spring Boot 2, Spring Framework 5 | 8 | UNIT internal fork. Internal support only, UNIT private repository |
| `3.0.0.RELEASE` | `jp.xet.springframework.data.mirage` | `spring-data-mirage-customize` | `jp.xet.springframework.data.mirage` | Spring Boot 3, Spring Framework 6 | 17 | UNIT internal fork. Internal support only, UNIT private repository |
| `4.0.x` | `vn.com.unit.springframework.data.mirage` | `spring-data-mirage-legacy` | `jp.xet.springframework.data.mirage` | Spring Boot 4, Spring Framework 7 | 17 | Legacy artifacts, maintenance-only. [Maven Central](https://mvnrepository.com/artifact/vn.com.unit.springframework.data.mirage/spring-data-mirage-legacy) |
| `5.0.x` | `vn.com.unit.springframework.data.mirage` | `spring-data-mirage` | `vn.com.unit.springframework.data.mirage` | Spring Boot 4, Spring Framework 7, Spring Data Commons managed by Spring Boot | 17 | Standard artifacts, released line. Mirage-SQL `5.x`. [Maven Central](https://mvnrepository.com/artifact/vn.com.unit.springframework.data.mirage/spring-data-mirage) |

## 5.0.0

### Changed

- Uses `vn.com.unit.springframework.data.mirage:spring-data-mirage` and Java package `vn.com.unit.springframework.data.mirage`.
- Uses the matching standard Mirage-SQL `5.x` artifacts: `vn.com.unit.miragesql:miragesql` and `miragesql-integration`.
- Requires Java 17 and uses the Spring Boot 4 dependency-management baseline, including its managed Spring Data Commons version.
- Uses `vn.com.unit.sparwings` for Spar Wings chunk types.
- Includes updated Spring Data repository behavior and tests for CRUD, paging, chunking, modifying queries, optional results, and auditing.
- Fixes `delete(Iterable)` behavior and enables batch-delete verification.
- Includes Maven project metadata, sources, Javadoc, signing configuration, and Central Publishing configuration. Publication must be verified independently before announcing availability.

### Migration

Applications using the Legacy `4.x` artifact must change the artifactId and package imports. See [Migrating from 4.x to 5.x](docs/migration/4-to-5.md).

**Evidence in this repository:** tag [`v5.0.0`](https://github.com/unit-corp/spring-data-mirage/tree/v5.0.0).

## 4.0.1 (Legacy artifact line)

UNIT-maintained Legacy artifact release supporting Spring Boot 4, Spring Framework 7, and Spring Data Commons 4.1.

- Published as `vn.com.unit.springframework.data.mirage:spring-data-mirage-legacy`.
- Retains the `jp.xet.springframework.data.mirage` package.
- Uses `vn.com.unit.miragesql:miragesql-legacy` and `miragesql-integration-legacy`.
- Uses `vn.com.unit.sparwings` for Spar Wings chunk types.
- Requires Java 17.
- Removes `ListenableFuture` repository return-type support. Use `CompletableFuture` instead.

**Evidence in this repository:** tag [`legacy-v4.0.1`](https://github.com/unit-corp/spring-data-mirage/tree/legacy-v4.0.1).
