# Spring Data Mirage

[![Maven Central](https://img.shields.io/maven-central/v/vn.com.unit.springframework.data.mirage/spring-data-mirage)](https://central.sonatype.com/artifact/vn.com.unit.springframework.data.mirage/spring-data-mirage)

The primary goal of the [Spring Data](http://www.springsource.org/spring-data) project is to make it easier to build
Spring-powered applications that use data access technologies. This module deals with enhanced support for
[Mirage SQL](https://github.com/mirage-sql/mirage) based data access layers.

This is a fork of [spring-data-mirage](https://github.com/dai0304/spring-data-mirage) maintained by UNIT Technology
Corporation since 2025.


## Features

This project defines a `MirageRepository` base interface:

```java
public interface MirageRepository<E, ID extends Serializable> extends PagingAndSortingRepository<E, ID> {
  T findOne(ID id);
  List<T> findAll();
  boolean exists(ID id);
  long count();
  <S extends E>S save(S entity);
  // ...
}
```


## Requirements

* Java 17+
* Spring Boot 4.1.x (Spring Framework 7, Spring Data Commons 4.1.x)
* Mirage SQL 4.1.1 (`vn.com.unit.miragesql`)


## Changes

See [CHANGELOG.md](CHANGELOG.md) for the release lines and all release notes. Release notes of this line:

### 4.1.1

* Current line: groupId `vn.com.unit.springframework.data.mirage`, artifactId `spring-data-mirage`, Java package `vn.com.unit.springframework.data.mirage`.
* Mirage-SQL types now come from the package `vn.com.unit.miragesql.miragesql`; Spar Wings from `vn.com.unit.sparwings`.
* Built against Spring Boot 4.1.2 / Spring Framework 7 / Spring Data Commons 4.1; Guava 33.7.2-jre to fix known CVEs.
* Minimum Java version is 17.
* Maven Central publishing setup (sources, javadoc, GPG signing) and Apache 2.0 `LICENSE` / `NOTICE` added.

To move from the legacy line (4.0.x, `spring-data-mirage-legacy`), see [Migration](#migration).


## Quick Start

### dependency

Add the jar to your maven project :

```xml
<dependency>
  <groupId>vn.com.unit.springframework.data.mirage</groupId>
  <artifactId>spring-data-mirage</artifactId>
  <version>4.1.1</version>
</dependency>
```

Gradle:

```groovy
implementation 'vn.com.unit.springframework.data.mirage:spring-data-mirage:4.1.1'
```

> The Java package of this library is `vn.com.unit.springframework.data.mirage`. Mirage SQL types live in
> `vn.com.unit.miragesql.miragesql` and are pulled in transitively (`vn.com.unit.miragesql:miragesql` and
> `miragesql-integration` 4.1.1); the chunk support comes from `vn.com.unit.sparwings`.

### Spring beans configurations

Configure your infrastructure:

```xml
<bean id="dataSource" ...>
  <!-- ... -->
</bean>

<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
  <property name="dataSource" ref="dataSource" />
</bean>

<tx:annotation-driven transaction-manager="transactionManager" proxy-target-class="false" />

<bean id="connectionProvider" class="vn.com.unit.miragesql.miragesql.integration.spring.SpringConnectionProvider">
  <property name="transactionManager" ref="transactionManager" />
</bean>

<bean id="dialect" class="vn.com.unit.miragesql.miragesql.dialect.MySQLDialect" />
<bean id="railsLikeNameConverter" class="vn.com.unit.miragesql.miragesql.naming.RailsLikeNameConverter" />

<bean id="sqlManager" class="vn.com.unit.miragesql.miragesql.SqlManagerImpl" depends-on="fieldPropertyExtractorInitializer">
  <property name="connectionProvider" ref="connectionProvider" />
  <property name="dialect" ref="dialect" />
  <property name="nameConverter" ref="railsLikeNameConverter" />
  <property name="beanDescFactory">
    <bean class="vn.com.unit.miragesql.miragesql.bean.BeanDescFactory">
      <property name="propertyExtractor">
        <bean class="vn.com.unit.miragesql.miragesql.bean.FieldPropertyExtractor" />
      </property>
    </bean>
  </property>
  <!-- ... -->
</bean>

<mirage:repositories base-package="com.example.product.repository" sql-manager-ref="sqlManager" />
```

### Entity classes

Create an mirage entity:

```java
@Table(name = "users")
public class User {

  @Id
  @PrimaryKey(generationType = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column(name = "first_name")
  private String firstname;

  @Column(name = "last_name")
  private String lastname;

  // Getters and setters
}
```

### Repository interfaces

Create a repository interface in `com.example.product.repository`:

```java
public interface UserRepository extends MirageRepository<User, Long> {

  List<User> findByFirstname(@Param("first_name") String firstName);

  List<User> findByComplexCondition(
    @Param("complex_param1") String cp1, @Param("complex_param2") int cp2);

  // another query methods...
}
```

### SQL files

Write SQL file `UserRepository.sql` (that's called 'base-select-SQL') and place on the same directory
with `UserRepository.class`:

```sql
SELECT *
FROM users

/*BEGIN*/
WHERE
	/*IF id != null*/
	user_id = /*id*/1
	/*END*/

	/*IF ids != null*/
	AND user_id IN /*ids*/(1, 2, 3)
	/*END*/

	/*IF first_name != null*/
	AND first_name = /*first_name*/'miyamoto'
	/*END*/
/*END*/

/*IF orders != null*/
ORDER BY /*$orders*/user_id
/*END*/

/*BEGIN*/
LIMIT
	/*IF offset != null*/
	/*offset*/0,
	/*END*/

	/*IF size != null*/
	/*size*/10
	/*END*/
/*END*/
```

This base-select-SQL must support "id", "ids", "orders", "offset" and "size" parameters.  These parameters are used
by `findOne()`, `findAll(Iterable<ID>)`, `findAll(Pageable)` and the like.

And you can place another 2-way-sql for specific query method (that's called 'method-specific-2-way-sql')
like this: `UserRepository_findByComplexCondition.sql`

```sql
SELECT U.*
FROM users U
	JOIN blahblah B ON U.username = B.username
WHERE
	B.complex_param1 LIKE /*complex_param1*/'%foobar%'
	OR
	B.complex_param2 > /*complex_param2*/10
```

If the all additional methods are supported by method-specific-2-way-sql or you don't declare additional query methods,
you don't need to create base-select-SQL file.

### Clients

Write a test client :

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(...)
public class UserRepositoryTest {

  @Autowired
  UserRepository repo;

  @Test
  @TransactionConfiguration
  @Transactional
  public void sampleTestCase() {
    User user = repo.findOne(1L); // SELECT * FROM users WHERE user_id = 1
    assertThat("user", user, is(notNullValue()));
    List<User> users = repo.findAll(); // SELECT * FROM users
    assertThat("users", users, is(notNullValue()));
    assertThat("usersSize", users.size() > 0, is(true));
    
    List<User> complex = repo.findByComplexCondition("xy%z", 2);
    // SELECT U.* FROM users U JOIN blahblah B ON U.username = B.username
    // WHERE B.complex_param1 LIKE 'xy%z' OR B.complex_param2 > 2
  }
}
```


## Miscellaneous things

### Modifying query

You must mark modifying (insert, update and delete) query methods by `@Modifying` annotation:

`FooBarRepository.java`
```java
public interface FooBarRepository  extends MirageRepository<FooBar, Long> {

	@Modifying
	void updateFooBar(@Param("foo") String foo, @Param("bar") String bar);

	// ...
}
```

`FooBarRepository_updateFooBar.sql`
```
UPDATE ...
```

### Static parameters

If you want to pass parameters to 2-way-sql statically, you can use `@StaticParam` annotation like this:

```java
@StaticParam(key = "id", value = "foo")
User findFoo();
```

You can use multiple `@StaticParam` annotations by using `@StaticParams` annotation like this:

```java
@StaticParams({
	@StaticParam(key = "foo", value = "foovalue"),
	@StaticParam(key = "bar", value = "barvalue")
})
List<User> findXxx();
```



## Building from source

* JDK 17 or later, Maven 3.9+
* `mvn clean install`
* Publishing to Maven Central: `mvn clean deploy -Pcentral-publishing -DskipTests` (needs a GPG key and a Central
  token in `~/.m2/settings.xml`; never commit them)


## Migration

| | Original | Legacy (4.0.x) | Current (4.1+, this line) |
|---|---|---|---|
| groupId | `jp.xet.springframework.data.mirage` | `vn.com.unit.springframework.data.mirage` | `vn.com.unit.springframework.data.mirage` |
| artifactId | `spring-data-mirage` | `spring-data-mirage-legacy` | `spring-data-mirage` |
| Library package | `jp.xet.springframework.data.mirage` | `jp.xet.springframework.data.mirage` | `vn.com.unit.springframework.data.mirage` |
| Spar Wings package | `jp.xet.sparwings` | `vn.com.unit.sparwings` | `vn.com.unit.sparwings` |
| Mirage-SQL package | `com.miragesql.miragesql` | `com.miragesql.miragesql` | `vn.com.unit.miragesql.miragesql` |

### From the legacy line (4.0.x) to this line (4.1+)

Both lines are built against Spring Boot 4.1 / Java 17 and the public API is the same. What changes is coordinates and
packages:

1. Change the dependency artifactId from `spring-data-mirage-legacy` to `spring-data-mirage` (groupId stays
   `vn.com.unit.springframework.data.mirage`).
2. If you declare Mirage-SQL yourself, drop the `-legacy` suffix from `miragesql-legacy` and
   `miragesql-integration-legacy`.
3. Replace the packages in your sources and Spring configuration (XML and Java config), for example:

   ```
   jp.xet.springframework.data.mirage   ->  vn.com.unit.springframework.data.mirage
   com.miragesql.miragesql              ->  vn.com.unit.miragesql.miragesql
   ```

   This includes class names in bean definitions such as `SpringConnectionProvider`, `SqlManagerImpl`,
   `MySQLDialect` and `RailsLikeNameConverter`.
4. Keep SQL files next to their repository interfaces: when you move the interfaces to a new package, move the
   `.sql` files with them.
5. `<mirage:repositories>` and its namespace URI (`http://www.springframework.org/schema/data/mirage`) stay as they
   are; only `base-package` and class names change.

### From the original (`jp.xet`) project

Change the dependency to `vn.com.unit.springframework.data.mirage:spring-data-mirage`, then apply the package
replacements in the table above (library, Spar Wings and Mirage-SQL packages).


## Links

* Fork repository: <https://github.com/unit-corp/spring-data-mirage>
* Upstream project: <https://github.com/dai0304/spring-data-mirage>
* [CHANGELOG.md](CHANGELOG.md)


## License

Apache License 2.0, see [LICENSE](LICENSE) and [NOTICE](NOTICE).
