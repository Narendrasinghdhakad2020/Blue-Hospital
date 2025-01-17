package com.bluehospital.patient.patient.config;

import com.bluehospital.patient.patient.filter.JwtFilter;
import com.bluehospital.patient.patient.model.hospital.Hospital;
import com.bluehospital.patient.patient.model.patient.Patient;
import com.bluehospital.patient.patient.service.hospital.HospitalService;
import com.bluehospital.patient.patient.service.hospital.HospitalServiceImp;
import com.bluehospital.patient.patient.service.patient.PatientServiceImp;
import com.bluehospital.patient.patient.service.common.TokenBlacklistService;
import com.bluehospital.patient.patient.utils.JwtUtils;
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

    private final PatientServiceImp patientService;
    private final HospitalService hospitalService;
    private final JwtUtils jwtUtils;
    private final TokenBlacklistService tokenBlacklistService;

    public SecurityConfig(PatientServiceImp patientService, JwtUtils jwtUtils,TokenBlacklistService tokenBlacklistService,HospitalService hospitalService){
        this.patientService=patientService;
        this.jwtUtils=jwtUtils;
        this.tokenBlacklistService=tokenBlacklistService;
        this.hospitalService=hospitalService;

    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtUtils,patientService,hospitalService,tokenBlacklistService);
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
                        .requestMatchers("/api/v1/public/**").permitAll()
                        .requestMatchers("/api/v1/private/patient/**").hasAuthority("PATIENT")
                        .requestMatchers("/api/v1/private/hospital/**").hasAuthority("HOSPITAL")
                        .requestMatchers("/doc").permitAll()
                        .anyRequest().authenticated()
                )
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
            // Try fetching a Patient
            Patient patient = patientService.loadPatientByUsername(username);
            if (patient != null) {
                return patient;
            }

            // Try fetching a Hospital
            Hospital hospital = hospitalService.loadHospitalByUsername(username);
            if (hospital != null) {
                return hospital;
            }

            // If neither is found, throw an exception
            throw new UsernameNotFoundException("User not found: " + username);
        };
    }
}
