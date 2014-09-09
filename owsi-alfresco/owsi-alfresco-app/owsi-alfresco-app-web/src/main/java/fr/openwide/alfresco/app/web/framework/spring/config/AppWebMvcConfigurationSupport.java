package fr.openwide.alfresco.app.web.framework.spring.config;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import fr.openwide.alfresco.app.core.security.service.UserService;
import fr.openwide.alfresco.app.web.download.binding.DownloadResponseMethodProcessor;
import fr.openwide.alfresco.app.web.framework.spring.binding.HandlerInterceptorAwareModelMethodProcessor;
import fr.openwide.alfresco.app.web.framework.spring.binding.NameReferenceFormatter;
import fr.openwide.alfresco.app.web.framework.spring.binding.NodeReferenceFormatter;
import fr.openwide.alfresco.app.web.framework.spring.binding.StoreReferenceFormatter;
import fr.openwide.alfresco.app.web.framework.spring.interceptor.ExceptionLoggerHandlerInterceptor;
import fr.openwide.alfresco.app.web.security.authentication.AuthenticationExposingInterceptor;
import fr.openwide.alfresco.app.web.validation.binding.AlertContainerMethodArgumentResolver;
import fr.openwide.alfresco.app.web.validation.binding.ValidationResponseMethodProcessor;

public abstract class AppWebMvcConfigurationSupport extends WebMvcConfigurationSupport {

	@Autowired
	private Environment environment;
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	@Autowired
	private UserService userService;

	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		super.addResourceHandlers(registry);
		String version = environment.getProperty("application.version");
		registry.addResourceHandler(MessageFormat.format("/static/{0}/**", version)).addResourceLocations("/static/", "classpath:/static/", "/webjars/");
	}

	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		super.addInterceptors(registry);
		// add open entityManager in view
		OpenEntityManagerInViewInterceptor openEntityManagerInView = new OpenEntityManagerInViewInterceptor();
		openEntityManagerInView.setEntityManagerFactory(entityManagerFactory);
		registry.addWebRequestInterceptor(openEntityManagerInView);
		// add exception logger
		registry.addInterceptor(new ExceptionLoggerHandlerInterceptor());
		// add authentication details
		AuthenticationExposingInterceptor authenticationExposing = new AuthenticationExposingInterceptor();
		authenticationExposing.setUserService(userService);
		registry.addInterceptor(authenticationExposing);
	}

	@Override
	protected void addFormatters(FormatterRegistry registry) {
		super.addFormatters(registry);
		registry.addFormatter(new NodeReferenceFormatter());
		registry.addFormatter(new StoreReferenceFormatter());
		registry.addFormatter(new NameReferenceFormatter());
	}
	
	@Bean
	public DownloadResponseMethodProcessor downloadResponseMethodProcessor() {
		return new DownloadResponseMethodProcessor();
	}
	@Bean
	public ValidationResponseMethodProcessor validationResponseMethodProcessor() {
		return new ValidationResponseMethodProcessor(getMessageConverters(), messageSource());
	}

	@Override
	protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		super.addArgumentResolvers(argumentResolvers);
		argumentResolvers.add(validationResponseMethodProcessor());
		argumentResolvers.add(downloadResponseMethodProcessor());
		argumentResolvers.add(new AlertContainerMethodArgumentResolver());
	}

	@Override
	protected void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		super.addReturnValueHandlers(returnValueHandlers);
		returnValueHandlers.add(validationResponseMethodProcessor());
		returnValueHandlers.add(downloadResponseMethodProcessor());
	}

	@Override
	protected void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		super.configureHandlerExceptionResolvers(exceptionResolvers);
		ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = new ExceptionHandlerExceptionResolver();
		exceptionHandlerExceptionResolver.setMessageConverters(getMessageConverters());
		// add custom argument resolvers
		List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<HandlerMethodArgumentResolver>();
		argumentResolvers.add(new HandlerInterceptorAwareModelMethodProcessor(getInterceptors()));
		argumentResolvers.add(validationResponseMethodProcessor());
		argumentResolvers.add(new AlertContainerMethodArgumentResolver());
		exceptionHandlerExceptionResolver.setCustomArgumentResolvers(argumentResolvers);
		// add custom return value handlers
		List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<HandlerMethodReturnValueHandler>();
		returnValueHandlers.add(validationResponseMethodProcessor());
		exceptionHandlerExceptionResolver.setCustomReturnValueHandlers(returnValueHandlers);
		exceptionHandlerExceptionResolver.afterPropertiesSet();

		exceptionResolvers.add(exceptionHandlerExceptionResolver);
		exceptionResolvers.add(new ResponseStatusExceptionResolver());
		exceptionResolvers.add(new DefaultHandlerExceptionResolver());
	}

	@Override
	@Bean
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
		RequestMappingHandlerAdapter handlerAdapter = super.requestMappingHandlerAdapter();
		// Force cache headers to no-cache
		handlerAdapter.setCacheSeconds(0);
		return handlerAdapter;
	}

	@Override
	@Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		RequestMappingHandlerMapping handlerMapping = super.requestMappingHandlerMapping();
		// Do NOT use suffix pattern .* to match an url to a handler
		handlerMapping.setUseSuffixPatternMatch(false);
		// Do NOT use trailing slash to match an url to a handler
		handlerMapping.setUseTrailingSlashMatch(false);
		return handlerMapping;
	}

	@Bean
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}

	public void addMessageSourcePath(List<String> paths) {
		paths.add("i18n/messages");
		paths.add("i18n/pdf-viewer");
	}

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		
		List<String> paths = new ArrayList<>();
		addMessageSourcePath(paths);
		messageSource.setBasenames(paths.toArray(new String[paths.size()]));
		return messageSource;
	}

	@Bean
	public ViewResolver jspViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/jsp/");
		resolver.setSuffix(".jsp");
		return resolver;
	}

	@Bean
	public Validator validator() {
		return new LocalValidatorFactoryBean();
	}

}
