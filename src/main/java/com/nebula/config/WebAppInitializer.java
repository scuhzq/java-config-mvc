package com.nebula.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.ServletException;

/**
 * Created by hzq on 2017/7/27.
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer{

	private static final Logger logger = LoggerFactory.getLogger(WebAppInitializer.class);

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[]{AppConfig.class};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[]{WebConfig.class};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};
	}

	@Override
	public void onStartup(javax.servlet.ServletContext servletContext) throws ServletException {

		logger.info("======================================");
		logger.info("=========执行 WebApplicationInitializer的 onStartUp方法=================");
		logger.info("======================================");

		//字符编码过滤器
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		servletContext.addFilter("characterEncodingFilter", characterEncodingFilter)
				.addMappingForUrlPatterns(null, false, "/*");

		super.onStartup(servletContext);
	}
}
