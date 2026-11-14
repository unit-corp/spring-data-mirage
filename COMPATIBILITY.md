# Compatibility

## Verified artifact families

| Variant | Maven coordinates | Java package | Mirage-SQL artifacts |
|---|---|---|---|
| Standard artifact | `vn.com.unit.springframework.data.mirage:spring-data-mirage` | `vn.com.unit.springframework.data.mirage` | `miragesql`, `miragesql-integration` |
| Legacy artifact | `vn.com.unit.springframework.data.mirage:spring-data-mirage-legacy` | `jp.xet.springframework.data.mirage` | `miragesql-legacy`, `miragesql-integration-legacy` |

The Legacy artifact is a separate compatibility family, not merely an older version of the standard artifact. Do not mix standard and Legacy Spring Data Mirage or Mirage-SQL artifacts in one application.

## Release compatibility

| Release line | Lifecycle | Minimum Java | Spring baseline | Mirage-SQL baseline | Evidence |
|---|---|---:|---|---|---|
| `5.x` standard artifacts | Current line | 17 | Spring Boot 4, Spring Framework 7, Spring Data Commons managed by Spring Boot | Standard `5.x` artifacts | Tag [`v5.0.0`](https://github.com/unit-corp/spring-data-mirage/tree/v5.0.0) |
| `4.x` Legacy artifacts | Maintenance-only | 17 | Spring Boot 4, Spring Framework 7, Spring Data Commons 4.1 | Legacy `4.x` artifacts | Tag [`legacy-v4.0.1`](https://github.com/unit-corp/spring-data-mirage/tree/legacy-v4.0.1) |
| `3.x` and earlier | Internal support only | Release-specific | Release-specific | Release-specific | Historical tags and source |

Spring Boot is used as the dependency-management and test baseline. Applications must still validate their complete Spring dependency graph and runtime configuration.

## Compatibility rules

- Keep Spring Data Mirage and Mirage-SQL on the matching artifact family and supported major line.
- Do not combine `spring-data-mirage-legacy` with standard Mirage-SQL artifacts or `spring-data-mirage` with `-legacy` Mirage-SQL artifacts.
- Keep only one copy of the Spring Data Mirage API packages on the runtime classpath.
- Moving repository interfaces between Java packages also requires moving their colocated SQL resources.
- Verify string-based class references in Spring XML, reflection, serialization metadata, and application configuration during migration.
- A successful H2 test suite does not certify every database, JDBC driver, dialect, or production workload.

## Maintenance model

The project may support one minor line or multiple minor lines within an artifact family according to operational need.

- Standard maintenance branches use `maintenance/{major}.x` or `maintenance/{major}.{minor}.x`.
- Legacy maintenance branches use `maintenance/legacy/{major}.x` or `maintenance/legacy/{major}.{minor}.x`.
- An exact patch branch may be used temporarily as `maintenance/{major}.{minor}.{patch}` or `maintenance/legacy/{major}.{minor}.{patch}`.

Branch existence alone is not a support commitment. See [Security Policy](SECURITY.md) for the public support scope.

## Publication verification

Confirm the exact version on Maven Central before declaring a production dependency. A POM version, changelog entry, planned tag link, or successful local build does not prove publication.
