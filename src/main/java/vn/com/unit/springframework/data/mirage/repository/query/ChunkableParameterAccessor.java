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
package vn.com.unit.springframework.data.mirage.repository.query;

import org.springframework.data.repository.query.ParameterAccessor;

import vn.com.unit.sparwings.spring.data.chunk.Chunkable;

/**
 * TODO for daisuke
 * 
 * @since TODO for daisuke
 * @version $Id$
 * @author daisuke
 */
public interface ChunkableParameterAccessor extends ParameterAccessor {
	
	/**
	 * Returns the {@link Chunkable} of the parameters, if available. Returns {@code null} otherwise.
	 * 
	 * @return
	 */
	Chunkable getChunkable();
	
}
