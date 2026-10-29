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

import java.util.List;

import vn.com.unit.sparwings.spring.data.repository.ScannableRepository;

import jp.xet.springframework.data.mirage.repository.example.Entity;

/**
 * Repository interface with a query method that has no SQL resource file.
 * It lives outside the package scanned by the shared test configuration on purpose, because the missing SQL
 * file makes the creation of the repository fail.
 */
public interface NoSqlRepository extends ScannableRepository<Entity, Long> {

	List<Entity> findNothing();
}
