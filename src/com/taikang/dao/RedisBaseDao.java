package com.taikang.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by kiwi on 2016/12/28.
 */
@Repository
public class RedisBaseDao {

    @Autowired
    protected StringRedisTemplate redisTemplate;




    /**
     * 对单一键删除
     * @param key
     */
    public void del(final String key) {
        this.redisTemplate.delete(key);
    }

    /**
     * 对多键删除
     * @param keys
     */
    public void del(final String... keys) {
        List<String> keyList = Arrays.asList(keys);
        this.redisTemplate.delete(keyList);
    }

    /**
     * 设临时值，指定保留时间：以秒为单位
     * @param key
     * @param value
     * @param liveTime 秒
     */
    public void set(final String key, final String value, final long liveTime) {
        del(key);
        if (liveTime > 0) {
            this.redisTemplate.opsForValue().set(key, value, liveTime, TimeUnit.SECONDS);
        } else {
            this.redisTemplate.opsForValue().set(key, value);
        }
    }

    /**
     * 设永久值
     * @param key
     * @param value
     */
    public void set(final String key, final String value) {
        set(key, value, 0L);
    }

    public String get(final String key) {
        return this.redisTemplate.opsForValue().get(key);
    }

    /**
     * hash操作<br>
     * 获取集合中所有的键和值
     *
     * @param key 集合key
     * @return
     */
    public Map<String, Object> hgetAll(String key) {
        return redisTemplate.<String, Object> opsForHash().entries(key);
    }

    /**
     * hash操作<br>
     * 获取field对应的值
     *
     * @param key hash存放的键
     * @param field
     * @return field对应的值
     */
    public Object hget(String key, String field) {
        return redisTemplate.<String, Object> opsForHash().get(key, field);
    }

    /**
     * hash操作<br>
     * 多值获取
     *
     * @param key
     * @param fields 多值对应的集合
     * @return
     */
    public Collection<Object> hmget(String key, Collection<String> fields) {
        return redisTemplate.<String, Object> opsForHash().multiGet(key, fields);
    }

    /**
     * hash操作<br>
     * 设置field对应的值
     *
     * @param key
     * @param field
     * @param value
     */
    public void hset(String key, String field, Object value) {
        redisTemplate.<String, Object> opsForHash().put(key, field, value);
    }

    /**
     * hash操作<br>
     * 多值插入
     *
     * @param key
     * @param param 多值对象
     */
    public void hmset(String key, Map<String, Object> param) {
        redisTemplate.<String, Object> opsForHash().putAll(key, param);
    }

    /**
     * @param key
     * @return
     */
    public Long hlen(String key) {
        return redisTemplate.opsForHash().size(key);
    }





}
