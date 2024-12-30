package com.bluehospital.patient.patient.config;

import com.bluehospital.patient.patient.filter.JwtFilter;
import com.bluehospital.patient.patient.model.Patient;
import com.bluehospital.patient.patient.repository.PatientRepository;
import com.bluehospital.patient.patient.service.PatientService;
import com.bluehospital.patient.patient.utils.JwtUtils;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PatientService patientService;
    private final JwtUtils jwtUtils;

    public SecurityConfig(PatientService patientService,JwtUtils jwtUtils){
        this.patientService=patientService;
        this.jwtUtils=jwtUtils;

    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtUtils,patientService);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(customeCsrf->customeCsrf.disable())
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/api/v1/public/patient/**").permitAll()
                        .requestMatchers("/api/v1/private/patient/**").hasAuthority("PATIENT")
                        .anyRequest().authenticated())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(basic->{})
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return username->{
            Patient patient= patientService.loadPatientByUsername(username);
            if(patient==null){
                throw new UsernameNotFoundException("User not found! "+username);
            }
            return patient;
        };
    }
}
