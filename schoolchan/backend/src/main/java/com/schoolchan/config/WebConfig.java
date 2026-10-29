package com.schoolchan.config;

import org.springframework.context.annotation.Configuration;
<<<<<<< HEAD
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

=======
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

>>>>>>> d5851d3 (Initial commit)
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
<<<<<<< HEAD
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
=======
        CacheControl cacheControl = CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic();
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCacheControl(cacheControl);
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCacheControl(cacheControl);
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/")
                .setCacheControl(cacheControl);
>>>>>>> d5851d3 (Initial commit)
    }
}
