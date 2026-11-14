# Spring Data Mirage [![Maven Central](https://img.shields.io/maven-central/v/vn.com.unit.springframework.data.mirage/spring-data-mirage)](https://mvnrepository.com/artifact/vn.com.unit.springframework.data.mirage/spring-data-mirage)

> **Lifecycle policy:** `5.x` is the Current standard artifact line for active development. Supported `4.x` Legacy lines are maintenance-only.
>
> A lifecycle designation does not prove that a release is available. Confirm version status and runtime requirements in [Compatibility](COMPATIBILITY.md).

The primary goal of the [Spring Data](https://spring.io/projects/spring-data) project is to make it easier to build Spring-powered applications that use data access technologies. Spring Data Mirage supports [Mirage-SQL](https://github.com/unit-corp/mirage)-based data access layers through Spring Data repository abstractions, including SQL-file-backed queries, paging, chunking, modifying queries, and auditing integration.

This project is a fork of [dai0304/spring-data-mirage](https://github.com/dai0304/spring-data-mirage) maintained by UNIT Technology Corporation since 2025.

The Legacy artifact family is a separate compatibility distribution. It uses the `spring-data-mirage-legacy` artifactId and retains the `jp.xet.springframework.data.mirage` Java package. Its source is maintained on the [`maintenance/legacy`](https://github.com/unit-corp/spring-data-mirage/tree/maintenance/legacy) branch. Legacy and standard artifacts have separate maintenance branches and release lifecycles.

## Artifact families

| Family | Artifact | Java package | Lifecycle |
|---|---|---|---|
| Standard | `vn.com.unit.springframework.data.mirage:spring-data-mirage` | `vn.com.unit.springframework.data.mirage` | Current `5.x` line |
| Legacy | `vn.com.unit.springframework.data.mirage:spring-data-mirage-legacy` | `jp.xet.springframework.data.mirage` | Maintenance-only `4.x` line |

Select a released version based on the application's Java, Spring, Spring Data, and Mirage-SQL baselines. See [Compatibility](COMPATIBILITY.md).

## Using the standard artifact

Define the selected released version once:

```xml
<properties>
    <spring-data-mirage.version>REPLACE_WITH_VERSION</spring-data-mirage.version>
</properties>
```

```xml
<dependency>
    <groupId>vn.com.unit.springframework.data.mirage</groupId>
    <artifactId>spring-data-mirage</artifactId>
    <version>${spring-data-mirage.version}</version>
</dependency>
```

The standard artifact uses the `vn.com.unit.springframework.data.mirage` package and depends on the standard Mirage-SQL artifacts.

## Repository configuration

Enable repository scanning with Java configuration:

```java
@Configuration
@EnableMirageRepositories(
        basePackages = "com.example.product.repository",
        sqlManagerRef = "sqlManager",
        transactionManagerRef = "transactionManager")
public class MirageRepositoryConfiguration {
}
```

XML configuration remains available:

```xml
<mirage:repositories
    base-package="com.example.product.repository"
    sql-manager-ref="sqlManager"
    transaction-manager-ref="transactionManager" />
```

Repository interfaces extend `MirageRepository` or the more specific repository interfaces provided by the project. SQL resources remain colocated with the repository package. A repository-level base SQL file uses the repository name, while method-specific SQL uses the repository and method names.

```text
com/example/product/repository/UserRepository.java
com/example/product/repository/UserRepository.sql
com/example/product/repository/UserRepository_findByStatus.sql
```

Review the source tests for executable examples of CRUD, paging, chunking, modifying queries, optional results, and auditing.

## Building from source

Use the JDK and Maven baselines documented for the target line in [Compatibility](COMPATIBILITY.md):

```bash
mvn clean verify
```

## Documentation

- [Changelog](CHANGELOG.md)
- [Compatibility](COMPATIBILITY.md)
- [Migration index](MIGRATION.md)
- [Security policy](SECURITY.md)
- [Contributing](CONTRIBUTING.md)
- [Release process](RELEASING.md)

## License

Apache License 2.0. See [LICENSE](LICENSE) and [NOTICE](NOTICE).
