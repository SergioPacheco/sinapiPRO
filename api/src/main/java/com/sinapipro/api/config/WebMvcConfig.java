package com.sinapipro.api.config;

import com.sinapipro.api.security.application.ProjectAccessInterceptor;
import com.sinapipro.api.tenant.application.TenantInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final TenantInterceptor tenantInterceptor;
    private final ProjectAccessInterceptor projectAccessInterceptor;

    public WebMvcConfig(TenantInterceptor tenantInterceptor, ProjectAccessInterceptor projectAccessInterceptor) {
        this.tenantInterceptor = tenantInterceptor;
        this.projectAccessInterceptor = projectAccessInterceptor;
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
