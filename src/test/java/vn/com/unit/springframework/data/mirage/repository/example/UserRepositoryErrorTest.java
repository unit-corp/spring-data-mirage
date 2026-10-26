/*
 * Copyright 2011-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vn.com.unit.springframework.data.mirage.repository.example;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import org.junit.jupiter.api.Test;

import vn.com.unit.miragesql.miragesql.SqlManagerImpl;
import vn.com.unit.miragesql.miragesql.bean.BeanDescFactory;
import vn.com.unit.miragesql.miragesql.bean.FieldPropertyExtractor;
import vn.com.unit.miragesql.miragesql.dialect.MySQLDialect;
import vn.com.unit.miragesql.miragesql.integration.spring.SpringConnectionProvider;
import vn.com.unit.miragesql.miragesql.naming.RailsLikeNameConverter;

import vn.com.unit.springframework.data.mirage.repository.TestConfiguration;
import vn.com.unit.springframework.data.mirage.repository.support.MirageRepositoryFactory;

/**
 * Tests that database failures reach callers as standard data-access exceptions.
 */
@SpringJUnitConfig(classes = TestConfiguration.class)
@Transactional
@SuppressWarnings("javadoc")
public class UserRepositoryErrorTest {

	@Autowired
	FullUserRepository users;

	@Autowired
	FullEntityRepository entities;


	@Test
	public void create_withExistingKey_raisesDuplicateKeyAndKeepsData() {
		users.create(new User("foo", "foopass"));

		assertThrows(DuplicateKeyException.class, () -> users.create(new User("foo", "other")));

		assertThat(users.count(), is(1L));
		assertThat(users.findOne("foo").getPassword(), is("foopass"));
	}

	@Test
	public void create_withNullRequiredColumn_raisesDataIntegrityViolation() {
		// password is "VARCHAR(64) NOT NULL"
		assertThrows(DataIntegrityViolationException.class, () -> users.create(new User("foo", null)));

		assertThat(users.count(), is(0L));
	}

	@Test
	public void create_withTooLongValue_raisesDataIntegrityViolation() {
		// password is "VARCHAR(64) NOT NULL", so 65 characters do not fit
		String tooLong = "x".repeat(65);

		assertThrows(DataIntegrityViolationException.class, () -> users.create(new User("foo", tooLong)));

		assertThat(users.count(), is(0L));
	}

	@Test
	public void save_entityWithNullRequiredColumn_raisesDataAccessExceptionAndStoresNothing() {
		// str is "VARCHAR(64) NOT NULL"
		assertThrows(DataAccessException.class, () -> entities.save(new Entity(null)));

		assertThat(entities.count(), is(0L));
	}

	@Test
	public void save_entityWithTooLongValue_raisesDataAccessExceptionAndStoresNothing() {
		// str is "VARCHAR(64) NOT NULL", so 65 characters do not fit
		String tooLong = "x".repeat(65);

		assertThrows(DataAccessException.class, () -> entities.save(new Entity(tooLong)));

		assertThat(entities.count(), is(0L));
	}

	@Test
	public void failuresInReadAndRemovalOperations_areTranslated() {
		// a database that has no tables at all, so every statement fails
		EmbeddedDatabase emptyDb = new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			.setName("empty-" + UUID.randomUUID() + ";MODE=MySQL")
			.build();
		try {
			FullUserRepository broken = new MirageRepositoryFactory(sqlManagerFor(emptyDb))
				.getRepository(FullUserRepository.class);

			// the connection provider requires a Spring-managed transaction on this data source
			new TransactionTemplate(new DataSourceTransactionManager(emptyDb)).executeWithoutResult(status -> {
				assertThrows(BadSqlGrammarException.class, () -> broken.count());
				assertThrows(BadSqlGrammarException.class, () -> broken.exists("x"));
				assertThrows(BadSqlGrammarException.class, () -> broken.findOne("x"));
				assertThrows(BadSqlGrammarException.class, () -> broken.findAll());
				assertThrows(DataAccessException.class, () -> broken.delete(new User("x", "y")));
			});
		} finally {
			emptyDb.shutdown();
		}
	}

	private static SqlManagerImpl sqlManagerFor(DataSource dataSource) {
		SpringConnectionProvider connectionProvider = new SpringConnectionProvider();
		connectionProvider.setDataSource(dataSource);

		BeanDescFactory beanDescFactory = new BeanDescFactory();
		beanDescFactory.setPropertyExtractor(new FieldPropertyExtractor());

		SqlManagerImpl sqlManager = new SqlManagerImpl();
		sqlManager.setConnectionProvider(connectionProvider);
		sqlManager.setDialect(new MySQLDialect());
		sqlManager.setBeanDescFactory(beanDescFactory);
		sqlManager.setNameConverter(new RailsLikeNameConverter());
		return sqlManager;
	}
}
