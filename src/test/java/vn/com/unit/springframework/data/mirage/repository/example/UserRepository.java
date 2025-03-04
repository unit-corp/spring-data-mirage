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

import vn.com.unit.sparwings.spring.data.repository.BatchReadableRepository;
import vn.com.unit.sparwings.spring.data.repository.ScannableRepository;
import vn.com.unit.sparwings.spring.data.repository.UpsertableRepository;

/**
 * Repository interface for {@link Entity}.
 * 
 * @author daisuke
 */
public interface UserRepository extends ScannableRepository<User, String>, UpsertableRepository<User, String>,
		BatchReadableRepository<User, String> {
}
