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
package jp.xet.springframework.data.mirage.extra;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.junit.jupiter.api.Test;

import com.miragesql.miragesql.SqlManager;

import jp.xet.springframework.data.mirage.repository.NoSuchSqlResourceException;
import jp.xet.springframework.data.mirage.repository.TestConfiguration;
import jp.xet.springframework.data.mirage.repository.support.MirageRepositoryFactory;

/**
 * Tests the error raised when the SQL resource of a query method is missing.
 */
@SpringJUnitConfig(classes = TestConfiguration.class)
@SuppressWarnings("javadoc")
public class MissingSqlResourceTest {

	@Autowired
	SqlManager sqlManager;


	@Test
	public void missingSqlResource_failsWhenTheRepositoryIsCreated_andNamesTheSearchedLocations() {
		MirageRepositoryFactory factory = new MirageRepositoryFactory(sqlManager);

		RuntimeException thrown =
				assertThrows(RuntimeException.class, () -> factory.getRepository(NoSqlRepository.class));

		Throwable t = thrown;
		while (t != null && !(t instanceof NoSuchSqlResourceException)) {
			t = t.getCause();
		}
		assertThat("NoSuchSqlResourceException in the cause chain of " + thrown, t, is(notNullValue()));
		NoSuchSqlResourceException e = (NoSuchSqlResourceException) t;
		assertThat(e.getCandidates().length, is(greaterThan(0)));
		assertThat(e.getMessage(), containsString("NoSqlRepository"));
		assertThat(e.getMessage(), containsString("findNothing"));
	}
}
