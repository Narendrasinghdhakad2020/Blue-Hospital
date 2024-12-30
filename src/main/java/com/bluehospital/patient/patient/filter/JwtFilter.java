package com.bluehospital.patient.patient.filter;

import com.bluehospital.patient.patient.model.Patient;
import com.bluehospital.patient.patient.service.PatientService;
import com.bluehospital.patient.patient.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    public final JwtUtils jwtUtils;
    private final PatientService patientService;

    public JwtFilter(JwtUtils jwtUtils,PatientService patientService){
        this.jwtUtils=jwtUtils;
        this.patientService=patientService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException{

        String authorizationHeader=request.getHeader("Authorization");//get authorizarion header
        String username=null;
        String token=null;

        System.out.println("Hello we are in JwtFilter! ");

         //check authorization header contains the bearer token
        if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")){
            token=authorizationHeader.substring(7);//setting the token of request to the token variable and removing the prefix Bearer from token
            username=jwtUtils.extractUsername(token);
        }

        //validating the token and set security context
        if(username!=null && SecurityContextHolder.getContext().getAuthentication() ==null){
            System.out.println("JwtFilter: validating the token");
            Patient patient=patientService.loadPatientByUsername(username);

            if(jwtUtils.validateToken(token,patient.getUsername())){
                System.out.println("JwtFilter: Successfully verified token");
                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(patient,null,patient.getAuthorities());
                System.out.println("JwtFilter: Auth Token ="+authToken);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);//set user authentication in security context
                System.out.println("JwtFilter: Successfully added the authentication in Security Context!");
            }

        }
        chain.doFilter(request,response); // continue the filter chain for every request
    }
}
