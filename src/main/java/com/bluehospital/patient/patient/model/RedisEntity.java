package com.bluehospital.patient.patient.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("RedisEntity")
public class RedisEntity {
    @Id
    private Object Id;
    private String key;
    private String value;

    //getters and setters

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object getId() {
        return Id;
    }

    public void setId(Object id) {
        Id = id;
    }
}
