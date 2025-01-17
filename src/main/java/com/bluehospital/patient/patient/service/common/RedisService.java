package com.bluehospital.patient.patient.service.common;

import com.bluehospital.patient.patient.model.RedisEntity;
import org.springframework.stereotype.Service;

@Service
public interface RedisService {

    public void saveRedisEntity(RedisEntity redisEntity);
    public boolean findRedisEntityByKey(String key);
}
