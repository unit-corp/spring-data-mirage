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
package vn.com.unit.springframework.data.mirage.repository.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

import org.junit.jupiter.api.Test;

/**
 * Tests the {@code auditing} element of the XML namespace.
 */
@SuppressWarnings("javadoc")
public class AuditingNamespaceTest {

	private static final String ASPECT_BEAN_NAME = "org.springframework.context.config.internalBeanConfigurerAspect";

	private static final String XML = "vn/com/unit/springframework/data/mirage/repository/config/auditing-namespace.xml";


	/**
	 * The handler registers a parser for {@code auditing}, but the schema file has the element commented out
	 * (also in the original module), so a validating load rejects it.
	 */
	@Test
	public void auditingElement_isRejectedBySchemaValidation_becauseTheSchemaDoesNotDeclareIt() {
		GenericApplicationContext context = new GenericApplicationContext();
		try {
			XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);

			assertThrows(BeanDefinitionStoreException.class,
					() -> reader.loadBeanDefinitions(new ClassPathResource(XML)));
		} finally {
			context.close();
		}
	}

	/**
	 * Without schema validation the namespace handler is reached; it needs spring-aspects, which is not a
	 * dependency of this module, so it reports a configuration problem instead of registering the aspect.
	 */
	@Test
	public void auditingElement_isRejected_whenSpringAspectsIsNotOnTheClasspath() {
		GenericApplicationContext context = new GenericApplicationContext();
		try {
			XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
			reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);
			reader.setNamespaceAware(true);

			assertThrows(BeanDefinitionParsingException.class,
					() -> reader.loadBeanDefinitions(new ClassPathResource(XML)));

			assertThat(context.containsBeanDefinition(ASPECT_BEAN_NAME), is(false));
		} finally {
			context.close();
		}
	}
}
