# Spring Data Mirage SQL

This project is a fork of [spring-data-mirage](https://github.com/dai0304/spring-data-mirage).
Modifications have been made by UNIT Technology Corporation in 2025.

The primary goal of the [Spring Data](http://www.springsource.org/spring-data) project is to make it easier to build
Spring-powered applications that use data access technologies. This module deals with enhanced support for
[Mirage SQL](https://github.com/unit-corp/mirage) based data access layers.


## Features

This project defines a `MirageRepository` base interface:

```java
public interface MirageRepository<E, ID extends Serializable> extends ScannableRepository<E, ID>,
		BatchReadableRepository<E, ID>, BatchWritableRepository<E, ID>, LockableCrudRepository<E, ID>,
		ChunkableRepository<E, ID>, PageableRepository<E, ID>, TruncatableRepository<E, ID> {

  E findOne(ID id);
  Iterable<E> findAll();
  boolean exists(ID id);
  long count();
  <S extends E> S create(S entity);
  <S extends E> S update(S entity);
  void delete(ID id);
  // ...
}
```


## Requirements

* Java 17+
* Spring Data Commons 3.x.x
* Mirage 3.x.x


## Quick Start

### Dependency

Add the jar to your maven project :

```xml
<dependency>
  <groupId>vn.com.unit.springframework.data.mirage</groupId>
  <artifactId>spring-data-mirage</artifactId>
  <version>3.x.x</version>
</dependency>
```

### Spring beans configurations

XML Configure your infrastructure:

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

<mirage:repositories base-package="vn.com.unit.example.product.repository" sql-manager-ref="sqlManager" />
```


Java Configure your infrastructure:

```java
    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Bean
    public SqlManager sqlManagerService() {

        // Bridge java.util.logging used by mirage
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        SqlManager sqlManager = new SqlManagerImpl();
        sqlManager.setConnectionProvider(connectionProvider());
        sqlManager.setDialect(new MySQLDialect());
        sqlManager.setBeanDescFactory(beanDescFactory());
        sqlManager.setNameConverter(new RailsLikeNameConverter());

        return sqlManager;
    }

    @Bean
    public ConnectionProvider connectionProvider() {
        SpringConnectionProvider springConnectionProvider = new SpringConnectionProvider();
        springConnectionProvider.setTransactionManager(transactionManager);
        return springConnectionProvider;
    }

    @Bean
    public BeanDescFactory beanDescFactory() {
        BeanDescFactory beanDescFactory = new BeanDescFactory();
        beanDescFactory.setPropertyExtractor(new FieldPropertyExtractor());
        return beanDescFactory;
    }
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

Create a repository interface in `vn.com.unit.example.product.repository`:

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

