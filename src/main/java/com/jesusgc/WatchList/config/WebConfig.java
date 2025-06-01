package com.jesusgc.WatchList.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AdminInterceptor adminInterceptor;

    public WebConfig(AdminInterceptor adminInterceptor) {
        this.adminInterceptor = adminInterceptor;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        System.out.println("Configurando AdminInterceptor para /admin/**");

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**");
    }
}
