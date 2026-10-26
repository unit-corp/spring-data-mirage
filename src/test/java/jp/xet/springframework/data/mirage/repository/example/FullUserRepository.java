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

import jp.xet.sparwings.spring.data.repository.BatchReadableRepository;
import jp.xet.sparwings.spring.data.repository.BatchWritableRepository;
import jp.xet.sparwings.spring.data.repository.LockableCrudRepository;
import jp.xet.sparwings.spring.data.repository.ScannableRepository;
import jp.xet.sparwings.spring.data.repository.TruncatableRepository;

/**
 * Repository interface for {@link User} that exposes create, delete and lock-option operations, which
 * {@link UserRepository} does not declare. The key of {@link User} is assigned by the application, so a second
 * create with the same key collides.
 */
public interface FullUserRepository extends ScannableRepository<User, String>, LockableCrudRepository<User, String>,
		BatchReadableRepository<User, String>, BatchWritableRepository<User, String>,
		TruncatableRepository<User, String> {
}
