# Changelog

## Release lines

| Line | Group | Artifacts | Java package | Spring Boot | Spring Framework | Minimum Java | Availability |
|------|-------|-----------|--------------|-------------|------------------|--------------|--------------|
| `0.14.2.RELEASE` (original, last upstream release) | `jp.xet.springframework.data.mirage` | `spring-data-mirage` | `jp.xet.springframework.data.mirage` | n/a (plain Spring Framework) | 5.x | 8 | JCenter (end of life) |
| `2.0.1.RELEASE` (UNIT internal fork) | `jp.xet.springframework.data.mirage` | `spring-data-mirage-customize` | `jp.xet.springframework.data.mirage` | 2.x | 5.x | 8 | Internal (UNIT private repository) |
| `3.0.0.RELEASE` (UNIT internal fork) | `jp.xet.springframework.data.mirage` | `spring-data-mirage-customize` | `jp.xet.springframework.data.mirage` | 3.x | 6.x | 17 | Internal (UNIT private repository) |
| `4.0.x` (legacy, UNIT-maintained) | `vn.com.unit.springframework.data.mirage` | `spring-data-mirage-legacy` | `jp.xet.springframework.data.mirage` | 4.x | 7.x | 17 | Maven Central |
| `4.1+` (current, UNIT active development) | `vn.com.unit.springframework.data.mirage` | `spring-data-mirage` | `vn.com.unit.springframework.data.mirage` | 4.x | 7.x | 17 | Maven Central |

## 4.1.1

Current line, UNIT active development.

### Changed

- Artifact is **`vn.com.unit.springframework.data.mirage:spring-data-mirage`**; the Java package is renamed from `jp.xet.springframework.data.mirage` to **`vn.com.unit.springframework.data.mirage`** (classes, `spring.handlers`, `spring.schemas` and the XSD resources moved accordingly). The XML namespace URI `http://www.springframework.org/schema/data/mirage` is unchanged.
- Mirage-SQL dependencies are now `vn.com.unit.miragesql:miragesql` / `miragesql-integration` 4.1.1 (no `-legacy` suffix), package `vn.com.unit.miragesql.miragesql`; Spar Wings chunk support from `vn.com.unit.sparwings`.
- Upgraded to Spring Boot 4.1.2 (Spring Framework 7, Spring Data Commons 4.1) and Guava 33.7.2-jre to fix known CVEs.
- Guava (33.7.2-jre) is now a compile dependency: `MirageQueryExecutionConverters` uses Guava `ListenableFuture` and it is no longer pulled in transitively by Mirage-SQL.
- The test suite was migrated from `spring-data-mirage-customize` and updated for Spring 4; `delete(Iterable)` was fixed and batch delete tests are enabled.
- Minimum Java version is **17**.
- Project metadata (organization, SCM, developers, license) and Maven Central publishing setup (sources, javadoc, GPG signing, central publishing plugin) are in the pom; `LICENSE` and `NOTICE` live in the repository root and are shipped in `META-INF` of the jar.

### Migration

- From legacy (4.0.x): change the artifactId to `spring-data-mirage` and replace `jp.xet.springframework.data.mirage` with `vn.com.unit.springframework.data.mirage` and `com.miragesql.miragesql` with `vn.com.unit.miragesql.miragesql` in sources and configuration, and drop `-legacy` from the Mirage-SQL artifacts. See the README for details.

## 4.0.1

- Published as the legacy line: groupId `vn.com.unit.springframework.data.mirage`, artifactId `spring-data-mirage-legacy` (formerly `spring-data-mirage-customize`, then `spring-data-mirage`).
- Spar Wings moved from `jp.xet.sparwings` to `vn.com.unit.sparwings` (groupId `vn.com.unit.spar-wings`); consumer imports of `Chunk`, `ChunkRequest`, `BatchReadableRepository` and similar types must be updated.
- Mirage-SQL dependency is now `vn.com.unit.miragesql:miragesql-legacy` / `miragesql-integration-legacy`.
- Minimum Java version is 17.
- Built against Spring Boot 4.1.2 / Spring Framework 7 / Spring Data Commons 4.1.
- Removed support for `ListenableFuture` repository return types (removed in Spring Framework 7). Use `CompletableFuture` instead.
