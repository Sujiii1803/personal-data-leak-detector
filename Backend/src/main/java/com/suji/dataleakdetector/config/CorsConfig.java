package com.suji.dataleakdetector.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                            "https://personal-data-leak-detector.vercel.app",
                                            "https://personal-data-leak-detector-a24lbx1nq-suji-s-projects1.vercel.app"
                                        )
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
}