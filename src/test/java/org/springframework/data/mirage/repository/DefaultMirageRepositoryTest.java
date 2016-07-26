/*
 * Copyright 2011 Daisuke Miyamoto.
 * Created on 2011/10/21
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.springframework.data.mirage.repository;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Arrays;

import com.google.common.collect.Iterables;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mirage.repository.example.User;
import org.springframework.data.mirage.repository.example.UserRepository;
import org.springframework.data.mirage.repository.support.MirageRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import jp.sf.amateras.mirage.SqlManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/test-context.xml")
@Transactional
@SuppressWarnings("javadoc")
public class DefaultMirageRepositoryTest {
	
	@Autowired
	SqlManager sqlManager;
	
	
	@Test
	public void findAll() {
		RepositoryFactorySupport factory = new MirageRepositoryFactory(sqlManager);
		UserRepository repos = factory.getRepository(UserRepository.class);
		
		repos.save(new User("foo", "foopass"));
		repos.save(new User("bar", "barpass"));
		repos.save(new User("baz", "bazpass"));
		
		Iterable<User> all = repos.findAll();
		assertThat("findAll size", Iterables.size(all), is(3));
		assertThat("findAll", all, hasItem(new User("foo", null)));
		assertThat("findAll", all, hasItem(new User("bar", null)));
		assertThat("findAll", all, hasItem(new User("baz", null)));
	}
	
	@Test
	public void findAll2() {
		RepositoryFactorySupport factory = new MirageRepositoryFactory(sqlManager);
		UserRepository repos = factory.getRepository(UserRepository.class);
		
		repos.save(new User("foo", "foopass"));
		repos.save(new User("bar", "barpass"));
		repos.save(new User("baz", "bazpass"));
		
		Iterable<User> all = repos.findAll(Arrays.asList("foo", "baz"));
		assertThat("findAll size", Iterables.size(all), is(2));
		assertThat("findAll", all, hasItem(new User("foo", null)));
		assertThat("findAll", all, not(contains(new User("bar", null))));
		assertThat("findAll", all, hasItem(new User("baz", null)));
	}
}
