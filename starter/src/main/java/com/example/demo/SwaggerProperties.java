package com.example.demo;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("swagger")
@Validated
public class SwaggerProperties {

	/**
	 * Folder location for read yaml files submodule
	 */
	@NotEmpty
	private String yaml;

	public String getYaml() {
		return yaml;
	}

	public void setYaml(String yaml) {
		this.yaml = yaml;
	}

}
