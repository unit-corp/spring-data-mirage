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
package jp.xet.springframework.data.mirage.repository.example;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import org.junit.jupiter.api.Test;

import jp.xet.springframework.data.mirage.repository.TestConfiguration;

/**
 * Tests for the repository operations that the migrated suite does not exercise:
 * removal, existence checks, batch save, sorted retrieval and read with the row-lock option.
 */
@SpringJUnitConfig(classes = TestConfiguration.class)
@Transactional
@SuppressWarnings("javadoc")
public class EntityRepositoryCrudTest {

	@Autowired
	FullEntityRepository repo;

	@Autowired
	PlatformTransactionManager transactionManager;


	private static List<String> strs(Iterable<Entity> entities) {
		List<String> result = new ArrayList<String>();
		for (Entity e : entities) {
			result.add(e.getStr());
		}
		return result;
	}

	// ---- US1: removal ----

	@Test
	public void delete_byEntity_removesOnlyThatRow() {
		Entity foo = repo.save(new Entity("foo"));
		repo.save(new Entity("bar"));
		repo.save(new Entity("baz"));

		repo.delete(foo);

		assertThat(repo.count(), is(2L));
		assertThat(repo.findOne(foo.getId()), is(nullValue()));
	}

	@Test
	public void delete_byId_removesOnlyThatRow() {
		Entity foo = repo.save(new Entity("foo"));
		Entity bar = repo.save(new Entity("bar"));
		repo.save(new Entity("baz"));

		repo.delete(foo.getId());

		assertThat(repo.count(), is(2L));
		assertThat(repo.findOne(foo.getId()), is(nullValue()));
		assertThat(repo.findOne(bar.getId()), is(notNullValue()));
	}

	@Test
	public void delete_byUnknownId_doesNothing() {
		repo.save(new Entity("foo"));

		repo.delete(Long.valueOf(999999L));

		assertThat(repo.count(), is(1L));
	}

	@Test
	public void delete_byGroup_removesExactlyThoseRows() {
		Entity foo = repo.save(new Entity("foo"));
		Entity bar = repo.save(new Entity("bar"));
		repo.save(new Entity("baz"));

		repo.delete(Arrays.asList(foo, bar));

		assertThat(repo.count(), is(1L));
		assertThat(strs(repo.findAll()), contains("baz"));
	}

	@Test
	public void delete_byEmptyGroup_doesNothing() {
		repo.save(new Entity("foo"));

		repo.delete(Collections.<Entity>emptyList());

		assertThat(repo.count(), is(1L));
	}

	@Test
	public void deleteAll_leavesNothing() {
		repo.save(new Entity("foo"));
		repo.save(new Entity("bar"));

		repo.deleteAll();

		assertThat(repo.count(), is(0L));
		assertThat(repo.findAll().iterator().hasNext(), is(false));
	}

	@Test
	public void delete_nullInput_isRejectedAndKeepsData() {
		Entity foo = repo.save(new Entity("foo"));

		assertThrows(NullPointerException.class, () -> repo.delete((Entity) null));
		assertThrows(NullPointerException.class, () -> repo.delete((Iterable<Entity>) null));
		assertThrows(NullPointerException.class, () -> repo.delete(Arrays.asList(foo, null)));

		assertThat(repo.count(), is(1L));
	}

	// ---- US1: existence ----

	@Test
	public void exists_reportsStoredAndMissingIds() {
		Entity foo = repo.save(new Entity("foo"));

		assertThat(repo.exists(foo.getId()), is(true));
		assertThat(repo.exists(Long.valueOf(999999L)), is(false));
	}

	@Test
	public void exists_withLockOption_reportsStoredAndMissingIds() {
		Entity foo = repo.save(new Entity("foo"));

		assertThat(repo.exists(foo.getId(), true), is(true));
		assertThat(repo.exists(Long.valueOf(999999L), true), is(false));
	}

	@Test
	public void exists_nullId_isRejected() {
		assertThrows(IllegalArgumentException.class, () -> repo.exists((Long) null));
		assertThrows(IllegalArgumentException.class, () -> repo.exists(null, true));
	}

	// ---- US2: batch save ----

	@Test
	public void saveGroup_ofNewEntities_storesAllInInputOrder() {
		Entity foo = new Entity("foo");
		Entity bar = new Entity("bar");
		Entity baz = new Entity("baz");

		Iterable<Entity> saved = repo.save(Arrays.asList(foo, bar, baz));

		assertThat(strs(saved), contains("foo", "bar", "baz"));
		assertThat(repo.count(), is(3L));
	}

	@Test
	public void saveGroup_mixedNewAndExisting_updatesAndInsertsWithoutDuplicates() {
		Entity existing = repo.save(new Entity("old"));
		existing.setStr("changed");

		repo.save(Arrays.asList(existing, new Entity("new1"), new Entity("new2")));

		assertThat(repo.count(), is(3L));
		assertThat(repo.findOne(existing.getId()).getStr(), is("changed"));
	}

