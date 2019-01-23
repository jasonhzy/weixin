package com.hzy.redis;

import java.nio.charset.Charset;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;


public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private Class<T> clazz;

    public FastJsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }
    @Override
    public byte[] serialize(T t) throws SerializationException {
        byte[] target = new byte[0];
        if (t != null) {
            target = JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes();
        }
        return target;
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        T t = null;
        if (bytes != null && bytes.length >0){
            String json = new String(bytes, DEFAULT_CHARSET);
            ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
            try {
                t = JSON.parseObject(json,clazz);
            } catch (Exception ex) {
                throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
            }
        }
        return t;
    }
}
