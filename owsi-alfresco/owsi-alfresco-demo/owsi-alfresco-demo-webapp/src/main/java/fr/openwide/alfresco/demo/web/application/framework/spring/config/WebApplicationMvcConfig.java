package fr.openwide.alfresco.demo.web.application.framework.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import fr.openwide.alfresco.app.web.framework.spring.config.AppWebMvcConfigurationSupport;

@Configuration
public class WebApplicationMvcConfig extends AppWebMvcConfigurationSupport {

	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		super.addResourceHandlers(registry);
		registry.addResourceHandler("/static/**").addResourceLocations("/static/");
	}

}
