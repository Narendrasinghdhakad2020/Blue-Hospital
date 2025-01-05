package com.bluehospital.patient.patient.service;

public interface TokenBlacklistService {

     void blacklistToken(String token,Long expirationMillis);
     boolean isTokenBlacklisted(String token);
}
