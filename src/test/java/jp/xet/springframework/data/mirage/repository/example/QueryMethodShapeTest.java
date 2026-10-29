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
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.Test;

import vn.com.unit.sparwings.spring.data.chunk.Chunk;
import vn.com.unit.sparwings.spring.data.chunk.ChunkRequest;

import jp.xet.springframework.data.mirage.repository.TestConfiguration;

/**
 * Tests for each return shape supported by query methods, modifying queries and fixed parameters.
 */
@SpringJUnitConfig(classes = TestConfiguration.class)
@Transactional
@SuppressWarnings("javadoc")
public class QueryMethodShapeTest {

	@Autowired
	QueryRepository queries;

	@Autowired
	FullEntityRepository entities;


	private void seed(String... strs) {
		for (String str : strs) {
			entities.save(new Entity(str));
		}
	}

	private static List<String> strs(Iterable<Entity> entities) {
		List<String> result = new ArrayList<String>();
		for (Entity e : entities) {
			result.add(e.getStr());
		}
		return result;
	}

	// ---- single entity ----

	@Test
	public void singleEntity_oneMatch_returnsIt() {
		seed("foo", "bar");

		Entity found = queries.findOneByStr("bar");

		assertThat(found, is(notNullValue()));
		assertThat(found.getStr(), is("bar"));
	}

	@Test
	public void singleEntity_noMatch_returnsNull() {
		seed("foo");

		assertThat(queries.findOneByStr("none"), is(nullValue()));
	}

	@Test
	public void singleEntity_severalMatches_raisesIncorrectResultSize() {
		seed("dup", "dup");

		IncorrectResultSizeDataAccessException e =
				assertThrows(IncorrectResultSizeDataAccessException.class, () -> queries.findOneByStr("dup"));

		assertThat(e.getActualSize(), is(2));
		assertThat(e.getExpectedSize(), is(1));
	}

	// ---- optional ----

	@Test
	public void optional_present_forMatchingInput() {
		seed("foo");

		Optional<Entity> found = queries.findOptionalByStr("foo");

		assertThat(found.isPresent(), is(true));
		assertThat(found.get().getStr(), is("foo"));
	}

	@Test
	public void optional_empty_forNonMatchingInput() {
		seed("foo");

		assertThat(queries.findOptionalByStr("none").isPresent(), is(false));
	}

	// ---- list ----

	@Test
	public void list_returnsAllMatchesInSqlOrder() {
		seed("dup", "other", "dup");

		List<Entity> found = queries.findListByStr("dup");

		assertThat(found, hasSize(2));
		assertThat(found.get(0).getId() < found.get(1).getId(), is(true));
	}

	// ---- page ----

	@Test
	public void page_returnsRequestedPage() {
		seed("foo", "foo2", "foo3", "bar", "bar2", "bar3", "baz");

		Page<Entity> page0 = queries.findPageByStrStartsWith("foo", PageRequest.of(0, 2, Direction.ASC, "str"));
		assertThat(page0.getNumber(), is(0));
		assertThat(page0.getSize(), is(2));
		assertThat(strs(page0.getContent()), contains("foo", "foo2"));

		Page<Entity> page1 = queries.findPageByStrStartsWith("foo", PageRequest.of(1, 2, Direction.ASC, "str"));
		assertThat(page1.getNumber(), is(1));
		assertThat(strs(page1.getContent()), contains("foo3"));
		// The total is only exact when the SQL uses the MySQL-specific row-count marker; see findings.md.
		assertThat(page1.getTotalElements(), is(greaterThanOrEqualTo(3L)));
	}

	// ---- chunk with an extra parameter ----

	@Test
	public void chunk_withExtraParameter_walksForwardWithoutSkippingOrRepeating() {
		seed("foo", "foo2", "bar", "bar2", "bar3", "baz");

		List<String> seen = new ArrayList<String>();
		Chunk<Entity> chunk = queries.findChunkByStrStartsWith("b", new ChunkRequest(2));
		assertThat(strs(chunk.getContent()), contains("bar", "bar2"));
		seen.addAll(strs(chunk.getContent()));

		chunk = queries.findChunkByStrStartsWith("b", chunk.nextChunkable());
		assertThat(strs(chunk.getContent()), contains("bar3", "baz"));
		seen.addAll(strs(chunk.getContent()));

		chunk = queries.findChunkByStrStartsWith("b", chunk.nextChunkable());
		assertThat(chunk.getContent().isEmpty(), is(true));
		assertThat(chunk.getPaginationToken(), is(nullValue()));

		assertThat(seen, contains("bar", "bar2", "bar3", "baz"));
	}

	// ---- modifying query ----

	@Test
	public void modifying_reportsAffectedRowsAndChangesData() {
		seed("foo", "foo", "bar");

		int affected = queries.updateStr("foo", "FOO");

		assertThat(affected, is(2));
		assertThat(queries.findListByStr("FOO"), hasSize(2));
		assertThat(queries.findListByStr("foo"), hasSize(0));
		assertThat(queries.findListByStr("bar"), hasSize(1));
	}

	// ---- several fixed parameters ----

	@Test
	public void fixedParameters_everyDeclaredValueIsApplied() {
		seed("foo", "bar", "baz");

		List<Entity> found = queries.findFixed();

		assertThat(strs(found), contains("foo", "bar"));
	}

	// ---- scalar (single value) ----

	@Test
	public void scalar_returnsTheCountForMatchingInput() {
		seed("foo", "foo2", "foo3", "bar");

		assertThat(queries.countByStrStartsWith("foo"), is(3));
	}

	@Test
	public void scalar_returnsZeroForNonMatchingInput() {
		seed("foo", "bar");

		assertThat(queries.countByStrStartsWith("none"), is(0));
	}
}
