package no.entur.mummu.config;

import no.entur.mummu.serializers.NetexJsonObjectMapper;
import no.entur.mummu.web.MaxCountInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    private final NetexJsonObjectMapper netexJsonObjectMapper;
    private final NetexJsonFragmentHttpMessageConverter netexJsonFragmentHttpMessageConverter;
    private final MaxCountInterceptor maxCountInterceptor;

    @Autowired
    public WebConfig(
            NetexJsonObjectMapper netexJsonObjectMapper,
            NetexJsonFragmentHttpMessageConverter netexJsonFragmentHttpMessageConverter,
            MaxCountInterceptor maxCountInterceptor
    ) {
        this.netexJsonObjectMapper = netexJsonObjectMapper;
        this.netexJsonFragmentHttpMessageConverter = netexJsonFragmentHttpMessageConverter;
        this.maxCountInterceptor = maxCountInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(maxCountInterceptor);
    }

    @Override
    public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
        configurer.ignoreAcceptHeader(false)
                .defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        // Ahead of Jackson: serves pre-rendered fragments for the entity types it
        // claims, and declines everything else so Jackson still handles it.
        converters.add(netexJsonFragmentHttpMessageConverter);
        converters.add(new MappingJackson2HttpMessageConverter(netexJsonObjectMapper.get()));
        converters.add(new NetexHttpMessageConverter());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        WebMvcConfigurer.super.addFormatters(registry);
        registry.addFormatter(new StringToVehicleModeEnumeration());
        registry.addFormatter(new StringToStopTypeEnumeration());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/**")
                .addResourceLocations("classpath:/public/");
    }
}
