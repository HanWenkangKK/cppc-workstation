package com.nanqiong.cppcaiagent.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI aiAgentOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CPPC AI Agent API")
                        .description("AI 报告生成相关接口文档")
                        .version("v1")
                        .contact(new Contact().name("CPPC Workstation"))
                        .license(new License().name("Internal")));
    }
}
