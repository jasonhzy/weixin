package com.hzy.redis;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 存放key/value，expire为0时表示永久存放
     * 
     * @param key
     * @param value
     * @param seconds
     * @return 成功\失败
     */
    public Boolean set(String key, String value, int seconds) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        boolean res = true;
        try {
            if (seconds != 0) {
                operations.set(key, value, seconds, TimeUnit.SECONDS);
            } else {
                operations.set(key, value);
            }
        } catch (Exception e) {
            res = false;
            logger.error("error for set cache ", e);
        }
        return res;
    }

    /**
     * 删除指定的key,也可以传入一个包含key的数组
     * 
     * @param keys
     * @return 成功\失败
     */
    public Boolean delete(String... keys) {
		boolean res = true;
		List<String> list = Arrays.asList(keys);
		try {
			redisTemplate.delete(list);
		} catch (Exception e) {
			res = false;
			logger.error("error for delete cache ", e);
		}
        return res;
    }

    public String get(String key) {
		ValueOperations<String, String> operations = redisTemplate.opsForValue();
		String value = operations.get(key);
        return value;
    }

    /**
     * 获取key的剩余过期时间，单位：秒
     *
     * @param key
     * @return
     */
    public Long getExpireSeconds(String key) {
		long ttl = 0;
		try {
			ttl = redisTemplate.getExpire(key);
		} catch (Exception e) {
			logger.error("error for delete cache ", e);
		}
		return ttl;
    }

    /**
     * 判断key是否存在
     * 
     * @param key
     * key
     * @return true:存在，false:不存在
     */
    public Boolean exists(String key) {
		boolean existed = false;
		try {
			existed = redisTemplate.hasKey(key);
		} catch (Exception e) {
			existed = false;
			logger.error("error for connect redis ", e);
		}
		return existed;
    }

    /**
     * 去锁
     * 
     * @return
     */
    public Boolean getLock(String key) {
		ValueOperations<String, String> operations = redisTemplate.opsForValue();
		boolean lock = false;
		try {
			lock = operations.setIfAbsent(key + "_lock","");
		} catch (Exception e) {
			lock = false;
			logger.error("set lock error",e);
		}
		return lock;
    }

    public boolean expire(String key, int seconds) {
		boolean expired = false;
		try {
			expired = redisTemplate.expire(key,seconds, TimeUnit.SECONDS);
		} catch (Exception e) {
			expired = false;
			logger.error("expire key error",e);
		}
		return expired;
    }

    /**
     * 指定的 key 不存在时，为 key 设置指定的值。
     * 
     * @param key
     * key
     * @return true:存在，false:不存在
     */
    public Boolean setnx(String key, Object object, int seconds) {
		ValueOperations<String, String> operations = redisTemplate.opsForValue();
		boolean lock = false;
		try {
			lock = operations.setIfAbsent(key,object.toString());
			lock = redisTemplate.expire(key,seconds,TimeUnit.SECONDS);
		} catch (Exception e) {
			lock = false;
			logger.error("lock error",e);
		}
		return lock;
    }

    /**
     * 指定的 key 不存在时，为 key 设置指定的值。
     * 
     * @param key
     * key
     * @return true:存在，false:不存在
     */
    public Boolean setnx(String key, Object object) {
        return setnx(key, object, 30);
    }

    /**
     * 自增
     * 
     * @param key
     * key
     * @return 0:失败，非0:成功
     */
    public Long incr(String key) {
		return incr(key,1L);
    }

	public Long incr(String key,Long step){
		ValueOperations<String, String> operations = redisTemplate.opsForValue();
		long inc = 0;
		try {
			inc = operations.increment(key,step);
		} catch (Exception e) {
			inc = 0;
			logger.error("increment value error ",e);
		}
		return inc;
	}

    /**
     * 自减
     * 
     * @param key
     * key
     * @return 0:失败，非0:成功
     */
	public Long decr(String key){
		return incr(key,-1L);
	}

}
