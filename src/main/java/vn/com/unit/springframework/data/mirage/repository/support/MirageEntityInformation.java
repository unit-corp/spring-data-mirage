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
package vn.com.unit.springframework.data.mirage.repository.support;

import java.io.Serializable;

import org.springframework.data.repository.core.EntityInformation;

/**
 * Extension of {@link EntityInformation} to capture aditional Mirage specific information about entities.
 * 
 * @param <T> 
 * @param <ID> 
 * @since 0.1
 * @version $Id$
 * @author daisuke
 */
public interface MirageEntityInformation<T, ID extends Serializable>extends EntityInformation<T, ID> {
	
	/**
	 * Returns the Mirage entity name.
	 * 
	 * @return the Mirage entity name.
	 * @since 0.1
	 */
	String getEntityName();
	
}
