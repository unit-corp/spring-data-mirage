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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import vn.com.unit.sparwings.spring.data.repository.ScannableRepository;
import vn.com.unit.springframework.data.mirage.repository.support.MirageDefaultRepositoryMetadata;
import vn.com.unit.springframework.data.mirage.repository.support.MirageRepositoryFactoryBean;

/**
 * Mirage specific configuration extension parsing custom attributes from the XML namespace and
 * {@link EnableMirageRepositories} annotation.
 * 
 * @author Oliver Gierke
 * @author Eberhard Wolff
 * @author Gil Markham
 */
public class MirageRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {
	private static final Log logger = LogFactory.getLog(MirageRepositoryConfigExtension.class);
	private static final String DEFAULT_SQL_MANAGER_BEAN_NAME = "sqlManager";
	
	private static final String DEFAULT_TRANSACTION_MANAGER_BEAN_NAME = "transactionManager";
	
	private static final Class<?> PET_POST_PROCESSOR = PersistenceExceptionTranslationPostProcessor.class;
	private static final String CLASS_LOADING_ERROR = "%s - Could not load type %s using class loader %s";
	@Override
	public <T extends RepositoryConfigurationSource> Collection<RepositoryConfiguration<T>> getRepositoryConfigurations(
			T configSource, ResourceLoader loader, boolean strictMatchesOnly) {

		Assert.notNull(configSource, "ConfigSource must not be null");
		Assert.notNull(loader, "Loader must not be null");

		Set<RepositoryConfiguration<T>> result = new HashSet<>();

		for (BeanDefinition candidate : configSource.getCandidates(loader)) {

			RepositoryConfiguration<T> configuration = getRepositoryConfiguration(candidate, configSource);
			Class<?> repositoryInterface = loadRepositoryInterface(configuration,
					getConfigurationInspectionClassLoader(loader));

			if (repositoryInterface == null) {
				result.add(configuration);
				continue;
			}

			RepositoryMetadata metadata = MirageDefaultRepositoryMetadata.getMetadata(repositoryInterface);

			boolean qualifiedForImplementation = !strictMatchesOnly || configSource.usesExplicitFilters()
					|| isStrictRepositoryCandidate(metadata);

			if (qualifiedForImplementation && useRepositoryConfiguration(metadata)) {
				result.add(configuration);
			}
		}

		return result;
	}
	
	@Nullable
	private Class<?> loadRepositoryInterface(RepositoryConfiguration<?> configuration,
			@Nullable ClassLoader classLoader) {

		String repositoryInterface = configuration.getRepositoryInterface();

		try {
			return org.springframework.util.ClassUtils.forName(repositoryInterface, classLoader);
		} catch (ClassNotFoundException | LinkageError e) {
			logger.warn(String.format(CLASS_LOADING_ERROR, getModuleName(), repositoryInterface, classLoader), e);
		}

		return null;
	}
	
	@Override
	public String getRepositoryFactoryBeanClassName() {
		return MirageRepositoryFactoryBean.class.getName();
	}
	
	@Override
	public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {
		AnnotationAttributes attributes = config.getAttributes();
		postProcess(builder, attributes.getString("sqlManagerRef"), attributes.getString("transactionManagerRef"),
				config.getSource());
	}
	
	@Override
	public void postProcess(BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config) {
		Element element = config.getElement();
		postProcess(builder, element.getAttribute("sql-manager-ref"), element.getAttribute("transaction-manager-ref"),
				config.getSource());
	}
	
	@Override
	public void registerBeansForRoot(BeanDefinitionRegistry registry,
			RepositoryConfigurationSource configurationSource) {
		super.registerBeansForRoot(registry, configurationSource);
		
		if (!hasBean(PET_POST_PROCESSOR, registry)) {
			AbstractBeanDefinition definition =
					BeanDefinitionBuilder.rootBeanDefinition(PET_POST_PROCESSOR).getBeanDefinition();
			registerWithSourceAndGeneratedBeanName(definition, registry, configurationSource.getSource());
		}
	}
	
	@Override
	protected String getModulePrefix() {
		return "mirage";
	}
	
	private void postProcess(BeanDefinitionBuilder builder, String sqlManagerRef, String transactionManagerRef,
			Object source) {
		if (StringUtils.hasText(sqlManagerRef)) {
			builder.addPropertyReference("sqlManager", sqlManagerRef);
		} else {
			builder.addPropertyReference("sqlManager", DEFAULT_SQL_MANAGER_BEAN_NAME);
		}
		
		if (StringUtils.hasText(transactionManagerRef)) {
			builder.addPropertyValue("transactionManager", transactionManagerRef);
		} else {
			builder.addPropertyValue("transactionManager", DEFAULT_TRANSACTION_MANAGER_BEAN_NAME);
		}
	}
	
    /*
     * HungHT - 20220601 - Override getIdentifyingTypes to fix Issue when spring data redis and spring data mirage, as its throwing with
     * Spring Data Mirage does not support multi-store setups!
     */
    protected Collection<Class<?>> getIdentifyingTypes() {
        return Arrays.asList(ScannableRepository.class);
    }
}
