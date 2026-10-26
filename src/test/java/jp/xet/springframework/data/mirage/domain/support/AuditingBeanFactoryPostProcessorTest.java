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
package jp.xet.springframework.data.mirage.domain.support;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Optional;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.data.domain.AuditorAware;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link AuditingBeanFactoryPostProcessor}.
 */
@SuppressWarnings("javadoc")
public class AuditingBeanFactoryPostProcessorTest {

	private static final String ASPECT_BEAN_NAME = "org.springframework.context.config.internalBeanConfigurerAspect";


	public static class TestAuditor implements AuditorAware<String> {

		@Override
		public Optional<String> getCurrentAuditor() {
			return Optional.of("tester");
		}
	}


	@Test
	public void auditorAwareBeans_areMadeLazy_whenSpringConfiguredIsActive() {
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		factory.registerSingleton(ASPECT_BEAN_NAME, new Object());
		factory.registerBeanDefinition("auditor", new RootBeanDefinition(TestAuditor.class));

		new AuditingBeanFactoryPostProcessor().postProcessBeanFactory(factory);

		assertThat(factory.getBeanDefinition("auditor").isLazyInit(), is(true));
	}

	@Test
	public void auditorAwareBeans_areLeftUnchanged_whenSpringConfiguredIsNotActive() {
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		factory.registerBeanDefinition("auditor", new RootBeanDefinition(TestAuditor.class));

		new AuditingBeanFactoryPostProcessor().postProcessBeanFactory(factory);

		assertThat(factory.getBeanDefinition("auditor").isLazyInit(), is(false));
	}
}
