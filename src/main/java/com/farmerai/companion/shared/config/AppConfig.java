package com.farmerai.companion.shared.config;

import com.farmerai.companion.auth.domain.service.AuthenticationDomainService;
import com.farmerai.companion.shared.logging.LoggingInterceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Common application configuration.
 *
 * <p>
 * Registers beans and interceptors used across the application.
 * </p>
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {

    private final LoggingInterceptor loggingInterceptor;

    public AppConfig(LoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    /**
     * Registers the domain service as a Spring bean.
     *
     * <p>
     * The domain service is a pure Java class (no Spring annotations)
     * to maintain domain layer purity. We register it here as a bean
     * so Spring can inject it into the application service.
     * </p>
     */
    @Bean
    public AuthenticationDomainService authenticationDomainService() {
        return new AuthenticationDomainService();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/api/**");
    }
}
