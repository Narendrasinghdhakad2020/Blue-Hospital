package com.bluehospital.patient.patient.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfigFilter {

    @Bean
    public CorsFilter corsFilter(){

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:5173"); //front end url which access the end points
        corsConfiguration.addAllowedMethod("*"); //allow all request to access the end points
        corsConfiguration.addAllowedHeader("*"); //allow all headers
        corsConfiguration.setAllowCredentials(true); //allow cookies if needed

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration); //apply to all end points
        return  new CorsFilter(source); //return new cors filter with source configuration
    }
}
