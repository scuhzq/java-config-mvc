package com.nebula.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hzq on 2017/7/27.
 */
@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@EnableAsync
@ComponentScan(basePackages = "com.nebula", useDefaultFilters = false,
		includeFilters = {@ComponentScan.Filter(Controller.class), @ComponentScan.Filter(ControllerAdvice.class)})
public class WebConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * 让spring正确解析出${}
	 */
	@Bean
	public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(){
		return new PropertySourcesPlaceholderConfigurer();
	}

	/**
	 * 配置内容协商的策略
	 */
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		Map<String, MediaType> mediaTypeMap = new HashMap<>();
		mediaTypeMap.put("html", MediaType.TEXT_HTML);
		mediaTypeMap.put("json", MediaType.APPLICATION_JSON);
		mediaTypeMap.put("atom", MediaType.APPLICATION_ATOM_XML);

		configurer.favorParameter(false)//是否支持format参数
				.favorPathExtension(false).useJaf(false)//是否支持.json .xml等扩展名
				.ignoreAcceptHeader(true)//忽视Accept请求头
				.defaultContentType(MediaType.APPLICATION_JSON)
				.mediaTypes(mediaTypeMap);

		super.configureContentNegotiation(configurer);
	}

	/**
	 * 视图解析器，允许同样的内容呈现不同的view
	 */
	@Bean
	public ContentNegotiatingViewResolver contentNegotiatingViewResolver(){
		ContentNegotiatingViewResolver contentNegotiatingViewResolver = new ContentNegotiatingViewResolver();
		List<ViewResolver> viewResolverList = new ArrayList<>();
		viewResolverList.add(thymeleafViewResolver());

		List<View> viewList = new ArrayList<>();
		viewList.add(mappingJackson2JsonView()); // 配置默认视图解析

		contentNegotiatingViewResolver.setOrder(1);
		contentNegotiatingViewResolver.setViewResolvers(viewResolverList);
		contentNegotiatingViewResolver.setDefaultViews(viewList);

		return contentNegotiatingViewResolver;
	}

	@Bean
	public MappingJackson2JsonView mappingJackson2JsonView(){
		return new MappingJackson2JsonView();//使用MappingJackson2JsonView 配合@ResponseBody来返回JSON格式
	}

	@Bean
	public ThymeleafViewResolver thymeleafViewResolver(){
		ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
		thymeleafViewResolver.setOrder(1);
		thymeleafViewResolver.setCharacterEncoding("utf-8");
		thymeleafViewResolver.setTemplateEngine(templateEngine());

		return thymeleafViewResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine(){
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		templateEngine.setEnableSpringELCompiler(true);

		return templateEngine;
	}

	@Bean
	public SpringResourceTemplateResolver templateResolver(){
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setPrefix("/WEB-INF/templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML5");
		templateResolver.setCacheable(false);//设置热部署
		templateResolver.setOrder(1);
		templateResolver.setCharacterEncoding("UTF-8");

		return templateResolver;
	}

}
