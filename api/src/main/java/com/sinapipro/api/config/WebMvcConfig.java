package com.sinapipro.api.config;

import com.sinapipro.api.security.application.ProjectAccessInterceptor;
import com.sinapipro.api.tenant.application.TenantInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final TenantInterceptor tenantInterceptor;
    private final ProjectAccessInterceptor projectAccessInterceptor;
    private final String allowedOrigins;

    public WebMvcConfig(TenantInterceptor tenantInterceptor, ProjectAccessInterceptor projectAccessInterceptor,
                        @Value("${sinapipro.security.cors.allowed-origins:*}") String allowedOrigins) {
        this.tenantInterceptor = tenantInterceptor;
        this.projectAccessInterceptor = projectAccessInterceptor;
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins.split(","))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(!"*".equals(allowedOrigins))
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns("/api/v1/auth/**");
        registry.addInterceptor(projectAccessInterceptor)
                .addPathPatterns("/api/v1/projects/{projectId}/**")
                .excludePathPatterns("/api/v1/auth/**");
    }
}
