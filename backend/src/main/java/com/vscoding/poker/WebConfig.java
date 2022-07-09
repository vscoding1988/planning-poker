package com.vscoding.poker;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

  @Value("${application.cors.allowedOrigins:}")
  private List<String> allowedOrigins;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    if (!allowedOrigins.isEmpty()) {
      registry
          .addMapping("/**")
          .allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE", "PATCH")
          .allowedOrigins(allowedOrigins.toArray(new String[]{}));
    }
  }
}