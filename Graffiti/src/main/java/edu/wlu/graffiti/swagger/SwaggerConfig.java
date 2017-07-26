package edu.wlu.graffiti.swagger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	@Bean
	public Docket searchApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("edu.wlu.graffiti.controller"))
				.paths(regex("/.*"))
				.build();
	}
	
	private ApiInfo metaData() {
		ApiInfo apiInfo = new ApiInfo(
				"Spring Boot REST API",
				"Spring Boot REST API for Ancient Graffiti Project",
				"1.0",
				"Terms of Service",
				new Contact("Ancient Graffiti Project", "http://ancientgraffiti.org", "about@ancientgraffiti.org"),
				"Apache License Version 2.0",
				"https://www.apache.org/licenses/LICENSE-2.0", null
				);
		return apiInfo;
	}
}
