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

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import vn.com.unit.miragesql.miragesql.SqlManager;
import vn.com.unit.miragesql.miragesql.SqlManagerImpl;
import vn.com.unit.miragesql.miragesql.bean.BeanDescFactory;
import vn.com.unit.miragesql.miragesql.bean.FieldPropertyExtractor;
import vn.com.unit.miragesql.miragesql.dialect.MySQLDialect;
import vn.com.unit.miragesql.miragesql.integration.spring.SpringConnectionProvider;
import vn.com.unit.miragesql.miragesql.naming.RailsLikeNameConverter;
import vn.com.unit.miragesql.miragesql.provider.ConnectionProvider;
import vn.com.unit.springframework.data.mirage.repository.config.EnableMirageRepositories;
import vn.com.unit.springframework.data.mirage.repository.support.MiragePersistenceExceptionTranslator;

/**
 * Test Configuration
 */
@Configuration
@EnableTransactionManagement
@EnableMirageRepositories
public class TestConfiguration {
	
	@Bean
	public SqlManager sqlManager() {
		SqlManagerImpl sqlManagerImpl = new SqlManagerImpl();
		sqlManagerImpl.setConnectionProvider(connectionProvider());
		sqlManagerImpl.setDialect(new MySQLDialect());
		sqlManagerImpl.setBeanDescFactory(beanDescFactory());
		sqlManagerImpl.setNameConverter(new RailsLikeNameConverter());
		return sqlManagerImpl;
	}
	
	@Bean
	public ConnectionProvider connectionProvider() {
		SpringConnectionProvider springConnectionProvider = new SpringConnectionProvider();
		springConnectionProvider.setDataSource(dataSource());
		return springConnectionProvider;
	}
	
	@Bean
	public BeanDescFactory beanDescFactory() {
		BeanDescFactory beanDescFactory = new BeanDescFactory();
		beanDescFactory.setPropertyExtractor(new FieldPropertyExtractor());
		return beanDescFactory;
	}
	
	@Bean
	public MiragePersistenceExceptionTranslator persistenceExceptionTranslator() {
		return new MiragePersistenceExceptionTranslator();
	}
	
	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			.addScripts("classpath:create.sql")
			.setName("test")
			.build();
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}
}
