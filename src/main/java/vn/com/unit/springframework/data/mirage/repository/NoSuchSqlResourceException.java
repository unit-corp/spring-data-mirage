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
package vn.com.unit.springframework.data.mirage.repository;

import java.util.Arrays;

/**
 * TODO for daisuke
 * 
 * @since 0.1
 * @version $Id$
 * @author daisuke
 */
@SuppressWarnings("serial")
public class NoSuchSqlResourceException extends RuntimeException {
	
	private final SqlResourceCandidate[] candidates;
	
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param candidates array of SQL resource candidate
	 * @since 0.2.1
	 */
	public NoSuchSqlResourceException(SqlResourceCandidate[] candidates) {
		super(Arrays.toString(candidates));
		this.candidates = candidates;
	}
	
	public SqlResourceCandidate[] getCandidates() {
		return candidates;
	}
}
