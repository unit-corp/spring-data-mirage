# Changelog

## Release lines

| Line | Group | Artifacts | Java package | Spring Boot | Spring Framework | Minimum Java | Availability |
|------|-------|-----------|--------------|-------------|------------------|--------------|--------------|
| `0.14.2.RELEASE` (original, last upstream release) | `jp.xet.springframework.data.mirage` | `spring-data-mirage` | `jp.xet.springframework.data.mirage` | n/a (plain Spring Framework) | 5.x | 8 | JCenter (end of life) |
| `2.0.1.RELEASE` (UNIT internal fork) | `jp.xet.springframework.data.mirage` | `spring-data-mirage-customize` | `jp.xet.springframework.data.mirage` | 2.x | 5.x | 8 | Internal (UNIT private repository) |
| `3.0.0.RELEASE` (UNIT internal fork) | `jp.xet.springframework.data.mirage` | `spring-data-mirage-customize` | `jp.xet.springframework.data.mirage` | 3.x | 6.x | 17 | Internal (UNIT private repository) |
| `4.0.x` (legacy, UNIT-maintained) | `vn.com.unit.springframework.data.mirage` | `spring-data-mirage-legacy` | `jp.xet.springframework.data.mirage` | 4.x | 7.x | 17 | Maven Central |
| `4.1+` (current, UNIT active development) | `vn.com.unit.springframework.data.mirage` | `spring-data-mirage` | `vn.com.unit.springframework.data.mirage` | 4.x | 7.x | 17 | Maven Central |

## Unreleased (maintenance/legacy)

This branch is the UNIT-maintained **legacy** line for applications that still run the original `jp.xet.springframework.data.mirage` package. It receives maintenance fixes only; no new features are planned.

### Changed

- Maven group changed from `jp.xet.springframework.data.mirage` to **`vn.com.unit.springframework.data.mirage`**; the artifact is now **`spring-data-mirage-legacy`**.
- The package of this library is unchanged (`jp.xet.springframework.data.mirage`), but the Spar Wings package changed from `jp.xet.sparwings` to **`vn.com.unit.sparwings`** (groupId `jp.xet.spar-wings` to `vn.com.unit.spar-wings`). Types such as `Chunk`, `ChunkRequest` and `BatchReadableRepository` are part of the repository API, so consumer imports of them must be updated.
- Dependencies now use the legacy Mirage-SQL artifacts (`vn.com.unit.miragesql:miragesql-legacy`, `miragesql-integration-legacy`) and `vn.com.unit.sparwings` for the chunk support.
- Minimum Java version set to **17** (was 21); the library is built against Spring Boot 4.
- Project metadata (organization, SCM, developers, license) and Maven Central publishing setup (sources, javadoc, GPG signing, central publishing plugin) added to the pom; the internal `distributionManagement` was removed.
- `LICENSE` and `NOTICE` live in the repository root and are shipped in `META-INF` of the jar.

### Migration

- From the original: change the dependency groupId to `vn.com.unit.springframework.data.mirage` and add `-legacy` to the artifactId; switch the Spar Wings and Mirage-SQL dependencies to their `vn.com.unit` coordinates; replace `jp.xet.sparwings` with `vn.com.unit.sparwings` in imports. Imports and configuration of this library itself stay as they are.
- Recommended: move to the current line (new package, artifactId without the `-legacy` suffix).
- From legacy (4.0.x) to current (4.1+): change the artifactId to `spring-data-mirage`, drop `-legacy` from the Mirage-SQL artifacts, and replace `jp.xet.springframework.data.mirage` with `vn.com.unit.springframework.data.mirage` and `com.miragesql.miragesql` with `vn.com.unit.miragesql.miragesql` in sources and configuration. See the README for details.

## 4.0.1

- Published as the legacy line: groupId `vn.com.unit.springframework.data.mirage`, artifactId `spring-data-mirage-legacy` (formerly `spring-data-mirage-customize`, then `spring-data-mirage`).
- Spar Wings moved from `jp.xet.sparwings` to `vn.com.unit.sparwings` (groupId `vn.com.unit.spar-wings`); consumer imports of `Chunk`, `ChunkRequest`, `BatchReadableRepository` and similar types must be updated.
- Mirage-SQL dependency is now `vn.com.unit.miragesql:miragesql-legacy` / `miragesql-integration-legacy`.
- Minimum Java version is 17.
- Built against Spring Boot 4.1.2 / Spring Framework 7 / Spring Data Commons 4.1.
- Removed support for `ListenableFuture` repository return types (removed in Spring Framework 7). Use `CompletableFuture` instead.
