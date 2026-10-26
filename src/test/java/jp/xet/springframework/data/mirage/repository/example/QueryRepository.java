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

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import jp.xet.sparwings.spring.data.chunk.Chunk;
import jp.xet.sparwings.spring.data.chunk.Chunkable;
import jp.xet.sparwings.spring.data.repository.ScannableRepository;

import jp.xet.springframework.data.mirage.repository.query.Modifying;
import jp.xet.springframework.data.mirage.repository.query.StaticParam;
import jp.xet.springframework.data.mirage.repository.query.StaticParams;

/**
 * Repository interface for {@link Entity} with one query method per supported return shape.
 * Each method has its own SQL resource {@code QueryRepository_<method>.sql}.
 */
public interface QueryRepository extends ScannableRepository<Entity, Long> {

	Entity findOneByStr(@Param("str") String str);

	Optional<Entity> findOptionalByStr(@Param("str") String str);

	List<Entity> findListByStr(@Param("str") String str);

	Page<Entity> findPageByStrStartsWith(@Param("str") String str, Pageable pageable);

	Chunk<Entity> findChunkByStrStartsWith(@Param("str") String str, Chunkable chunkable);

	@Modifying
	int updateStr(@Param("from") String from, @Param("to") String to);

	@StaticParams({
		@StaticParam(key = "first", value = "foo"),
		@StaticParam(key = "second", value = "bar")
	})
	List<Entity> findFixed();

	int countByStrStartsWith(@Param("str") String str);
}
