package com.bluehospital.patient.patient.filter;

import com.bluehospital.patient.patient.model.patient.Patient;
import com.bluehospital.patient.patient.model.hospital.Hospital;
import com.bluehospital.patient.patient.service.hospital.HospitalService;
import com.bluehospital.patient.patient.service.patient.PatientService;
import com.bluehospital.patient.patient.service.patient.PatientServiceImp;
import com.bluehospital.patient.patient.service.hospital.HospitalServiceImp;
import com.bluehospital.patient.patient.service.common.TokenBlacklistService;
import com.bluehospital.patient.patient.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUtils jwtUtils;
    private final PatientService patientService;
    private final HospitalService hospitalService;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtFilter(JwtUtils jwtUtils, PatientService patientService, HospitalService hospitalService, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtils = jwtUtils;
        this.patientService = patientService;
        this.hospitalService = hospitalService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        logger.info("JwtFilter: Starting token validation");

        // Check if the authorization header contains a Bearer token
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // Extract token by removing "Bearer " prefix
            if (!jwtUtils.isAccessToken(token) || tokenBlacklistService.isTokenBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Invalid, expired, or blacklisted access token");
                return;
            }
            username = jwtUtils.extractUsername(token);
        }

        // Validate token and set the security context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("JwtFilter: Validating the token for user {}", username);

            // Attempt to authenticate as a Patient
            Patient patient = patientService.loadPatientByUsername(username);
            if (patient != null && jwtUtils.validateToken(token, patient.getUsername())) {
                logger.info("JwtFilter: Valid token for patient");
                setAuthenticationContext(patient, request);
            } else {
                // Attempt to authenticate as a Hospital
                Hospital hospital = hospitalService.loadHospitalByUsername(username);
                if (hospital != null && jwtUtils.validateToken(token, hospital.getUsername())) {
                    logger.info("JwtFilter: Valid token for hospital");
                    setAuthenticationContext(hospital, request);
                } else {
                    logger.warn("JwtFilter: Token validation failed for user {}", username);
                }
            }
        }

        chain.doFilter(request, response); // Continue the filter chain
    }

    private void setAuthenticationContext(Object user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user, null, ((org.springframework.security.core.userdetails.UserDetails) user).getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        logger.info("JwtFilter: Successfully added authentication to the security context");
    }
}
