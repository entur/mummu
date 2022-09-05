package no.entur.mummu.config;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfiguration {

    @Value("${no.entur.mummu.swagger.host.url}")
    private String hostUrl;

    public SwaggerConfiguration() {
        ModelConverters.getInstance().addConverter(new CustomConverters());
    }

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl(hostUrl);
        return new OpenAPI().servers(List.of(server));
    }
}
