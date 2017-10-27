package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.example.demo.storage.StorageProperties;
import com.example.demo.storage.StorageService;
import com.example.demo.util.Utility;

@Configuration
@ComponentScan(value = { "com.example.demo" })
@EnableConfigurationProperties(StorageProperties.class)
public class SwaggerRegistryControllerAutoConfiguration {

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}

	// add upload-dir to enable under the application
	@Bean
	WebMvcConfigurer configurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {

				registry.addResourceHandler("/*.yaml").addResourceLocations(Utility.getRootPath());

			}
		};
	}

}
