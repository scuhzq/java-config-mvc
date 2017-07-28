package com.nebula.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.annotation.PostConstruct;

/**
 * Created by hzq on 2017/7/27.
 */
@Configuration
@EnableTransactionManagement//在spring-orm，开启事务==<tx:annotation-driven />
@EnableAspectJAutoProxy
@EnableAsync
@EnableScheduling
@ComponentScan(basePackages = "com.nebula",
		excludeFilters = {@ComponentScan.Filter(Configuration.class),
				@ComponentScan.Filter(Controller.class),
				@ComponentScan.Filter(ControllerAdvice.class)})
@Import({DatabaseConfig.class, RedisConfig.class})
@PropertySources({@PropertySource(value = "classpath:properties/dby-finance-${dby.finance.env}.properties")})
public class AppConfig {

	private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

	@PostConstruct
	public void appConfigLogger(){
		logger.info("======================================");
		logger.info("=========执行 AppConfig中bean的初始化=================");
		logger.info("======================================");
	}

	@Bean
	public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(){
		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		return propertySourcesPlaceholderConfigurer;
	}

}