	@Test
	public void saveGroup_empty_storesNothing() {
		Iterable<Entity> saved = repo.save(Collections.<Entity>emptyList());

		assertThat(saved.iterator().hasNext(), is(false));
		assertThat(repo.count(), is(0L));
	}

	@Test
	public void saveGroup_null_returnsEmptyResult() {
		Iterable<Entity> saved = repo.save((Iterable<Entity>) null);

		assertThat(saved, is(notNullValue()));
		assertThat(saved.iterator().hasNext(), is(false));
	}

	// ---- US2: sorted retrieval ----

	@Test
	public void findAll_sortedAscending() {
		repo.save(new Entity("foo"));
		repo.save(new Entity("bar"));
		repo.save(new Entity("baz"));

		assertThat(strs(repo.findAll(Sort.by("str"))), contains("bar", "baz", "foo"));
	}

	@Test
	public void findAll_sortedDescending() {
		repo.save(new Entity("foo"));
		repo.save(new Entity("bar"));
		repo.save(new Entity("baz"));

		assertThat(strs(repo.findAll(Sort.by(Direction.DESC, "str"))), contains("foo", "baz", "bar"));
	}

	@Test
	public void findAll_sortedByTwoFieldsWithMixedDirections() {
		Entity first = repo.save(new Entity("dup"));
		Entity second = repo.save(new Entity("dup"));
		repo.save(new Entity("aaa"));

		List<Entity> sorted = repo.findAll(Sort.by(Sort.Order.asc("str"), Sort.Order.desc("id")));

		assertThat(sorted, hasSize(3));
		assertThat(sorted.get(0).getStr(), is("aaa"));
		assertThat(sorted.get(1).getId(), is(second.getId()));
		assertThat(sorted.get(2).getId(), is(first.getId()));
	}

	@Test
	public void findAll_sortedByUnknownField_isTranslatedToDataAccessException() {
		repo.save(new Entity("foo"));

		// Observed: the sort field is put into the SQL as is, so the database rejects it; see findings.md.
		assertThrows(BadSqlGrammarException.class, () -> repo.findAll(Sort.by("noSuchField")));
	}

	@Test
	public void findAll_withEmptyStore_returnsEmpty() {
		assertThat(repo.findAll(Sort.by("str")), is(empty()));
	}

	// ---- US2: read with the row-lock option ----

	@Test
	public void findOne_withLockOption_returnsSameAsPlainRead() {
		Entity foo = repo.save(new Entity("foo"));

		Entity plain = repo.findOne(foo.getId());
		Entity locked = repo.findOne(foo.getId(), true);

		assertThat(locked, is(notNullValue()));
		assertThat(locked.getId(), is(plain.getId()));
		assertThat(locked.getStr(), is(plain.getStr()));
	}

	@Test
	public void findOne_withLockOption_unknownIdReturnsNull() {
		assertThat(repo.findOne(Long.valueOf(999999L), true), is(nullValue()));
	}

	@Test
	public void findOne_nullId_isRejected() {
		assertThrows(IllegalArgumentException.class, () -> repo.findOne((Long) null));
		assertThrows(IllegalArgumentException.class, () -> repo.findOne(null, true));
	}

	// ---- edge cases ----

	@Test
	public void delete_ofAnEntityAlreadyDeletedInTheSameTransaction_changesNothing() {
		Entity foo = repo.save(new Entity("foo"));
		repo.save(new Entity("bar"));
		repo.delete(foo);

		repo.delete(foo);

		assertThat(repo.count(), is(1L));
	}

	@Test
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void saveGroup_thatFailsPartWay_isRolledBackWhenItsTransactionEnds() {
		// the second entity has 65 characters, but str is "VARCHAR(64) NOT NULL"
		List<Entity> group = Arrays.asList(new Entity("fits"), new Entity("x".repeat(65)));
		TransactionTemplate tx = new TransactionTemplate(transactionManager);

		// the failure surfaces as a data-access exception and ends the caller's transaction by rollback
		assertThrows(DataAccessException.class, () -> tx.executeWithoutResult(status -> repo.save(group)));

		// batch statements are not atomic inside the transaction (finding F-012), but nothing is committed
		assertThat(tx.execute(status -> repo.count()), is(0L));
	}

	@Test
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void readsOutsideATransaction_areRejected() {
		// the connection provider needs a Spring-managed transaction (finding F-005)
		assertThrows(IllegalStateException.class, () -> repo.exists(Long.valueOf(1L)));
		assertThrows(IllegalStateException.class, () -> repo.findOne(Long.valueOf(1L)));
	}
}
