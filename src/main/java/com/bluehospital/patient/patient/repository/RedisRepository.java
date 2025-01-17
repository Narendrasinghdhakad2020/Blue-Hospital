package com.bluehospital.patient.patient.repository;

import com.bluehospital.patient.patient.model.RedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedisRepository extends CrudRepository<RedisEntity,Object> {
    public Optional<RedisEntity> findRedisEntityByKey(String key);
}
