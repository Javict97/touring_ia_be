package io.atlassian.micros.dr2insights.application.config;

import io.atlassian.micros.springboot.env.ConditionalOnDev;
import io.atlassian.micros.springboot.env.ConditionalOnLocal;
import io.atlassian.micros.springboot.env.ConditionalOnStaging;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.SpringDocConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SwaggerConfig {

    @Value("${AVAILABILITY_PATH:http://localhost:8080}")
    private String localServerValue;
    @Value("${AVAILABILITY_DEV_PATH:https://dr2-insights-availability.ap-southeast-2.dev.atl-paas.net}")
    private String devServerValue;
    @Value("${AVAILABILITY_STG_PATH:https://dr2-insights-availability.us-east-1.staging.atl-paas.net}")
    private String stgServerValue;

    @Bean
    @Primary
    SpringDocConfiguration springDocConfiguration() {
        return new SpringDocConfiguration();
    }

    @Bean
    @ConditionalOnLocal
    public OpenAPI customOpenAPI() {
        var localServer = new Server().url(localServerValue);
        return new OpenAPI().addServersItem(localServer).components(getSecuritySchemes());
    }

    @Bean
    @ConditionalOnDev
    public OpenAPI customOpenAPIDev() {
        var devServer = new Server().url(devServerValue);
        return new OpenAPI().addServersItem(devServer).components(getSecuritySchemes());
    }

    @Bean
    @ConditionalOnStaging
    public OpenAPI customOpenAPIStg() {
        var stgServer = new Server().url(stgServerValue);
        return new OpenAPI().addServersItem(stgServer).components(getSecuritySchemes());
    }

    private Components getSecuritySchemes() {
        return new Components().addSecuritySchemes("bearerAuth",
                new SecurityScheme().type(SecurityScheme.Type.HTTP).in(SecurityScheme.In.HEADER).scheme("bearer").bearerFormat("JWT"));
    }

}
