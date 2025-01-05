package com.bluehospital.patient.patient.filter;

import com.bluehospital.patient.patient.model.Patient;
import com.bluehospital.patient.patient.service.PatientServiceImp;
import com.bluehospital.patient.patient.service.TokenBlacklistService;
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

    private static final Logger logger= LoggerFactory.getLogger(JwtFilter.class);

    public final JwtUtils jwtUtils;
    private final PatientServiceImp patientService;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtFilter(JwtUtils jwtUtils, PatientServiceImp patientService, TokenBlacklistService tokenBlacklistService){
        this.jwtUtils=jwtUtils;
        this.patientService=patientService;
        this.tokenBlacklistService=tokenBlacklistService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException{

        String authorizationHeader=request.getHeader("Authorization");//get authorizarion header
        String username=null;
        String token=null;

        logger.info("Hello we are in JwtFilter! ");

         //check authorization header contains the bearer token
        if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")){
            token=authorizationHeader.substring(7);//setting the token of request to the token variable and removing the prefix Bearer from token
            if(!jwtUtils.isAccessToken(token) || tokenBlacklistService.isTokenBlacklisted(token)){ // to check the user is not trying to access using refresh token
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Invalid, expired or blacklisted access token");
                return;
            }
            username=jwtUtils.extractUsername(token);
        }

        //validating the token and set security context
        if(username!=null && SecurityContextHolder.getContext().getAuthentication() ==null){
            logger.info("JwtFilter: validating the token");
            Patient patient=patientService.loadPatientByUsername(username);

            if(jwtUtils.validateToken(token,patient.getUsername())){
               logger.info("JwtFilter: Successfully verified token");
                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(patient,null,patient.getAuthorities());
                logger.info("JwtFilter: Auth Token successfully authenticated");
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);//set user authentication in security context
                logger.info("JwtFilter: Successfully added the authentication in Security Context!");
            }

        }
        chain.doFilter(request,response); // continue the filter chain for every request
    }
}
