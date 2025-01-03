package com.bluehospital.patient.patient.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {//it generates and validate the token

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    //generating the secure secrete key
    private static String SecretKeyGenerator(){
        SecureRandom secureRandom=new SecureRandom();
        byte[] key=new byte[32]; //256 bit key (32 byte)
        secureRandom.nextBytes(key);

        //Encode the byte array to base64 string
        String base64= Base64.getEncoder().encodeToString(key);

        logger.info("Generated secretkey is: "+base64);

        return base64;
    }

    private final String SECRET_KEY=SecretKeyGenerator();//secret key for jwt generation
    private final Long ACCESS_TOKEN_VALIDITY=5*60*1000l;//5min validity
    private final Long REFRESH_TOKEN_VALIDITY=30*60*1000l; //30 min validity

    //method to generate Access JWT Token
    public String generateAccessToken(String username){

        return Jwts.builder()
                .setSubject(username)//setting patient info in jwt payload
                .setIssuedAt(new Date())//issued date for access token
                .setExpiration(new Date(System.currentTimeMillis()+ACCESS_TOKEN_VALIDITY))//expiry data for token is 5 min
                .signWith(SignatureAlgorithm.HS256,SECRET_KEY) //signing algorithm for jwt creation
                .compact(); //builds and return a string

    }

    //method to generate Refresh JWT Token
    public String generateRefreshToken(String username){

        return Jwts.builder()
                .setSubject(username)//setting patient info in jwt payload
                .setIssuedAt(new Date())//issued date for refresh token
                .setExpiration(new Date(System.currentTimeMillis()+REFRESH_TOKEN_VALIDITY))//expiry data for refresh token is 30 min
                .signWith(SignatureAlgorithm.HS256,SECRET_KEY) //signing algorithm for jwt creation
                .compact(); //builds and return a string

    }

    //method to validate token
    public boolean validateToken(String token,String username){
        logger.info("JwtUtils: validating the token");
        String tokenUsername=extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));

    }

    //method to extract username from token
    public String extractUsername(String token){
        logger.info("JwtUtils: extracting username from token");
        Claims claims=Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
        logger.info("Token username= "+claims.getSubject());
        return claims.getSubject();
    }

    //method to check token expiry
    public boolean isTokenExpired(String token){
        logger.info("JwtUtils: checking token expiry");
        return extractExpiration(token).before(new Date());
    }

    //method to extract token expiration date
    public Date extractExpiration(String token){
        logger.info("JwtUtils: extracting the expiration date of token");
        Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
        logger.info("Token expiration Date= "+claims.getExpiration());
        return claims.getExpiration();
    }



}
