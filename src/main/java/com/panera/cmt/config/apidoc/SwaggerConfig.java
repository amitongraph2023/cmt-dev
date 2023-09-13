package com.panera.cmt.config.apidoc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String V1_INCLUDE_PATTERN  = "/api/v1/.*";

    @Bean
    public Docket v1Docket() {
        Contact contact = new Contact(
                "Customer Management Tool",
                "https://cmt.panerabread.com",
                "none@xyz.com");


        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("Customer Management Tool")
                .description("Web App and backend services for managing customers")
                .version("0.13.0-SNAPSHOT")
                .contact(contact)
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName("API v1")
                .apiInfo(apiInfo)
                .select()
                .paths(regex(V1_INCLUDE_PATTERN))
                .build();
    }
}
