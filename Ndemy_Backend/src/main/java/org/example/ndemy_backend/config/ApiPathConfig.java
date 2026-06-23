package org.example.ndemy_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiPathConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Agrega "/api" delante de TODOS los controllers del paquetes sin afectar Swagger ni actuator.
        configurer.addPathPrefix("/api",
                HandlerTypePredicate.forBasePackage("org.example.ndemy_backend.controllers"));
    }
}
