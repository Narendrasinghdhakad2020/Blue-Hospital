package com.bluehospital.patient.patient.service.common;

import com.bluehospital.patient.patient.model.RedisEntity;
import com.bluehospital.patient.patient.repository.RedisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImp implements RedisService {

    private static final Logger logger=LoggerFactory.getLogger(RedisServiceImp.class);

    private final RedisRepository redisRepository;


    RedisServiceImp(RedisRepository redisRepository){
        this.redisRepository=redisRepository;
    }

    //method to save redisEntity
    @Override
    public void saveRedisEntity(RedisEntity redisEntity){
        redisRepository.save(redisEntity);
    }

    //method to find redisEntity
    @Override
    public boolean findRedisEntityByKey(String key){
        RedisEntity redisEntity=redisRepository.findRedisEntityByKey(key).orElse(null);
        if(redisEntity!=null){
            return true;
        }
        return false;

    }
}
