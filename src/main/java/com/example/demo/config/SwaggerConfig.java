package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration

public class SwaggerConfig {

	@Value("${openapi.service.title}")
	String title;
	@Value("${openapi.service.version}")
	String version;
	@Value("${openapi.service.server}")
	String serverUrl;

	private static final String SCHEME_NAME = "basicAuth";
	private static final String SCHEME = "basic";

	@Bean
	public OpenAPI myOpenAPI() {
		return new OpenAPI().info(new Info()

				.title(title).version(version).description("API documentation for quan")
				.license(new License().name("Apache 2.0").url("https://springdoc.org"))

		).components(new Components().addSecuritySchemes(SCHEME_NAME, createSecurityScheme()))
				.addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME));
	}

	private SecurityScheme createSecurityScheme() {
		return new SecurityScheme().name(SCHEME_NAME).type(SecurityScheme.Type.HTTP).scheme(SCHEME);
	}

}