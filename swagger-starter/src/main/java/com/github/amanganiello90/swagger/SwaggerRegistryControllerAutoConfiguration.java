package com.github.amanganiello90.swagger;

import org.apache.catalina.servlet4preview.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.github.amanganiello90.swagger.application.storage.StorageProperties;
import com.github.amanganiello90.swagger.application.storage.StorageService;
import com.github.amanganiello90.swagger.application.util.Utility;

@Configuration
@ComponentScan(value = { "com.github.amanganiello90.swagger.application" })
@EnableConfigurationProperties({ StorageProperties.class, SwaggerProperties.class })
@Conditional(ProfileCondition.class)
public class SwaggerRegistryControllerAutoConfiguration {

	@Autowired
	ServletContext context;

	@Autowired
	ResourceLoader resourceLoader;

	private final SwaggerProperties swaggerProperties;

	public SwaggerRegistryControllerAutoConfiguration(SwaggerProperties swaggerProperties) {
		this.swaggerProperties = swaggerProperties;
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}

	// create txt file that list all yaml loaded from classpath
	@EventListener(ApplicationReadyEvent.class)
	public void postConstruct() {

		Utility.extractYAMLFromJar(swaggerProperties.getYaml());

	}

	// add upload-dir to enable under the application
	@Bean
	WebMvcConfigurer configurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {

				registry.addResourceHandler("/swagger-ui/*.yaml").addResourceLocations(Utility.getRootPath());
				registry.addResourceHandler("/**").addResourceLocations("classpath:/swagger-ui/");

			}

			@Override
			public void addViewControllers(ViewControllerRegistry registry) {

				registry.addViewController("/swagger-ui").setViewName("forward:/index.html");

			}
		};
	};

}
