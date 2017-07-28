package com.nebula.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.annotation.PostConstruct;

/**
 * Created by hzq on 2017/7/27.
 */
@Configuration
@EnableTransactionManagement
public class DatabaseConfig implements TransactionManagementConfigurer {

	private static Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

	@PostConstruct
	public void dbConfigLogger(){
		logger.info("======================================");
		logger.info("=========执行 DatabaseConfig 中bean的初始化=================");
		logger.info("======================================");
	}


	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return null;
	}

	@Value("${jdbc.url}")
	private String url;

	@Value("${jdbc.username}")
	private String username;

	@Value("${jdbc.password}")
	private String password;

	@Value("${jdbc.read.url}")
	private String readUrl;

	@Value("${jdbc.read.username}")
	private String readUsername;

	@Value("${jdbc.read.password}")
	private String readPassword;

	@Bean(initMethod = "init", destroyMethod = "close")
	public DruidDataSource readDataSource(){
		return initCommonDataSource(readUrl, readUsername, readPassword);
	}

	@Bean(initMethod = "init", destroyMethod = "close")
	public DruidDataSource writeDataSource(){
		return initCommonDataSource(url, username, password);
	}

	private DruidDataSource initCommonDataSource(String url, String username, String password) {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);

		dataSource.setInitialSize(1);
		dataSource.setMinIdle(10);
		dataSource.setMaxActive(1000);

		dataSource.setMaxWait(60000);

		dataSource.setTimeBetweenEvictionRunsMillis(60000);

		dataSource.setMinEvictableIdleTimeMillis(300000);
		dataSource.setValidationQuery("SELECT 'x'");
		dataSource.setTestOnBorrow(false);
		dataSource.setTestOnReturn(false);
		dataSource.setTestWhileIdle(true);

		dataSource.setPoolPreparedStatements(true);
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

		try {
			dataSource.setFilters("config,stat");
		} catch(Throwable t){
			logger.info(t.getMessage(), t);
		}

		dataSource.setRemoveAbandoned(true);
		dataSource.setRemoveAbandonedTimeout(1800);
		dataSource.setLogAbandoned(true);

		return dataSource;
	}

	/**
	 * 配置jdbctemplate
	 */
	@Bean
	public JdbcTemplate jdbcTemplate(){
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		jdbcTemplate.setDataSource(writeDataSource());

		return jdbcTemplate;
	}

	@Bean
	public NamedParameterJdbcTemplate namedParameterJdbcTemplate(){
		return new NamedParameterJdbcTemplate(writeDataSource());
	}
}
