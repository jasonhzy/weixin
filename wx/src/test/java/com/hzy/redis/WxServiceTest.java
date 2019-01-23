package com.hzy.redis;

import com.hzy.redis.constant.WxConstant;
import com.hzy.redis.service.WxService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WxServiceTest {

    @Autowired
    private WxService wxService;  //自动注入

    @Autowired
    private RedisService redisService;  //自动注入

    @Test
    public void testWxService(){
        String openid = null;
        try{
            openid = wxService.getOpenId("1234");
        }catch (Exception e){
            System.out.println(e.getMessage());;
        }
        Assert.assertNotNull(openid);
    }

    @Test
    public void testRedis(){
        String accessToken = null;
        try{
            accessToken = redisService.get(WxConstant.WX_ACCESS_TOKEN_CACHE_KEY);
        }catch (Exception e){
            System.out.println(e.getMessage());;
        }
        Assert.assertNotNull(accessToken);
    }
}
