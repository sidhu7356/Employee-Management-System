package com.company.ems.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI employeeManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employee Management System API")
                        .description("""
                                REST API for managing employees and departments within an organization.
                                
                                **Key Features:**
                                - Full CRUD for employees and departments
                                - Hierarchical reporting chain traversal
                                - Department analytics (employee count, average & total salary)
                                - Pagination support on all collection endpoints
                                - Employee lookup (id + name) for dropdown use cases
                                - Department employee expansion
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("EMS Team")
                                .email("ems-support@company.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development"),
                        new Server().url("http://dev.ems.company.com").description("Development")
                ));
    }
}
