package com.bluehospital.patient.patient.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.time.Duration;

@Service
public class TokenBlacklistServiceImp implements TokenBlacklistService {

    private final RedisTemplate<String,Object> redisTemplate;
    private static final Logger logger= LoggerFactory.getLogger(TokenBlacklistServiceImp.class);

    public TokenBlacklistServiceImp(RedisTemplate<String,Object> redisTemplate){
        this.redisTemplate=redisTemplate;
    }

    //method to blacklist the token
    @Override
    public void blacklistToken(String token, Long expirationMillis){
        logger.info("TokenBlacklistServiceImp: blacklisting the given token");
        redisTemplate.opsForValue().set(token,"BLACKLISTED", Duration.ofMillis(expirationMillis));
    }

    //method to check is token is blacklisted or not
    @Override
    public boolean isTokenBlacklisted(String token){
        logger.info("TokenBlacklistServiceImp: Checking the given token is blacklisted or not!");
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }

}
